package com.sky.service;

import com.sky.dto.SeckillGoodsDTO;
import com.sky.vo.SeckillGoodsVO;
import com.sky.vo.AvailableGoodsVO;

import java.util.List;

/**
 * 秒杀商品Service接口
 */
public interface SeckillGoodsService {

    /**
     * 根据活动ID查询秒杀商品列表
     * @param activityId
     * @return
     */
    List<SeckillGoodsVO> getByActivityId(Long activityId);

    /**
     * 根据ID查询秒杀商品详情
     * @param id
     * @return
     */
    SeckillGoodsVO getById(Long id);

    /**
     * 修改秒杀商品信息
     * @param seckillGoodsDTO
     */
    void update(SeckillGoodsDTO seckillGoodsDTO);

    /**
     * 删除秒杀商品
     * @param id
     */
    void deleteById(Long id);

    /**
     * 查询可用商品列表
     * @param type
     * @param name
     * @return
     */
    List<AvailableGoodsVO> getAvailableGoods(Integer type, String name);

    /**
     * 检查用户购买资格
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    Object checkEligibility(Long seckillGoodsId, Integer quantity);
}