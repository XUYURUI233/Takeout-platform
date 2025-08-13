package com.sky.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ��ɱ�DTO
 */
@Data
public class SeckillDTO {

    //��ɱ�����
    private String seckillName;

    //��ɱ�����
    private String seckillDescription;

    //��ʼʱ��
    private LocalDateTime startTime;

    //����ʱ��
    private LocalDateTime endTime;

    //���ͼƬ
    private String bannerImage;

    //״̬ 0-���� 1-����
    private Integer status;

    //��ɱ��Ʒ�б�
    private List<SeckillItemDTO> seckillItems;
}

