package com.flashsale.engine.service;

// import java.util.Collection;
import java.util.Collections;
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
    
    public int reserveStock(String productId , int quantity){
        Long result = redisTemplate.execute(
            stockDecrementScript,
            Collections.singletonList("product:" + productId + ":stock"),
            String.valueOf(quantity)
        );

        if(result == null) return -1;
        return result.intValue();
    }

    public void rollbackStock(String productId, int quantity){
        redisTemplate.opsForValue().increment("product:" + productId +  ":stock" , quantity);
    }

    public boolean rollbackStockOnce(String orderId, String productId, int quantity) {
        Long result = redisTemplate.execute(rollbackOnceScript,
                List.of("product:" + productId + ":stock", "order:" + orderId + ":rolled-back"),
                String.valueOf(quantity));
        if (result == null) {
            throw new IllegalStateException("Redis returned no rollback result");
        }
        return result == 1;
    }
}
