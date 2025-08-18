package com.sky.service;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.SeckillOrderSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.SeckillOrderSubmitVO;

/**
 * 秒杀订单Service接口
 */
public interface SeckillOrderService {

    /**
     * 提交秒杀订单
     * @param seckillOrderSubmitDTO
     * @return
     */
    SeckillOrderSubmitVO submitOrder(SeckillOrderSubmitDTO seckillOrderSubmitDTO);

    /**
     * 秒杀订单支付
     * @param ordersPaymentDTO
     * @return
     * @throws Exception
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 分页查询用户秒杀订单
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult pageQueryByUser(int page, int pageSize, Integer status);

    /**
     * 根据ID查询秒杀订单详情
     * @param id
     * @return
     */
    Object getOrderDetail(Long id);

    /**
     * 取消秒杀订单
     * @param id
     */
    void cancelOrder(Long id);

    /**
     * 再来一单
     * @param id
     * @return
     */
    SeckillOrderSubmitVO repeatOrder(Long id);

    /**
     * 处理超时未支付的秒杀订单
     */
    void handleExpiredOrders();

    /**
     * 检查用户购买资格
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    Object checkEligibility(Long seckillGoodsId, Integer quantity);
}