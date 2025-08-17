package com.sky.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ��ɱ������ҳ��ѯDTO
 */
@Data
public class SeckillOrderPageQueryDTO implements Serializable {

    // ҳ��
    private int page;

    // ҳ���С
    private int pageSize;

    // ������
    private String number;

    // �ֻ���
    private String phone;

    // ����״̬
    private Integer status;

    // ��ʼʱ��
    private LocalDateTime beginTime;

    // ����ʱ��
    private LocalDateTime endTime;
}



