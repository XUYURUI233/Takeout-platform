package com.sky.service;

import com.sky.dto.SeckillDTO;
import com.sky.dto.SeckillPurchaseDTO;
import com.sky.dto.SeckillStockUpdateDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.SeckillGoodsVO;
import com.sky.vo.SeckillOrderVO;
import com.sky.vo.SeckillVO;

import java.util.List;

/**
 * ��ɱ����ӿ�
 */
public interface SeckillService {

    /**
     * ������ɱ�
     * @param seckillDTO
     */
    void createSeckill(SeckillDTO seckillDTO);

    /**
     * ��ҳ��ѯ��ɱ�
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult getSeckillList(Integer page, Integer pageSize, Integer status);

    /**
     * ������ɱ�
     * @param seckillId
     * @param seckillDTO
     */
    void updateSeckill(Long seckillId, SeckillDTO seckillDTO);

    /**
     * ɾ����ɱ�
     * @param seckillId
     */
    void deleteSeckill(Long seckillId);

    /**
     * ��ȡ��ɱ����б�
     * @return
     */
    List<SeckillVO> getBannerList();

    /**
     * ��ȡ��ɱ��Ʒ�б�
     * @param seckillId
     * @param page
     * @param pageSize
     * @return
     */
    PageResult getGoodsList(Long seckillId, Integer page, Integer pageSize);

    /**
     * ������ɱ����
     * @param purchaseDTO
     * @return
     */
    SeckillOrderVO purchase(SeckillPurchaseDTO purchaseDTO);

    /**
     * ��ȡ�û���ɱ�����б�
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult getUserOrders(Integer page, Integer pageSize, Integer status);

    /**
     * ������ɱ���
     * @param stockUpdateDTO
     */
    void updateStock(SeckillStockUpdateDTO stockUpdateDTO);

    /**
     * �ع���ɱ���
     * @param orderId
     * @param itemId
     * @param rollbackQuantity
     */
    void rollbackStock(String orderId, Long itemId, Integer rollbackQuantity);

    /**
     * ��ȡ��ɱͳ������
     * @param seckillId
     * @return
     */
    Object getStatistics(Long seckillId);
}

