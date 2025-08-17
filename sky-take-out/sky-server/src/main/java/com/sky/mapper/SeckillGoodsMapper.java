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
 * ��ɱ��ƷMapper�ӿ�
 */
@Mapper
public interface SeckillGoodsMapper {

    /**
     * ����������ɱ��Ʒ
     * @param seckillGoodsList
     */
    void insertBatch(List<SeckillGoods> seckillGoodsList);

    /**
     * �޸���ɱ��Ʒ
     * @param seckillGoods
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(SeckillGoods seckillGoods);

    /**
     * ����idɾ����ɱ��Ʒ
     * @param id
     */
    @Delete("delete from seckill_goods where id = #{id}")
    void deleteById(Long id);

    /**
     * ���ݻidɾ����ɱ��Ʒ
     * @param activityId
     */
    @Delete("delete from seckill_goods where activity_id = #{activityId}")
    void deleteByActivityId(Long activityId);

    /**
     * ����id��ѯ��ɱ��Ʒ
     * @param id
     * @return
     */
    @Select("select * from seckill_goods where id = #{id}")
    SeckillGoods getById(Long id);

    /**
     * ���ݻid��ѯ��ɱ��Ʒ�б�
     * @param activityId
     * @return
     */
    @Select("select * from seckill_goods where activity_id = #{activityId} and status = 1 order by create_time asc")
    List<SeckillGoods> getByActivityId(Long activityId);

    /**
     * �ۼ���棨�ֹ�����
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
     * �ͷſ��
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    @Update("update seckill_goods set available_stock = available_stock + #{quantity}, " +
            "sold_count = sold_count - #{quantity}, version = version + 1, update_time = now() " +
            "where id = #{seckillGoodsId}")
    int releaseStock(Long seckillGoodsId, Integer quantity);

    /**
     * ��ѯ������Ʒ�б���Ʒ���ײͣ�
     * @param goodsType
     * @param name
     * @return
     */
    List<com.sky.vo.AvailableGoodsVO> getAvailableGoods(Integer goodsType, String name);
}



