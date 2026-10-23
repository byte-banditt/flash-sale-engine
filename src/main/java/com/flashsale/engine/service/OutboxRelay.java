package com.flashsale.engine.service;

import com.flashsale.engine.config.KafkaConfig;
import com.flashsale.engine.dto.OrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class OutboxRelay {
    private static final Logger log = LoggerFactory.getLogger(OutboxRelay.class);
    private final JdbcTemplate db;
    private final KafkaTemplate<String, OrderRequest> publisher;
    private final int batchSize;
    @Value("${flashsale.kafka.delivery-timeout-ms:10000}")
    private long deliveryTimeoutMillis;

    public OutboxRelay(JdbcTemplate db, KafkaTemplate<String, OrderRequest> publisher,
                       @Value("${flashsale.outbox.batch-size:32}") int batchSize) {
        this.db = db; this.publisher = publisher; this.batchSize = batchSize;
    }

    @Transactional
    public void publishBatch() {
        var events = db.queryForList("""
                select event_id, order_id, payload->>'productId' as product_id,
                  payload->>'quantity' as quantity, payload->>'idempotencyKey' as idempotency_key
                from outbox_events where status='PENDING'
                order by created_at,event_id limit ? for update skip locked
                """, batchSize);
        for (var event : events) {
            var request = new OrderRequest(event.get("order_id").toString(),
                    Long.valueOf(event.get("product_id").toString()), Integer.valueOf(event.get("quantity").toString()),
                    event.get("idempotency_key").toString());
            try {
                publisher.send(KafkaConfig.ORDERS_TOPIC, request.getOrderId(), request)
                        .get(deliveryTimeoutMillis, TimeUnit.MILLISECONDS);
                log.debug("OUTBOX_ACK eventId={} orderId={}", event.get("event_id"), request.getOrderId());
                // Broker ack and this DB update are not atomic. A crash can republish;
                // consumer row locks and terminal status make those retries harmless.
                db.update("update outbox_events set status='PUBLISHED',published_at=current_timestamp where event_id=?",
                        event.get("event_id"));
            } catch (Exception failure) {
                log.warn("Outbox publish pending retry eventId={} orderId={} cause={}",
                        event.get("event_id"), request.getOrderId(), failure.toString());
                // An absent confirm is not proof of absent delivery. Never refund here.
                // Stop the batch on broker failure; release all locks promptly for recovery.
                break;
            }
        }
    }
}
