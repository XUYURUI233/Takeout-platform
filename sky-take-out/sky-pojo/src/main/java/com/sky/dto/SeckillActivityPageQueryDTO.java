package com.sky.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ��ɱ���ҳ��ѯDTO
 */
@Data
public class SeckillActivityPageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // ҳ��
    private int page;

    // ÿҳ��¼��
    private int pageSize;

    // �����
    private String name;

    // ״̬
    private Integer status;

    // ��ʼʱ��
    private LocalDateTime startTime;

    // ����ʱ��
    private LocalDateTime endTime;

}