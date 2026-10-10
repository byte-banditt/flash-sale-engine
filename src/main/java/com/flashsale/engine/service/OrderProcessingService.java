package com.flashsale.engine.service;

import com.flashsale.engine.dto.OrderRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderProcessingService {
    private final JdbcTemplate db;
    public OrderProcessingService(JdbcTemplate db) { this.db = db; }

    @Transactional
    public void processOrder(OrderRequest request) {
        var rows = db.queryForList("select * from orders where order_id=? for update", request.getOrderId());
        if (rows.isEmpty()) throw new IllegalArgumentException("No durable order intent for delivery");
        var order = rows.getFirst();
        if (!request.getIdempotencyKey().equals(order.get("idempotency_key"))
                || request.getProductId().longValue() != ((Number) order.get("product_id")).longValue()
                || request.getQuantity().intValue() != ((Number) order.get("quantity")).intValue())
            throw new IllegalArgumentException("Delivery does not match immutable order intent");
        // CONFIRMED and CANCELLED are terminal. This lock also fences reconciliation,
        // so a refund can never race a later transition to CONFIRMED.
        if (!"PENDING".equals(order.get("status"))) return;
        db.update("update orders set status='CONFIRMED' where order_id=?", request.getOrderId());
    }
}
