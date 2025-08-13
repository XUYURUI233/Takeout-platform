package com.sky.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 秒杀活动DTO
 */
@Data
public class SeckillDTO {

    //秒杀活动名称
    private String seckillName;

    //秒杀活动描述
    private String seckillDescription;

    //开始时间
    private LocalDateTime startTime;

    //结束时间
    private LocalDateTime endTime;

    //横幅图片
    private String bannerImage;

    //状态 0-禁用 1-启用
    private Integer status;

    //秒杀商品列表
    private List<SeckillItemDTO> seckillItems;
}

