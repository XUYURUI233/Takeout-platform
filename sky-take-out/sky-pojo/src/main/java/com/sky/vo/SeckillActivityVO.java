package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 秒杀活动VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillActivityVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 活动名称
    private String name;

    // 活动图片
    private String image;

    // 开始时间
    private LocalDateTime startTime;

    // 结束时间
    private LocalDateTime endTime;

    // 活动描述
    private String description;

    // 活动状态 0:未开始 1:进行中 2:已结束 3:已取消
    private Integer status;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;

    // 商品数量
    private Integer goodsCount;

    // 剩余时间（秒）
    private Long remainingTime;

    // 商品列表
    private List<SeckillGoodsVO> goodsList;
}