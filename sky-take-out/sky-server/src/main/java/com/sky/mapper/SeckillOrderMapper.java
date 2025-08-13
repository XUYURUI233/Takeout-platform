package com.sky.mapper;

import com.sky.entity.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 秒杀订单Mapper
 */
@Mapper
public interface SeckillOrderMapper {

    /**
     * 插入秒杀订单
     * @param seckillOrder
     */
    void insert(SeckillOrder seckillOrder);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     * @return
     */
    @Select("select * from seckill_order where order_number = #{orderNumber}")
    SeckillOrder getByOrderNumber(String orderNumber);

    /**
     * 根据用户ID查询订单列表
     * @param userId
     * @return
     */
    @Select("select * from seckill_order where user_id = #{userId} order by create_time desc")
    List<SeckillOrder> getByUserId(Long userId);

    /**
     * 根据秒杀商品项ID和用户ID查询用户购买数量
     * @param itemId
     * @param userId
     * @return
     */
    @Select("select coalesce(sum(quantity), 0) from seckill_order where item_id = #{itemId} and user_id = #{userId} and status in (1, 2)")
    Integer getUserBoughtCount(Long itemId, Long userId);

    /**
     * 更新订单状态
     * @param orderNumber
     * @param status
     */
    void updateStatus(String orderNumber, Integer status);
}

