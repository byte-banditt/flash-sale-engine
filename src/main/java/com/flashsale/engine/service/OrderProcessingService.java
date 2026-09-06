package com.flashsale.engine.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flashsale.engine.dto.OrderRequest;
import com.flashsale.engine.entity.Order;
import com.flashsale.engine.repository.OrderRepository;

@Service
public class OrderProcessingService {

    private final OrderRepository orderRepository;

    public OrderProcessingService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void processOrder(OrderRequest request) {
        boolean alreadyProcessed = orderRepository
                .findByIdempotencyKey(request.getIdempotencyKey())
                .isPresent();

        if (alreadyProcessed) {
            return; // duplicate delivery — not an error
        }

        Order order = new Order();
        order.setOrderId(request.getOrderId());
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setIdempotencyKey(request.getIdempotencyKey());
        order.setStatus("CONFIRMED");

        orderRepository.save(order);
        // No try/catch — a real failure should propagate to the listener
        // container so the retry interceptor / DLQ can act on it.
    }
}