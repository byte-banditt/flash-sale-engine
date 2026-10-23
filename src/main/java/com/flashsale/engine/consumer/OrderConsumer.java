package com.flashsale.engine.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.flashsale.engine.config.KafkaConfig;
import com.flashsale.engine.dto.OrderRequest;
import com.flashsale.engine.service.OrderProcessingService;

@Component
public class OrderConsumer {

    private final OrderProcessingService orderProcessingService;

    public OrderConsumer(OrderProcessingService orderProcessingService) {
        this.orderProcessingService = orderProcessingService;
    }

    @KafkaListener(topics = KafkaConfig.ORDERS_TOPIC, containerFactory = "kafkaListenerContainerFactory")
    public void handleOrder(OrderRequest request) {
        orderProcessingService.processOrder(request);
        // Let failures propagate to DefaultErrorHandler.
    }
}
