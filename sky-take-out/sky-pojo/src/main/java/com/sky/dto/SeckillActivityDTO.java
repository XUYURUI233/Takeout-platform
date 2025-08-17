package com.sky.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ��ɱ�DTO
 */
@Data
public class SeckillActivityDTO implements Serializable {

    private Long id;

    // ��ɱ�����
    private String name;

    // �ͼƬ
    private String image;

    // ��ɱ��ʼʱ��
    private LocalDateTime startTime;

    // ��ɱ����ʱ��
    private LocalDateTime endTime;

    // �����
    private String description;

    // ��ɱ��Ʒ�б�
    private List<SeckillGoodsDTO> goodsList;
}



