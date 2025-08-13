package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ��ɱ�
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seckill implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

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

    //����ʱ��
    private LocalDateTime createTime;

    //����ʱ��
    private LocalDateTime updateTime;

    //������
    private Long createUser;

    //������
    private Long updateUser;
}

