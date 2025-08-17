package com.sky.service;

import com.sky.dto.SeckillOrderDTO;
import com.sky.dto.SeckillOrderPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SeckillOrderSubmitVO;
import com.sky.vo.SeckillOrderVO;

/**
 * 秒杀订单Service接口
 */
public interface SeckillOrderService {

    /**
     * 提交秒杀订单
     * @param seckillOrderDTO
     * @return
     */
    SeckillOrderSubmitVO submitOrder(SeckillOrderDTO seckillOrderDTO);

    /**
     * 秒杀订单支付
     * @param orderNumber
     * @param payMethod
     * @return
     */
    void payment(String orderNumber, Integer payMethod);

    /**
     * 取消秒杀订单
     * @param id
     */
    void cancelOrder(Long id);

    /**
     * 分页查询秒杀订单（管理端）
     * @param seckillOrderPageQueryDTO
     * @return
     */
    PageResult adminPageQuery(SeckillOrderPageQueryDTO seckillOrderPageQueryDTO);

    /**
     * 查询秒杀订单详情（管理端）
     * @param id
     * @return
     */
    SeckillOrderVO getOrderDetails(Long id);

    /**
     * 分页查询用户秒杀订单
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult userPageQuery(Integer page, Integer pageSize, Integer status);

    /**
     * 查询用户秒杀订单详情
     * @param id
     * @return
     */
    SeckillOrderVO getUserOrderDetails(Long id);

    /**
     * 再来一单
     * @param id
     * @return
     */
    SeckillOrderSubmitVO againOrder(Long id);

    /**
     * 处理超时未支付订单
     */
    void handleExpiredOrders();
}



