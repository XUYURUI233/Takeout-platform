package com.sky.mapper;

import com.sky.entity.SeckillItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 秒杀商品项Mapper
 */
@Mapper
public interface SeckillItemMapper {

    /**
     * 插入秒杀商品项
     * @param seckillItem
     */
    void insert(SeckillItem seckillItem);

    /**
     * 根据ID查询秒杀商品项
     * @param id
     * @return
     */
    @Select("select * from seckill_item where id = #{id}")
    SeckillItem getById(Long id);

    /**
     * 根据秒杀活动ID查询商品列表
     * @param seckillId
     * @return
     */
    @Select("select * from seckill_item where seckill_id = #{seckillId} and status = 1")
    List<SeckillItem> getBySeckillId(Long seckillId);

    /**
     * 更新秒杀商品项
     * @param seckillItem
     */
    void update(SeckillItem seckillItem);

    /**
     * 更新库存
     * @param id
     * @param stockChange
     * @return
     */
    @Update("update seckill_item set current_stock = current_stock + #{stockChange} where id = #{id} and current_stock + #{stockChange} >= 0")
    int updateStock(Long id, Integer stockChange);

    /**
     * 根据秒杀活动ID删除商品项
     * @param seckillId
     */
    @Update("delete from seckill_item where seckill_id = #{seckillId}")
    void deleteBySeckillId(Long seckillId);
}

