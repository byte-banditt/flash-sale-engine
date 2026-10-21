package com.flashsale.engine.entity;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum OrderStatus {
    PENDING, CONFIRMED, CANCELLED;

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = allowed();

    private static Map<OrderStatus, Set<OrderStatus>> allowed() {
        var table = new EnumMap<OrderStatus, Set<OrderStatus>>(OrderStatus.class);
        table.put(PENDING, EnumSet.of(CONFIRMED, CANCELLED));
        table.put(CONFIRMED, EnumSet.noneOf(OrderStatus.class));
        table.put(CANCELLED, EnumSet.noneOf(OrderStatus.class));
        return Map.copyOf(table);
    }

    public boolean canTransitionTo(OrderStatus next) {
        return ALLOWED.get(this).contains(next);
    }
}
