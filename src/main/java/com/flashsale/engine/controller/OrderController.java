package com.flashsale.engine.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flashsale.engine.config.RabbitMQConfig;
import com.flashsale.engine.dto.OrderRequest;
import com.flashsale.engine.dto.OrderResponse;
import com.flashsale.engine.entity.Order;
import com.flashsale.engine.repository.OrderRepository;
import com.flashsale.engine.service.InventoryService;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final InventoryService inventoryService;
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    public OrderController(InventoryService inventoryService,
                            OrderRepository orderRepository,
                            RabbitTemplate rabbitTemplate) {
        this.inventoryService = inventoryService;
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/init")
    public ResponseEntity<String> initStock(@RequestParam String productId, @RequestParam int stock) {
        inventoryService.initStock(productId, stock);
        return ResponseEntity.ok("Stock initialized for product " + productId + " to " + stock + '\n');
    }

    @PostMapping()
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request) {
        Long productId = request.getProductId();
        int quantity = request.getQuantity();
        String idempotencyKey = request.getIdempotencyKey();

        // 1. Idempotency check first , before touching Redis at all.
        Optional<Order> existing = orderRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            Order order = existing.get();
            return ResponseEntity.ok(
                    new OrderResponse(order.getOrderId(), order.getStatus(), "Order already processed\n"));
        }

        // 2. Reserve stock atomically in Redis.
        int code = inventoryService.reserveStock(String.valueOf(productId), quantity);

        if (code == 1) {
            String orderId = UUID.randomUUID().toString();
            request.setOrderId(orderId);

            try {
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.ORDER_EXCHANGE,
                        RabbitMQConfig.ORDER_ROUTING_KEY,
                        request);
            } catch (AmqpException e) {
                // Stock was reserved but the order never made it to the queue - give it back.
                inventoryService.rollbackStock(String.valueOf(productId), quantity);
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(new OrderResponse(null, "FAILED", "Could not queue order for processing, please retry\n"));
            }

            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(new OrderResponse(orderId, "PENDING", "Stock reserved successfully\n"));

        } else if (code == 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new OrderResponse(null, "OUT_OF_STOCK", "Stock not available\n"));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new OrderResponse(null, "NOT_FOUND", "Product does not exist.\n"));
        }
    }
}