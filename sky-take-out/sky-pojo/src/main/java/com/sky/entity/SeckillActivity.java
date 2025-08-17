package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 秒杀活动表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 秒杀活动名称
    private String name;

    // 活动图片
    private String image;

    // 秒杀开始时间
    private LocalDateTime startTime;

    // 秒杀结束时间
    private LocalDateTime endTime;

    // 活动状态 0:未开始 1:进行中 2:已结束 3:已取消
    private Integer status;

    // 活动描述
    private String description;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;

    // 创建人
    private Long createUser;

    // 修改人
    private Long updateUser;
}