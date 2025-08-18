package com.sky.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 秒杀活动DTO
 */
@Data
public class SeckillActivityDTO implements Serializable {

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

    // 商品列表
    private List<SeckillGoodsDTO> goodsList;
}