package com.sky.service;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.SeckillOrderSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.SeckillOrderSubmitVO;

/**
 * ��ɱ����Service�ӿ�
 */
public interface SeckillOrderService {

    /**
     * �ύ��ɱ����
     * @param seckillOrderSubmitDTO
     * @return
     */
    SeckillOrderSubmitVO submitOrder(SeckillOrderSubmitDTO seckillOrderSubmitDTO);

    /**
     * ��ɱ����֧��
     * @param ordersPaymentDTO
     * @return
     * @throws Exception
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * ֧���ɹ����޸Ķ���״̬
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * ��ҳ��ѯ�û���ɱ����
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult pageQueryByUser(int page, int pageSize, Integer status);

    /**
     * ����ID��ѯ��ɱ��������
     * @param id
     * @return
     */
    Object getOrderDetail(Long id);

    /**
     * ȡ����ɱ����
     * @param id
     */
    void cancelOrder(Long id);

    /**
     * ����һ��
     * @param id
     * @return
     */
    SeckillOrderSubmitVO repeatOrder(Long id);

    /**
     * ����ʱδ֧������ɱ����
     */
    void handleExpiredOrders();

    /**
     * ����û������ʸ�
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    Object checkEligibility(Long seckillGoodsId, Integer quantity);
}