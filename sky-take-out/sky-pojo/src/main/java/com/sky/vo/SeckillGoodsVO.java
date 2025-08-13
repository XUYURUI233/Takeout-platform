package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * ????VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillGoodsVO {

    private Long seckillId;

    private Long itemId;

    private Long dishId;

    private String dishName;

    private BigDecimal originalPrice;

    private BigDecimal seckillPrice;

    private Integer seckillStock;

    private Integer currentStock;

    private Integer limitPerUser;

    private Integer userBoughtCount;

    private Long remainingTime;
}

