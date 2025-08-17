package com.sky.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 秒杀活动分页查询DTO
 */
@Data
public class SeckillActivityPageQueryDTO implements Serializable {

    // 页码
    private int page;

    // 页面大小
    private int pageSize;

    // 活动名称
    private String name;

    // 活动状态
    private Integer status;

    // 开始时间
    private LocalDateTime startTime;

    // 结束时间
    private LocalDateTime endTime;
}



