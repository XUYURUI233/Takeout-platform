package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 秒杀活动
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seckill implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

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

    //创建时间
    private LocalDateTime createTime;

    //更新时间
    private LocalDateTime updateTime;

    //创建人
    private Long createUser;

    //更新人
    private Long updateUser;
}

