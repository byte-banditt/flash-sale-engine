package com.flashsale.engine.service;

// import java.util.Collection;
import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

// import tools.jackson.databind.node.StringNode;

@Service
public class InventoryService {
    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> stockDecrementScript;
    private final DefaultRedisScript<Long> rollbackOnceScript;
    
    public InventoryService(StringRedisTemplate redisTemplate, DefaultRedisScript<Long> stockDecrementScript,
                            DefaultRedisScript<Long> rollbackOnceScript){
        this.redisTemplate = redisTemplate;
        this.stockDecrementScript = stockDecrementScript;
        this.rollbackOnceScript = rollbackOnceScript;

    }
    
    public void initStock(String productId , int initialStock){
        String key = "product:" + productId + ":stock";
        
        redisTemplate.opsForValue().set(key , String.valueOf(initialStock));

    }
    
    public int reserveStock(String productId, int quantity, String idempotencyKey, String orderId){
        Long result = redisTemplate.execute(
            stockDecrementScript,
            List.of("product:" + productId + ":stock", "idempotency:" + idempotencyKey),
            String.valueOf(quantity), orderId
        );

        if(result == null) return -1;
        return result.intValue();
    }

    public String reservedOrderId(String idempotencyKey) {
        return redisTemplate.opsForValue().get("idempotency:" + idempotencyKey);
    }

    public void rollbackStock(String productId, int quantity){
        redisTemplate.opsForValue().increment("product:" + productId +  ":stock" , quantity);
    }

    public boolean rollbackStockOnce(String orderId, String productId, int quantity) {
        return rollbackStockOnce(orderId, productId, quantity, null);
    }

    public boolean rollbackStockOnce(String orderId, String productId, int quantity,
                                     String idempotencyKey) {
        List<String> keys = idempotencyKey == null
                ? List.of("product:" + productId + ":stock", "order:" + orderId + ":rolled-back")
                : List.of("product:" + productId + ":stock", "order:" + orderId + ":rolled-back",
                          "idempotency:" + idempotencyKey);
        Long result = redisTemplate.execute(rollbackOnceScript,
                keys, String.valueOf(quantity), orderId);
        if (result == null) {
            throw new IllegalStateException("Redis returned no rollback result");
        }
        return result == 1;
    }
}
