package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SeckillUserRecord;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 秒杀用户记录Mapper接口
 */
@Mapper
public interface SeckillUserRecordMapper {

    /**
     * 根据用户ID和商品ID查询购买记录
     * @param userId 用户ID
     * @param seckillGoodsId 秒杀商品ID
     * @return
     */
    @Select("select * from seckill_user_record where user_id = #{userId} and seckill_goods_id = #{seckillGoodsId}")
    SeckillUserRecord getByUserIdAndGoodsId(Long userId, Long seckillGoodsId);

    /**
     * 根据用户ID和秒杀商品ID查询购买记录（别名方法，保持兼容性）
     * @param userId 用户ID
     * @param seckillGoodsId 秒杀商品ID
     * @return
     */
    default SeckillUserRecord getByUserIdAndSeckillGoodsId(Long userId, Long seckillGoodsId) {
        return getByUserIdAndGoodsId(userId, seckillGoodsId);
    }

    /**
     * 新增用户购买记录
     * @param seckillUserRecord
     */
    @AutoFill(OperationType.INSERT)
    void insert(SeckillUserRecord seckillUserRecord);

    /**
     * 更新用户购买记录
     * @param seckillUserRecord
     */
    @AutoFill(OperationType.UPDATE)
    void update(SeckillUserRecord seckillUserRecord);

    /**
     * 插入或更新用户购买记录（使用ON DUPLICATE KEY UPDATE避免并发问题）
     * @param seckillUserRecord
     */
    @AutoFill(OperationType.INSERT)
    void insertOrUpdate(SeckillUserRecord seckillUserRecord);
}

