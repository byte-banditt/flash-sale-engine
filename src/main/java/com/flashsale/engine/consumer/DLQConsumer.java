package com.flashsale.engine.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.flashsale.engine.config.RabbitMQConfig;
import com.flashsale.engine.dto.OrderRequest;
import com.flashsale.engine.service.InventoryService;

@Component
public class DLQConsumer {

    private static final Logger log = LoggerFactory.getLogger(DLQConsumer.class);

    private final InventoryService inventoryService;

    public DLQConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @RabbitListener(
            queues = RabbitMQConfig.DLQ_QUEUE,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleFailedOrder(OrderRequest request) {
        try {
            boolean restored = inventoryService.rollbackStockOnce(
                    request.getOrderId(),
                    String.valueOf(request.getProductId()),
                    request.getQuantity());

            log.error("Order processing permanently failed after retries. Stock rollback applied={}. " +
                            "orderId={}, productId={}, quantity={}, idempotencyKey={}",
                    restored, request.getOrderId(), request.getProductId(),
                    request.getQuantity(), request.getIdempotencyKey());

        } catch (Exception e) {
            log.error("CRITICAL: failed to roll back stock for a dead-lettered order. " +
                            "orderId={}, productId={}, quantity={}",
                    request.getOrderId(), request.getProductId(), request.getQuantity(), e);
            throw e;
        }
    }
}
