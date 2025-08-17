package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀订单VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrderVO implements Serializable {

    // 订单ID
    private Long id;

    // 订单号
    private String number;

    // 用户ID
    private Long userId;

    // 下单时间
    private LocalDateTime orderTime;

    // 结账时间
    private LocalDateTime checkoutTime;

    // 支付方式
    private Integer payMethod;

    // 支付状态
    private Integer payStatus;

    // 订单金额
    private BigDecimal amount;

    // 备注
    private String remark;

    // 手机号
    private String phone;

    // 地址
    private String address;

    // 收货人
    private String consignee;

    // 预计送达时间
    private LocalDateTime estimatedDeliveryTime;

    // 秒杀信息
    private SeckillInfo seckillInfo;

    // 秒杀信息内部类
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeckillInfo implements Serializable {
        
        // 活动ID
        private Long activityId;
        
        // 活动名称
        private String activityName;
        
        // 秒杀商品ID
        private Long seckillGoodsId;
        
        // 商品名称
        private String goodsName;
        
        // 商品图片
        private String goodsImage;
        
        // 原价
        private BigDecimal originalPrice;
        
        // 秒杀价
        private BigDecimal seckillPrice;
        
        // 购买数量
        private Integer quantity;
    }
}