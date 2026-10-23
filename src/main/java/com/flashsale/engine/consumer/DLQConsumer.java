package com.flashsale.engine.consumer;

import com.flashsale.engine.config.KafkaConfig;
import com.flashsale.engine.dto.OrderRequest;
import com.flashsale.engine.service.OrderProcessingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DLQConsumer {
    private final OrderProcessingService processing;
    private static final Logger log = LoggerFactory.getLogger(DLQConsumer.class);
    public DLQConsumer(OrderProcessingService processing) { this.processing = processing; }
    @KafkaListener(id="orders-dlq-consumer", topics=KafkaConfig.DLQ_TOPIC,
            containerFactory="kafkaListenerContainerFactory")
    public void handleFailedOrder(OrderRequest request) {
        // Retry the durable intent; only the scheduled sweep may cancel/refund.
        try {
            processing.processOrder(request);
        } catch (RuntimeException failure) {
            log.error("Terminal DLQ processing failed for orderId={}", request.getOrderId(), failure);
        }
    }
}
