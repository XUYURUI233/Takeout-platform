package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 秒杀库存操作日志表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillStockLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 秒杀商品ID
    private Long seckillGoodsId;

    // 用户ID
    private Long userId;

    // 订单ID
    private Long orderId;

    // 操作类型 1:扣减库存 2:释放库存 3:初始化库存
    private Integer operationType;

    // 操作数量
    private Integer quantity;

    // 操作前库存
    private Integer beforeStock;

    // 操作后库存
    private Integer afterStock;

    // 操作时的版本号
    private Integer version;

    // 备注
    private String remark;

    // 创建时间
    private LocalDateTime createTime;
}



