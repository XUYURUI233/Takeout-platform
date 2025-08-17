package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ��ɱ���
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillActivity implements Serializable {

    private static final long serialVersionUID = 1L;

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

    // ������
    private Long createUser;

    // �޸���
    private Long updateUser;
}