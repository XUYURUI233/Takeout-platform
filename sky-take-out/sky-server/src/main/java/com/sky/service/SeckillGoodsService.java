package com.sky.service;

import com.sky.dto.SeckillGoodsDTO;
import com.sky.entity.SeckillGoods;
import com.sky.vo.SeckillGoodsVO;

import java.util.List;

/**
 * ��ɱ��Ʒҵ��ӿ�
 */
public interface SeckillGoodsService {

    /**
     * ��ѯ������Ʒ�б�
     * @param type
     * @param name
     * @return
     */
    List<SeckillGoodsVO> getAvailableGoods(Integer type, String name);

    /**
     * �޸���ɱ��Ʒ��Ϣ
     * @param seckillGoodsDTO
     */
    void update(SeckillGoodsDTO seckillGoodsDTO);

    /**
     * ɾ����ɱ��Ʒ
     * @param id
     */
    void deleteById(Long id);

    /**
     * �ۼ���棨����������
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    boolean deductStock(Long seckillGoodsId, Integer quantity);

    /**
     * ����û������ʸ�
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    boolean checkEligibility(Long seckillGoodsId, Integer quantity);

    /**
     * ���ݻID��ѯ��Ʒ�б�
     * @param activityId
     * @return
     */
    List<SeckillGoodsVO> getByActivityId(Long activityId);

    /**
     * ����ID��ѯ��ɱ��Ʒ����
     * @param id
     * @return
     */
    SeckillGoodsVO getById(Long id);

    /**
     * �����Ʒ���
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    boolean checkStock(Long seckillGoodsId, Integer quantity);

    /**
     * �ۼ����
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    boolean decreaseStock(Long seckillGoodsId, Integer quantity);

    /**
     * �ͷſ��
     * @param seckillGoodsId
     * @param quantity
     */
    void releaseStock(Long seckillGoodsId, Integer quantity);
}