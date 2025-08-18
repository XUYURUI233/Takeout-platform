package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ��ɱ�û���¼ʵ����
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillUserRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // �ID
    private Long activityId;

    // ��ɱ��ƷID
    private Long seckillGoodsId;

    // �û�ID
    private Long userId;

    // �ѹ�������
    private Integer quantity;

    // ����ʱ��
    private LocalDateTime createTime;

    // ����ʱ��
    private LocalDateTime updateTime;
}