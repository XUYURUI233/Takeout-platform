package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * ��ɱ�����ύDTO
 */
@Data
public class SeckillOrderSubmitDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // ��ɱ��ƷID
    private Long seckillGoodsId;

    // ��������
    private Integer quantity;

    // ��ַ��ID
    private Long addressBookId;

    // ��ע
    private String remark;
}