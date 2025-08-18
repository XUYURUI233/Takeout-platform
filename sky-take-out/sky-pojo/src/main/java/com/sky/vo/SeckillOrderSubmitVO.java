package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀订单提交结果VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrderSubmitVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 订单ID
    private Long orderId;

    // 订单号
    private String orderNumber;

    // 支付超时时间
    private LocalDateTime payExpireTime;

    // 订单总金额
    private BigDecimal totalAmount;

    // 支付时限（秒）
    private Integer payTimeLimit;
}