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
 * 秒杀服务接口
 */
public interface SeckillService {

    /**
     * 创建秒杀活动
     * @param seckillDTO
     */
    void createSeckill(SeckillDTO seckillDTO);

    /**
     * 分页查询秒杀活动
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult getSeckillList(Integer page, Integer pageSize, Integer status);

    /**
     * 更新秒杀活动
     * @param seckillId
     * @param seckillDTO
     */
    void updateSeckill(Long seckillId, SeckillDTO seckillDTO);

    /**
     * 删除秒杀活动
     * @param seckillId
     */
    void deleteSeckill(Long seckillId);

    /**
     * 获取秒杀横幅列表
     * @return
     */
    List<SeckillVO> getBannerList();

    /**
     * 获取秒杀商品列表
     * @param seckillId
     * @param page
     * @param pageSize
     * @return
     */
    PageResult getGoodsList(Long seckillId, Integer page, Integer pageSize);

    /**
     * 参与秒杀购买
     * @param purchaseDTO
     * @return
     */
    SeckillOrderVO purchase(SeckillPurchaseDTO purchaseDTO);

    /**
     * 获取用户秒杀订单列表
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult getUserOrders(Integer page, Integer pageSize, Integer status);

    /**
     * 更新秒杀库存
     * @param stockUpdateDTO
     */
    void updateStock(SeckillStockUpdateDTO stockUpdateDTO);

    /**
     * 回滚秒杀库存
     * @param orderId
     * @param itemId
     * @param rollbackQuantity
     */
    void rollbackStock(String orderId, Long itemId, Integer rollbackQuantity);

    /**
     * 获取秒杀统计数据
     * @param seckillId
     * @return
     */
    Object getStatistics(Long seckillId);
}

