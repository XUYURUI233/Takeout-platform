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

    private Long id;

    // 秒杀活动名称
    private String name;

    // 活动图片
    private String image;

    // 秒杀开始时间
    private LocalDateTime startTime;

    // 秒杀结束时间
    private LocalDateTime endTime;

    // 活动描述
    private String description;

    // 秒杀商品列表
    private List<SeckillGoodsDTO> goodsList;
}



