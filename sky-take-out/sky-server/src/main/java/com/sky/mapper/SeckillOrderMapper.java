package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.SeckillOrderPageQueryDTO;
import com.sky.entity.SeckillOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 秒杀订单Mapper接口
 */
@Mapper
public interface SeckillOrderMapper {

    /**
     * 插入秒杀订单
     * @param seckillOrder
     */
    @Insert("insert into seckill_order (order_id, activity_id, seckill_goods_id, user_id, quantity, seckill_price, total_amount, pay_status, pay_expire_time, create_time, update_time) " +
            "values (#{orderId}, #{activityId}, #{seckillGoodsId}, #{userId}, #{quantity}, #{seckillPrice}, #{totalAmount}, #{payStatus}, #{payExpireTime}, #{createTime}, #{updateTime})")
    void insert(SeckillOrder seckillOrder);

    /**
     * 根据订单id查询秒杀订单
     * @param orderId
     * @return
     */
    @Select("select * from seckill_order where order_id = #{orderId}")
    SeckillOrder getByOrderId(Long orderId);

    /**
     * 根据id查询秒杀订单
     * @param id
     * @return
     */
    @Select("select * from seckill_order where id = #{id}")
    SeckillOrder getById(Long id);

    /**
     * 分页查询秒杀订单（管理端）
     * @param seckillOrderPageQueryDTO
     * @return
     */
    Page<SeckillOrder> adminPageQuery(SeckillOrderPageQueryDTO seckillOrderPageQueryDTO);

    /**
     * 分页查询用户秒杀订单
     * @param userId
     * @param status
     * @return
     */
    Page<SeckillOrder> userPageQuery(Long userId, Integer status);

    /**
     * 更新支付状态
     * @param orderId
     * @param payStatus
     */
    @Update("update seckill_order set pay_status = #{payStatus}, update_time = now() where order_id = #{orderId}")
    void updatePayStatus(Long orderId, Integer payStatus);

    /**
     * 查询超时未支付的订单
     * @param expireTime
     * @return
     */
    @Select("select * from seckill_order where pay_status = 0 and pay_expire_time < #{expireTime}")
    List<SeckillOrder> getExpiredOrders(LocalDateTime expireTime);

    /**
     * 查询用户购买数量（兼容旧版本）
     * @param seckillGoodsId
     * @param userId
     * @return
     */
    @Select("select COALESCE(sum(quantity), 0) from seckill_order where seckill_goods_id = #{seckillGoodsId} and user_id = #{userId} and pay_status = 1")
    Integer getUserBoughtCount(Long seckillGoodsId, Long userId);

    /**
     * 根据用户ID查询订单列表（兼容旧版本）
     * @param userId
     * @return
     */
    @Select("select * from seckill_order where user_id = #{userId} order by create_time desc")
    List<SeckillOrder> getByUserId(Long userId);
}