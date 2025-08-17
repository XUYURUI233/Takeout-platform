package com.sky.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ��ɱ���ҳ��ѯDTO
 */
@Data
public class SeckillActivityPageQueryDTO implements Serializable {

    // ҳ��
    private int page;

    // ҳ���С
    private int pageSize;

    // �����
    private String name;

    // �״̬
    private Integer status;

    // ��ʼʱ��
    private LocalDateTime startTime;

    // ����ʱ��
    private LocalDateTime endTime;
}



