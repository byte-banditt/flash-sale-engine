package com.flashsale.engine.consumer;

import com.flashsale.engine.config.RabbitMQConfig;
import com.flashsale.engine.dto.OrderRequest;
import com.flashsale.engine.service.OrderProcessingService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DLQConsumer {
    private final OrderProcessingService processing;
    public DLQConsumer(OrderProcessingService processing) { this.processing = processing; }
    @RabbitListener(queues=RabbitMQConfig.DLQ_QUEUE, containerFactory="rabbitListenerContainerFactory")
    public void handleFailedOrder(OrderRequest request) {
        // Retry the durable intent; only the scheduled sweep may cancel/refund.
        // A DB error propagates rather than acknowledging an unresolved delivery.
        processing.processOrder(request);
    }
}
