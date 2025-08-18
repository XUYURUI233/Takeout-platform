package com.sky.mapper;

import com.sky.entity.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SeckillGoodsMapper {

    /**
     * 插入秒杀商品数据
     * @param seckillGoods
     */
    void insert(SeckillGoods seckillGoods);

    /**
     * 批量插入秒杀商品数据
     * @param seckillGoodsList
     */
    void insertBatch(List<SeckillGoods> seckillGoodsList);

    /**
     * 根据活动ID查询秒杀商品列表
     * @param activityId
     * @return
     */
    @Select("select * from seckill_goods where activity_id = #{activityId} and status = 1")
    List<SeckillGoods> getByActivityId(Long activityId);

    /**
     * 根据ID查询秒杀商品
     * @param id
     * @return
     */
    @Select("select * from seckill_goods where id = #{id}")
    SeckillGoods getById(Long id);

    /**
     * 更新秒杀商品
     * @param seckillGoods
     */
    void update(SeckillGoods seckillGoods);

    /**
     * 删除秒杀商品
     * @param id
     */
    @Select("delete from seckill_goods where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据活动ID删除秒杀商品
     * @param activityId
     */
    @Select("delete from seckill_goods where activity_id = #{activityId}")
    void deleteByActivityId(Long activityId);

    /**
     * 扣减库存（乐观锁）
     * @param id
     * @param quantity
     * @param version
     * @return
     */
    int deductStock(Long id, Integer quantity, Integer version);

    /**
     * 批量查询秒杀商品库存信息（性能优化）
     * @param goodsIds
     * @return
     */
    List<SeckillGoods> getStockInfoByIds(List<Long> goodsIds);

    /**
     * 查询低库存商品（库存小于指定数量）
     * @param threshold 库存阈值
     * @param activityId 活动ID（可选）
     * @return
     */
    List<SeckillGoods> getLowStockGoods(Integer threshold, Long activityId);

    /**
     * 批量更新商品版本号（防止ABA问题）
     * @param goodsIds
     * @return
     */
    int batchUpdateVersion(List<Long> goodsIds);

    /**
     * 获取商品库存快照（用于缓存）
     * @param goodsId
     * @return
     */
    @Select("select id, available_stock, sold_count, version, update_time from seckill_goods where id = #{goodsId}")
    SeckillGoods getStockSnapshot(Long goodsId);
}