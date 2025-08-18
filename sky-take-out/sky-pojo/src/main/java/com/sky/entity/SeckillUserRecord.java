package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 秒杀用户记录实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillUserRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 活动ID
    private Long activityId;

    // 秒杀商品ID
    private Long seckillGoodsId;

    // 用户ID
    private Long userId;

    // 已购买数量
    private Integer quantity;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;
}