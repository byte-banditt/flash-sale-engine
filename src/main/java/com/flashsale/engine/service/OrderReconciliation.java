package com.flashsale.engine.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderReconciliation {
    private final JdbcTemplate db;
    private final long pendingTimeoutMs;
    public OrderReconciliation(JdbcTemplate db,
                               @Value("${flashsale.outbox.pending-timeout-ms:300000}") long pendingTimeoutMs) {
        if (pendingTimeoutMs <= 0) throw new IllegalArgumentException("Pending timeout must be positive");
        this.db = db; this.pendingTimeoutMs = pendingTimeoutMs;
    }
    @Transactional
    public int sweep() {
        var stale = db.queryForList("""
                select order_id,product_id,quantity from orders
                where status='PENDING' and created_at < current_timestamp - (? * interval '1 millisecond')
                order by created_at,order_id limit 32 for update skip locked
                """, pendingTimeoutMs);
        for (var order : stale) {
            // This locked PostgreSQL row IS the durable consumer-side outcome.
            // Kafka cannot prove absence of an individual in-flight message.
            // Consumer and sweep lock the same row: either CONFIRMED already won,
            // or cancellation wins and fences all late deliveries before refund.
            // Thus no speculative refund can later coexist with CONFIRMED.
            db.update("update orders set status='CANCELLED' where order_id=?", order.get("order_id"));
            db.update("update products set available_stock=available_stock+? where id=?",
                    order.get("quantity"), order.get("product_id"));
            db.update("update outbox_events set status='FAILED' where order_id=?", order.get("order_id"));
        }
        return stale.size();
    }
}
