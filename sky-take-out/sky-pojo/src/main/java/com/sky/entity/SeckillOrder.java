package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀订单
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //订单号
    private String orderNumber;

    //秒杀活动ID
    private Long seckillId;

    //秒杀商品项ID
    private Long itemId;

    //用户ID
    private Long userId;

    //菜品ID
    private Long dishId;

    //菜品名称
    private String dishName;

    //购买数量
    private Integer quantity;

    //秒杀价格
    private BigDecimal seckillPrice;

    //总金额
    private BigDecimal totalAmount;

    //地址ID
    private Long addressId;

    //备注
    private String remark;

    //订单状态 1-待支付 2-已支付 3-已取消
    private Integer status;

    //支付方式 1-微信 2-支付宝
    private Integer payMethod;

    //支付时间
    private LocalDateTime payTime;

    //支付截止时间
    private LocalDateTime payDeadline;

    //创建时间
    private LocalDateTime createTime;

    //更新时间
    private LocalDateTime updateTime;
}

