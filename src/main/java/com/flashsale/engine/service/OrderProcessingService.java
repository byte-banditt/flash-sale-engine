package com.flashsale.engine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flashsale.engine.dto.OrderRequest;
import com.flashsale.engine.entity.Order;
import com.flashsale.engine.repository.OrderRepository;

@Service
public class OrderProcessingService {
    private static final Logger log = LoggerFactory.getLogger(OrderProcessingService.class);

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
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.debug("ORDER_TRACE event=PERSIST_COMMIT key={} orderId={} epochMs={}",
                        request.getIdempotencyKey(), request.getOrderId(), System.currentTimeMillis());
            }
        });
        // No try/catch — a real failure should propagate to the listener
        // container so the retry interceptor / DLQ can act on it.
    }
}