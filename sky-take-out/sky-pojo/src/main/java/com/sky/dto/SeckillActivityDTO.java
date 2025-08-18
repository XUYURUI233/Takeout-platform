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

    // ��Ʒ�б�
    private List<SeckillGoodsDTO> goodsList;
}