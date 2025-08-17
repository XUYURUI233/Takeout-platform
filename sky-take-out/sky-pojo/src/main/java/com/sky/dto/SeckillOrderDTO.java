package com.sky.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * ��ɱ�����ύDTO
 */
@Data
public class SeckillOrderDTO implements Serializable {

    // ��ɱ��ƷID
    private Long seckillGoodsId;

    // ��������
    private Integer quantity;

    // ��ַ��ID
    private Long addressBookId;

    // ��ע
    private String remark;
}



