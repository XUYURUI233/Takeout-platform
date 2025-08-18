package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Seckill Order VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrderVO implements Serializable {

    // Order ID
    private Long id;

    // Order number
    private String number;

    // User ID
    private Long userId;

    // Order time
    private LocalDateTime orderTime;

    // Checkout time
    private LocalDateTime checkoutTime;

    // Payment method
    private Integer payMethod;

    // Payment status
    private Integer payStatus;

    // Order amount
    private BigDecimal amount;

    // Remark
    private String remark;

    // Phone number
    private String phone;

    // Address
    private String address;

    // Consignee
    private String consignee;

    // Estimated delivery time
    private LocalDateTime estimatedDeliveryTime;

    // Seckill info
    private SeckillInfo seckillInfo;

    // Seckill info inner class
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeckillInfo implements Serializable {
        
        // Activity ID
        private Long activityId;
        
        // Activity name
        private String activityName;
        
        // Seckill goods ID
        private Long seckillGoodsId;
        
        // Goods name
        private String goodsName;
        
        // Goods image
        private String goodsImage;
        
        // Original price
        private BigDecimal originalPrice;
        
        // Seckill price
        private BigDecimal seckillPrice;
        
        // Quantity
        private Integer quantity;
    }
}