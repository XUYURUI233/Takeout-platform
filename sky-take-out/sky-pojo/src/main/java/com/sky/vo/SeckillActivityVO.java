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

    private Long id;

    // ��ɱ�����
    private String name;

    // �ͼƬ
    private String image;

    // ��ɱ��ʼʱ��
    private LocalDateTime startTime;

    // ��ɱ����ʱ��
    private LocalDateTime endTime;

    // �״̬ 0:δ��ʼ 1:������ 2:�ѽ��� 3:��ȡ��
    private Integer status;

    // �����
    private String description;

    // ����ʱ��
    private LocalDateTime createTime;

    // ����ʱ��
    private LocalDateTime updateTime;

    // ��Ʒ����
    private Integer goodsCount;

    // ʣ��ʱ�䣨�룩
    private Long remainingTime;

    // ��ɱ��Ʒ�б�
    private List<SeckillGoodsVO> goodsList;
}



