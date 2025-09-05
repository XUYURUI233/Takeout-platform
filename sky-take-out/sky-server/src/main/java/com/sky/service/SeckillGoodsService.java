package com.sky.service;

import com.sky.dto.SeckillGoodsDTO;
import com.sky.vo.SeckillGoodsVO;
import com.sky.vo.AvailableGoodsVO;

import java.util.List;

/**
 * ��ɱ��ƷService�ӿ�
 */
public interface SeckillGoodsService {

    /**
     * ���ݻID��ѯ��ɱ��Ʒ�б�
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
     * ��ѯ������Ʒ�б�
     * @param type
     * @param name
     * @return
     */
    List<AvailableGoodsVO> getAvailableGoods(Integer type, String name);

    /**
     * ����û������ʸ�
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    Object checkEligibility(Long seckillGoodsId, Integer quantity);
}