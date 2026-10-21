package com.flashsale.engine.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.flashsale.engine.dto.OrderRequest;
import com.flashsale.engine.dto.OrderResponse;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final com.flashsale.engine.service.OrderAcceptanceService acceptance;
    private final com.flashsale.engine.service.OrderQueryService queries;

    public OrderController(com.flashsale.engine.service.OrderAcceptanceService acceptance,
                           com.flashsale.engine.service.OrderQueryService queries) {
        this.acceptance = acceptance;
        this.queries = queries;
    }

    @PostMapping("/init")
    public ResponseEntity<String> initStock(@RequestParam long productId, @RequestParam int stock) {
        try {
            acceptance.initialize(productId, stock);
            return ResponseEntity.ok("Sale initialized\n");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PostMapping()
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request) {
        if (request.getIdempotencyKey() == null || request.getIdempotencyKey().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new OrderResponse(null, "INVALID_REQUEST", "idempotencyKey is required"));
        }
        if (request.getProductId() == null) {
            return ResponseEntity.badRequest()
                    .body(new OrderResponse(null, "INVALID_REQUEST", "productId is required"));
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            return ResponseEntity.badRequest()
                    .body(new OrderResponse(null, "INVALID_REQUEST", "quantity must be greater than zero"));
        }
        return acceptance.accept(request);
    }

    @GetMapping("/products/{productId}/stock")
    public ResponseEntity<?> stock(@PathVariable long productId) {
        return queries.stock(productId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new OrderResponse(null, "NOT_FOUND", "Sale not initialized")));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> orderStatus(@PathVariable String orderId) {
        return queries.order(orderId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new OrderResponse(orderId, "NOT_FOUND", "Order not found")));
    }
}
