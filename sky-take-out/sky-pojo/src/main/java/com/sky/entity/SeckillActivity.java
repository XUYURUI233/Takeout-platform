package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ��ɱ�ʵ����
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * �״̬����
     */
    public static final Integer NOT_STARTED = 0; // δ��ʼ
    public static final Integer ACTIVE = 1;      // ������
    public static final Integer ENDED = 2;       // �ѽ���
    public static final Integer CANCELLED = 3;   // ��ȡ��

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

    // ������
    private Long createUser;

    // ������
    private Long updateUser;
}