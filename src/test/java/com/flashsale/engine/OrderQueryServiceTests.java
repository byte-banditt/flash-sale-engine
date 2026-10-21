package com.flashsale.engine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import com.flashsale.engine.service.OrderQueryService;

class OrderQueryServiceTests {
    private final JdbcTemplate db = mock(JdbcTemplate.class);
    private final OrderQueryService queries = new OrderQueryService(db);

    @Test
    void returnsCurrentStock() {
        when(db.queryForList(anyString(), anyLong()))
                .thenReturn(List.of(Map.of("available_stock", 7)));

        var result = queries.stock(101L).orElseThrow();

        assertEquals(101L, result.productId());
        assertEquals(7, result.availableStock());
    }

    @Test
    void missingSaleHasNoStock() {
        when(db.queryForList(anyString(), anyLong())).thenReturn(List.of());

        assertTrue(queries.stock(101L).isEmpty());
    }

    @Test
    void returnsOrderStatus() {
        when(db.queryForList(anyString(), anyString()))
                .thenReturn(List.of(Map.of("order_id", "o-1", "status", "PENDING")));

        var result = queries.order("o-1").orElseThrow();

        assertEquals("o-1", result.getOrderId());
        assertEquals("PENDING", result.getStatus());
    }

    @Test
    void missingOrderHasNoStatus() {
        when(db.queryForList(anyString(), anyString())).thenReturn(List.of());

        assertTrue(queries.order("missing").isEmpty());
    }
}
