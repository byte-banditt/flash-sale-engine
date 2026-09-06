package com.flashsale.engine.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.flashsale.engine.config.RabbitMQConfig;
import com.flashsale.engine.dto.OrderRequest;
import com.flashsale.engine.service.OrderProcessingService;

@Component
public class OrderConsumer {

    private final OrderProcessingService orderProcessingService;

    public OrderConsumer(OrderProcessingService orderProcessingService) {
        this.orderProcessingService = orderProcessingService;
    }

    @RabbitListener(
            queues = RabbitMQConfig.ORDER_QUEUE,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleOrder(OrderRequest request) {
        orderProcessingService.processOrder(request);
        // No try/catch: let failures propagate so the retry interceptor
        // and RepublishMessageRecoverer on rabbitListenerContainerFactory
        // can do their job.
    }
}