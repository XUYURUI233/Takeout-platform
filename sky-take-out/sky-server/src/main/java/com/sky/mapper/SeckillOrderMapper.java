package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.SeckillOrder;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 秒杀订单Mapper接口
 */
@Mapper
public interface SeckillOrderMapper {

    /**
     * 新增秒杀订单
     * @param seckillOrder
     */
    @AutoFill(OperationType.INSERT)
    void insert(SeckillOrder seckillOrder);

    /**
     * 根据ID查询秒杀订单
     * @param id
     * @return
     */
    @Select("select * from seckill_order where id = #{id}")
    SeckillOrder getById(Long id);

    /**
     * 根据订单号和用户ID查询秒杀订单
     * @param orderNumber
     * @param userId
     * @return
     */
    @Select("select * from seckill_order where number = #{orderNumber} and user_id = #{userId}")
    SeckillOrder getByNumberAndUserId(String orderNumber, Long userId);

    /**
     * 根据订单ID查询秒杀订单
     * @param orderId
     * @return
     */
    @Select("select * from seckill_order where order_id = #{orderId}")
    SeckillOrder getByOrderId(Long orderId);

    /**
     * 更新秒杀订单
     * @param seckillOrder
     */
    @AutoFill(OperationType.UPDATE)
    void update(SeckillOrder seckillOrder);

    /**
     * 用户端分页查询秒杀订单
     * @param userId
     * @param status
     * @return
     */
    Page<SeckillOrder> pageQueryByUser(Long userId, Integer status);

    /**
     * 查询超时未支付的秒杀订单
     * @param currentTime
     * @return
     */
    @Select("select * from seckill_order where status = 1 and pay_expire_time < #{currentTime}")
    List<SeckillOrder> getExpiredOrders(LocalDateTime currentTime);

    /**
     * 查询可回滚的订单（待支付状态）
     * @param seckillGoodsId 秒杀商品ID
     * @param maxCount 最大数量
     * @return 可回滚订单列表
     */
    List<SeckillOrder> getRollbackableOrders(Long seckillGoodsId, Integer maxCount);

    /**
     * 根据时间范围查询订单
     * @param seckillGoodsId 秒杀商品ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单列表
     */
    List<SeckillOrder> getOrdersByTimeRange(Long seckillGoodsId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计已完成订单的总数量
     * @param seckillGoodsId 秒杀商品ID
     * @return 已完成订单总数量
     */
    Integer getCompletedOrderQuantity(Long seckillGoodsId);

    /**
     * 统计活动总订单数
     * @param activityId 活动ID
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @return
     */
    Integer countTotalOrders(Long activityId, String beginDate, String endDate);

    /**
     * 统计活动成功订单数
     * @param activityId 活动ID
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @return
     */
    Integer countSuccessOrders(Long activityId, String beginDate, String endDate);

    /**
     * 统计活动总金额
     * @param activityId 活动ID
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @return
     */
    Double sumTotalAmount(Long activityId, String beginDate, String endDate);
}