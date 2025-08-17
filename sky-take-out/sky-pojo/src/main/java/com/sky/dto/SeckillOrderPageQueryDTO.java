package com.sky.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 秒杀订单分页查询DTO
 */
@Data
public class SeckillOrderPageQueryDTO implements Serializable {

    // 页码
    private int page;

    // 页面大小
    private int pageSize;

    // 订单号
    private String number;

    // 手机号
    private String phone;

    // 订单状态
    private Integer status;

    // 开始时间
    private LocalDateTime beginTime;

    // 结束时间
    private LocalDateTime endTime;
}



