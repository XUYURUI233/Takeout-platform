package com.sky.mapper;

import com.sky.entity.SeckillUserRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * ��ɱ�û������¼Mapper�ӿ�
 */
@Mapper
public interface SeckillUserRecordMapper {

    /**
     * �����û������¼
     * @param seckillUserRecord
     */
    @Insert("insert into seckill_user_record (activity_id, seckill_goods_id, user_id, quantity, create_time, update_time) " +
            "values (#{activityId}, #{seckillGoodsId}, #{userId}, #{quantity}, #{createTime}, #{updateTime})")
    void insert(SeckillUserRecord seckillUserRecord);

    /**
     * ��ѯ�û������¼
     * @param activityId
     * @param seckillGoodsId
     * @param userId
     * @return
     */
    @Select("select * from seckill_user_record where activity_id = #{activityId} and seckill_goods_id = #{seckillGoodsId} and user_id = #{userId}")
    SeckillUserRecord getByActivityGoodsUser(Long activityId, Long seckillGoodsId, Long userId);

    /**
     * �����û���������
     * @param activityId
     * @param seckillGoodsId
     * @param userId
     * @param quantity
     */
    @Update("update seckill_user_record set quantity = quantity + #{quantity}, update_time = now() " +
            "where activity_id = #{activityId} and seckill_goods_id = #{seckillGoodsId} and user_id = #{userId}")
    void updateQuantity(Long activityId, Long seckillGoodsId, Long userId, Integer quantity);
}



