package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 秒杀活动实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动状态常量
     */
    public static final Integer NOT_STARTED = 0; // 未开始
    public static final Integer ACTIVE = 1;      // 进行中
    public static final Integer ENDED = 2;       // 已结束
    public static final Integer CANCELLED = 3;   // 已取消

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

    // 创建人
    private Long createUser;

    // 更新人
    private Long updateUser;
}