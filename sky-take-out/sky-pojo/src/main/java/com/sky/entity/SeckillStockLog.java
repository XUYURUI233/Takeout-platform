package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 秒杀库存日志实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillStockLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作类型常量
     */
    public static final Integer DEDUCT_STOCK = 1;  // 扣减库存
    public static final Integer RELEASE_STOCK = 2; // 释放库存

    private Long id;

    // 秒杀商品ID
    private Long seckillGoodsId;

    // 操作类型 1:扣减 2:释放
    private Integer operationType;

    // 操作数量
    private Integer quantity;

    // 操作前库存
    private Integer beforeStock;

    // 操作后库存
    private Integer afterStock;

    // 关联订单ID
    private Long orderId;

    // 操作描述
    private String description;

    // 创建时间
    private LocalDateTime createTime;
}