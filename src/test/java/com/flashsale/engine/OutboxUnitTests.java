package com.flashsale.engine;

import com.flashsale.engine.dto.OrderRequest;
import com.flashsale.engine.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.concurrent.CompletableFuture;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OutboxUnitTests {
    @Test void acceptanceCreatesInventoryOrderAndOutboxIntent() {
        var db = mock(JdbcTemplate.class);
        when(db.queryForList(startsWith("select order_id,status"), any(Object[].class))).thenReturn(List.of());
        when(db.queryForList(startsWith("select available_stock"), any(Object[].class)))
                .thenReturn(List.of(Map.of("available_stock", 1)));
        var result = new OrderAcceptanceService(db).accept(new OrderRequest(null, 101L, 1, "unit-key"));
        assertEquals(202, result.getStatusCode().value());
        assertNotNull(result.getBody().getOrderId());
        var sequence = inOrder(db);
        sequence.verify(db).update(startsWith("update products"), any(Object[].class));
        sequence.verify(db).update(contains("insert into orders"), any(Object[].class));
        sequence.verify(db).update(contains("insert into outbox_events"), any(Object[].class));
    }

    private Map<String,Object> event() {
        return Map.of("event_id", UUID.randomUUID(), "order_id", "order", "product_id", "101",
                "quantity", "1", "idempotency_key", "unit-key");
    }
    @Test void relayMarksPublishedOnlyAfterAck() {
        var db = mock(JdbcTemplate.class); var publisher = mock(KafkaTemplate.class); var event = event();
        when(db.queryForList(anyString(), any(Object[].class))).thenReturn(List.of(event));
        when(publisher.send(anyString(), anyString(), any(OrderRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(null));
        new OutboxRelay(db, publisher, 1).publishBatch();
        var sequence = inOrder(db, publisher);
        sequence.verify(publisher).send(anyString(), anyString(), any(OrderRequest.class));
        sequence.verify(db).update(startsWith("update outbox_events"), eq(event.get("event_id")));
    }
    @Test void failedConfirmRemainsPendingAndNeverRefunds() {
        var db = mock(JdbcTemplate.class); var publisher = mock(KafkaTemplate.class);
        when(db.queryForList(anyString(), any(Object[].class))).thenReturn(List.of(event()));
        when(publisher.send(anyString(), anyString(), any(OrderRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("missing ack")));
        new OutboxRelay(db, publisher, 1).publishBatch();
        verify(db, never()).update(anyString(), any(Object[].class));
    }
    @Test void sweepRefundsOnlyLockedStaleIntent() {
        var db = mock(JdbcTemplate.class);
        when(db.queryForList(anyString(), any(Object[].class))).thenReturn(List.of(
                Map.of("order_id", "order", "product_id", 101L, "quantity", 1)));
        assertEquals(1, new OrderReconciliation(db, 1).sweep());
        var sequence = inOrder(db);
        sequence.verify(db).update(startsWith("update orders set status='CANCELLED'"), eq("order"));
        sequence.verify(db).update(startsWith("update products"), eq(1), eq(101L));
        sequence.verify(db).update(startsWith("update outbox_events set status='FAILED'"), eq("order"));
    }
    @Test void sweepDoesNothingWhenNoPendingOrder() {
        var db = mock(JdbcTemplate.class);
        when(db.queryForList(anyString(), any(Object[].class))).thenReturn(List.of());
        assertEquals(0, new OrderReconciliation(db, 1).sweep());
        verify(db, never()).update(anyString(), any(Object[].class));
    }
}
