package com.sky.service;

import com.sky.dto.SeckillOrderDTO;
import com.sky.dto.SeckillOrderPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SeckillOrderSubmitVO;
import com.sky.vo.SeckillOrderVO;

/**
 * ��ɱ����Service�ӿ�
 */
public interface SeckillOrderService {

    /**
     * �ύ��ɱ����
     * @param seckillOrderDTO
     * @return
     */
    SeckillOrderSubmitVO submitOrder(SeckillOrderDTO seckillOrderDTO);

    /**
     * ��ɱ����֧��
     * @param orderNumber
     * @param payMethod
     * @return
     */
    void payment(String orderNumber, Integer payMethod);

    /**
     * ȡ����ɱ����
     * @param id
     */
    void cancelOrder(Long id);

    /**
     * ��ҳ��ѯ��ɱ����������ˣ�
     * @param seckillOrderPageQueryDTO
     * @return
     */
    PageResult adminPageQuery(SeckillOrderPageQueryDTO seckillOrderPageQueryDTO);

    /**
     * ��ѯ��ɱ�������飨����ˣ�
     * @param id
     * @return
     */
    SeckillOrderVO getOrderDetails(Long id);

    /**
     * ��ҳ��ѯ�û���ɱ����
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult userPageQuery(Integer page, Integer pageSize, Integer status);

    /**
     * ��ѯ�û���ɱ��������
     * @param id
     * @return
     */
    SeckillOrderVO getUserOrderDetails(Long id);

    /**
     * ����һ��
     * @param id
     * @return
     */
    SeckillOrderSubmitVO againOrder(Long id);

    /**
     * ����ʱδ֧������
     */
    void handleExpiredOrders();
}



