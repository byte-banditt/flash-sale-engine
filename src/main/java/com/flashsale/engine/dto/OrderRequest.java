package com.flashsale.engine.dto;

public class OrderRequest {

    private Long productId;
    private Integer quantity;
    private String idempotencyKey;
    private String orderId;

    public OrderRequest() {}

    public OrderRequest(String orderId, Long productId, Integer quantity, String idempotencyKey) {
        this.productId = productId;
        this.quantity = quantity;
        this.idempotencyKey = idempotencyKey;
        this.orderId = orderId;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}