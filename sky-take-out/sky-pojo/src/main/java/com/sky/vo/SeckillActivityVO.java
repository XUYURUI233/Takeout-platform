package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ��ɱ�VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillActivityVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // �����
    private String name;

    // �ͼƬ
    private String image;

    // ��ʼʱ��
    private LocalDateTime startTime;

    // ����ʱ��
    private LocalDateTime endTime;

    // �����
    private String description;

    // �״̬ 0:δ��ʼ 1:������ 2:�ѽ��� 3:��ȡ��
    private Integer status;

    // ����ʱ��
    private LocalDateTime createTime;

    // ����ʱ��
    private LocalDateTime updateTime;

    // ��Ʒ����
    private Integer goodsCount;

    // ʣ��ʱ�䣨�룩
    private Long remainingTime;

    // ��Ʒ�б�
    private List<SeckillGoodsVO> goodsList;
}