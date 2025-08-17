package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SeckillGoods;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 秒杀商品Mapper接口
 */
@Mapper
public interface SeckillGoodsMapper {

    /**
     * 批量插入秒杀商品
     * @param seckillGoodsList
     */
    void insertBatch(List<SeckillGoods> seckillGoodsList);

    /**
     * 修改秒杀商品
     * @param seckillGoods
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(SeckillGoods seckillGoods);

    /**
     * 根据id删除秒杀商品
     * @param id
     */
    @Delete("delete from seckill_goods where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据活动id删除秒杀商品
     * @param activityId
     */
    @Delete("delete from seckill_goods where activity_id = #{activityId}")
    void deleteByActivityId(Long activityId);

    /**
     * 根据id查询秒杀商品
     * @param id
     * @return
     */
    @Select("select * from seckill_goods where id = #{id}")
    SeckillGoods getById(Long id);

    /**
     * 根据活动id查询秒杀商品列表
     * @param activityId
     * @return
     */
    @Select("select * from seckill_goods where activity_id = #{activityId} and status = 1 order by create_time asc")
    List<SeckillGoods> getByActivityId(Long activityId);

    /**
     * 扣减库存（乐观锁）
     * @param seckillGoodsId
     * @param quantity
     * @param version
     * @return
     */
    @Update("update seckill_goods set available_stock = available_stock - #{quantity}, " +
            "sold_count = sold_count + #{quantity}, version = version + 1, update_time = now() " +
            "where id = #{seckillGoodsId} and available_stock >= #{quantity} and version = #{version} and status = 1")
    int deductStock(Long seckillGoodsId, Integer quantity, Integer version);

    /**
     * 释放库存
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    @Update("update seckill_goods set available_stock = available_stock + #{quantity}, " +
            "sold_count = sold_count - #{quantity}, version = version + 1, update_time = now() " +
            "where id = #{seckillGoodsId}")
    int releaseStock(Long seckillGoodsId, Integer quantity);

    /**
     * 查询可用商品列表（菜品和套餐）
     * @param goodsType
     * @param name
     * @return
     */
    List<com.sky.vo.AvailableGoodsVO> getAvailableGoods(Integer goodsType, String name);
}



