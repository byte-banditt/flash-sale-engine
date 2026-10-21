package com.flashsale.engine;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.flashsale.engine.entity.OrderStatus;

class OrderStatusTests {
    @Test
    void pendingMayConfirmOrCancel() {
        assertTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.CONFIRMED));
        assertTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.CANCELLED));
    }

    @Test
    void terminalStatesCannotTransition() {
        for (var terminal : new OrderStatus[] { OrderStatus.CONFIRMED, OrderStatus.CANCELLED }) {
            for (var next : OrderStatus.values()) {
                assertFalse(terminal.canTransitionTo(next));
            }
        }
    }

    @Test
    void pendingCannotRemainPending() {
        assertFalse(OrderStatus.PENDING.canTransitionTo(OrderStatus.PENDING));
    }
}
