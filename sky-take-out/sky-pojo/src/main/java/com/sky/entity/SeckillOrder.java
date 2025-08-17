package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀订单表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 订单ID（关联orders表）
    private Long orderId;

    // 秒杀活动ID
    private Long activityId;

    // 秒杀商品ID
    private Long seckillGoodsId;

    // 用户ID
    private Long userId;

    // 购买数量
    private Integer quantity;

    // 秒杀价格
    private BigDecimal seckillPrice;

    // 订单总金额
    private BigDecimal totalAmount;

    // 支付状态 0:未支付 1:已支付 2:支付超时取消
    private Integer payStatus;

    // 支付超时时间
    private LocalDateTime payExpireTime;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;
}