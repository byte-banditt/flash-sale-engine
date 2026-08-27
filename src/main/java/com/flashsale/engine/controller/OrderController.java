package com.flashsale.engine.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flashsale.engine.dto.OrderRequest;
import com.flashsale.engine.dto.OrderResponse;
import com.flashsale.engine.service.InventoryService;

@RestController
@RequestMapping("/api/v1/orders")


public class OrderController {
    private final InventoryService inventoryService;
    
    public OrderController(InventoryService service){
        this.inventoryService = service;
    }

    @PostMapping("/init")
    public ResponseEntity<String> initStock(@RequestParam String productId, @RequestParam int stock) {
        
        inventoryService.initStock(productId, stock);
        
        return ResponseEntity.ok("Stock initialized for product " + productId + " to " + stock);
    }

    @PostMapping()
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request){
        Long productId = request.getProductId();
        int quantity = request.getQuantity();

        int code = inventoryService.reserveStock(String.valueOf(productId), quantity);

        if(code == 1){
            String orderId = UUID.randomUUID().toString();
            // ResponseEntity<String> entity ;
            // return ResponseEntity.status(HttpStatus.ACCEPTED)
            //         .body(new OrderResponse(orderId ,"PENDING" ,"Stock reserved successfully" ));
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(new OrderResponse(orderId, "PENDING", "Stock reserved successfully"));
        }
        else if( code == 0 ){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new OrderResponse(null, "OUT_OF_STOCK", "Stock not available"));
        }
        else{
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new OrderResponse(null , "NOT_FOUND" ,"Product does not exist." ));
        }
    }
}
