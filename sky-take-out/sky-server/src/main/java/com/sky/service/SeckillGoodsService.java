package com.sky.service;

import com.sky.dto.SeckillGoodsDTO;
import com.sky.entity.SeckillGoods;
import com.sky.vo.SeckillGoodsVO;

import java.util.List;

/**
 * 秒杀商品业务接口
 */
public interface SeckillGoodsService {

    /**
     * 查询可用商品列表
     * @param type
     * @param name
     * @return
     */
    List<SeckillGoodsVO> getAvailableGoods(Integer type, String name);

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
     * 扣减库存（别名方法）
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    boolean deductStock(Long seckillGoodsId, Integer quantity);

    /**
     * 检查用户购买资格
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    boolean checkEligibility(Long seckillGoodsId, Integer quantity);

    /**
     * 根据活动ID查询商品列表
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
     * 检查商品库存
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    boolean checkStock(Long seckillGoodsId, Integer quantity);

    /**
     * 扣减库存
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    boolean decreaseStock(Long seckillGoodsId, Integer quantity);

    /**
     * 释放库存
     * @param seckillGoodsId
     * @param quantity
     */
    void releaseStock(Long seckillGoodsId, Integer quantity);
}