package com.flashsale.engine.service;

import java.util.UUID;
import com.flashsale.engine.dto.OrderRequest;
import com.flashsale.engine.dto.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderAcceptanceService {
    private final JdbcTemplate db;
    public OrderAcceptanceService(JdbcTemplate db) { this.db = db; }

    @Transactional
    public void initialize(long productId, int stock) {
        db.queryForList("select pg_advisory_xact_lock(?)", productId);
        if (stock < 0) throw new IllegalArgumentException("Stock must be nonnegative");
        db.queryForList("select id from products where id=? for update", productId);
        // Initialization cannot erase live reservations or already consumed stock.
        if (db.queryForObject("select count(*) from orders where product_id=?", Long.class, productId) != 0)
            throw new IllegalStateException("Clear the sale's orders before starting a new sale");
        db.update("""
                insert into products(id,name,total_stock,available_stock,sale_started_at)
                values (?, ?, ?, ?, current_timestamp)
                on conflict(id) do update set total_stock=excluded.total_stock,
                  available_stock=excluded.available_stock,sale_started_at=excluded.sale_started_at
                """, productId, "Sale " + productId, stock, stock);
    }

    @Transactional
    public ResponseEntity<OrderResponse> accept(OrderRequest request) {
        // One PostgreSQL transaction owns inventory, order intent, and delivery intent.
        // Redis is deliberately absent: compensating a Redis decrement after commit
        // would recreate the crash window this outbox is intended to remove.
        // The advisory lock serializes the same idempotency key across products/replicas;
        // the product row lock serializes stock checks, including sweep refunds.
        db.queryForList("select pg_advisory_xact_lock(hashtextextended(?, 0))", request.getIdempotencyKey());
        var prior = db.queryForList("select order_id,status from orders where idempotency_key=?",
                request.getIdempotencyKey());
        if (!prior.isEmpty()) return ResponseEntity.ok(new OrderResponse(
                prior.getFirst().get("order_id").toString(), prior.getFirst().get("status").toString(), "Order already accepted"));
        var products = db.queryForList("select available_stock from products where id=? for update", request.getProductId());
        if (products.isEmpty()) return ResponseEntity.status(409).body(new OrderResponse(null,"NOT_FOUND","Sale not initialized"));
        if (((Number) products.getFirst().get("available_stock")).intValue() < request.getQuantity())
            return ResponseEntity.status(409).body(new OrderResponse(null,"OUT_OF_STOCK","Stock not available"));
        String orderId = UUID.randomUUID().toString();
        UUID eventId = UUID.randomUUID();
        db.update("update products set available_stock=available_stock-? where id=?", request.getQuantity(), request.getProductId());
        db.update("""
                insert into orders(order_id,product_id,quantity,idempotency_key,status,created_at)
                values (?,?,?,?,'PENDING',current_timestamp)
                """, orderId, request.getProductId(), request.getQuantity(), request.getIdempotencyKey());
        db.update("""
                insert into outbox_events(event_id,order_id,event_type,payload,status,created_at)
                values (?,?,'ORDER_CREATED',jsonb_build_object('orderId',?::text,'productId',?::bigint,
                  'quantity',?::integer,'idempotencyKey',?::text),'PENDING',current_timestamp)
                """, eventId, orderId, orderId, request.getProductId(), request.getQuantity(), request.getIdempotencyKey());
        return ResponseEntity.status(202).body(new OrderResponse(orderId,"PENDING","Order and delivery intent committed"));
    }
}
