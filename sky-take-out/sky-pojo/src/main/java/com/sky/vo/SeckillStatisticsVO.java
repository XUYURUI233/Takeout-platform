package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 秒杀活动统计VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillStatisticsVO implements Serializable {

    // 总订单数
    private Integer totalOrders;

    // 成功订单数
    private Integer successOrders;

    // 总金额
    private BigDecimal totalAmount;

    // 总库存
    private Integer totalStock;

    // 已售数量
    private Integer soldCount;

    // 成功率
    private Double successRate;
}



