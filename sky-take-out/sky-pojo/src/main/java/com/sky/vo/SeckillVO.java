package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * ????VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillVO {

    private Long seckillId;

    private String seckillName;

    private String bannerImage;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;

    private Long remainingTime;
}

