package com.sky.mapper;

import com.sky.entity.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ��ɱ����Mapper
 */
@Mapper
public interface SeckillOrderMapper {

    /**
     * ������ɱ����
     * @param seckillOrder
     */
    void insert(SeckillOrder seckillOrder);

    /**
     * ���ݶ����Ų�ѯ����
     * @param orderNumber
     * @return
     */
    @Select("select * from seckill_order where order_number = #{orderNumber}")
    SeckillOrder getByOrderNumber(String orderNumber);

    /**
     * �����û�ID��ѯ�����б�
     * @param userId
     * @return
     */
    @Select("select * from seckill_order where user_id = #{userId} order by create_time desc")
    List<SeckillOrder> getByUserId(Long userId);

    /**
     * ������ɱ��Ʒ��ID���û�ID��ѯ�û���������
     * @param itemId
     * @param userId
     * @return
     */
    @Select("select coalesce(sum(quantity), 0) from seckill_order where item_id = #{itemId} and user_id = #{userId} and status in (1, 2)")
    Integer getUserBoughtCount(Long itemId, Long userId);

    /**
     * ���¶���״̬
     * @param orderNumber
     * @param status
     */
    void updateStatus(String orderNumber, Integer status);
}

