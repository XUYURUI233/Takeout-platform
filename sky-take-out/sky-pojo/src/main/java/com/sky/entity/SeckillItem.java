package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀商品项
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //秒杀活动ID
    private Long seckillId;

    //菜品ID
    private Long dishId;

    //菜品名称
    private String dishName;

    //原价
    private BigDecimal originalPrice;

    //秒杀价
    private BigDecimal seckillPrice;

    //秒杀库存
    private Integer seckillStock;

    //当前库存
    private Integer currentStock;

    //每人限购数量
    private Integer limitPerUser;

    //状态 0-禁用 1-启用
    private Integer status;

    //创建时间
    private LocalDateTime createTime;

    //更新时间
    private LocalDateTime updateTime;
}

