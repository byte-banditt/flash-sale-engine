package com.flashsale.engine.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.flashsale.engine.dto.OrderResponse;
import com.flashsale.engine.dto.StockResponse;

@Service
public class OrderQueryService {
    private final JdbcTemplate db;

    public OrderQueryService(JdbcTemplate db) {
        this.db = db;
    }

    public Optional<StockResponse> stock(long productId) {
        var rows = db.queryForList(
                "select available_stock from products where id=?", productId);
        if (rows.isEmpty()) return Optional.empty();
        return Optional.of(new StockResponse(productId,
                ((Number) rows.getFirst().get("available_stock")).intValue()));
    }

    public Optional<OrderResponse> order(String orderId) {
        var rows = db.queryForList(
                "select order_id,status from orders where order_id=?", orderId);
        if (rows.isEmpty()) return Optional.empty();
        Map<String, Object> row = rows.getFirst();
        return Optional.of(new OrderResponse(row.get("order_id").toString(),
                row.get("status").toString(), "Current order status"));
    }
}
