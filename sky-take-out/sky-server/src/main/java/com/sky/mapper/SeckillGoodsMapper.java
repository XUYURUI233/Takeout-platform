package com.sky.mapper;

import com.sky.entity.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SeckillGoodsMapper {

    /**
     * ������ɱ��Ʒ����
     * @param seckillGoods
     */
    void insert(SeckillGoods seckillGoods);

    /**
     * ����������ɱ��Ʒ����
     * @param seckillGoodsList
     */
    void insertBatch(List<SeckillGoods> seckillGoodsList);

    /**
     * ���ݻID��ѯ��ɱ��Ʒ�б�
     * @param activityId
     * @return
     */
    @Select("select * from seckill_goods where activity_id = #{activityId} and status = 1")
    List<SeckillGoods> getByActivityId(Long activityId);

    /**
     * ����ID��ѯ��ɱ��Ʒ
     * @param id
     * @return
     */
    @Select("select * from seckill_goods where id = #{id}")
    SeckillGoods getById(Long id);

    /**
     * ������ɱ��Ʒ
     * @param seckillGoods
     */
    void update(SeckillGoods seckillGoods);

    /**
     * ɾ����ɱ��Ʒ
     * @param id
     */
    @Select("delete from seckill_goods where id = #{id}")
    void deleteById(Long id);

    /**
     * ���ݻIDɾ����ɱ��Ʒ
     * @param activityId
     */
    @Select("delete from seckill_goods where activity_id = #{activityId}")
    void deleteByActivityId(Long activityId);

    /**
     * �ۼ���棨�ֹ�����
     * @param id
     * @param quantity
     * @param version
     * @return
     */
    int deductStock(Long id, Integer quantity, Integer version);

    /**
     * ������ѯ��ɱ��Ʒ�����Ϣ�������Ż���
     * @param goodsIds
     * @return
     */
    List<SeckillGoods> getStockInfoByIds(List<Long> goodsIds);

    /**
     * ��ѯ�Ϳ����Ʒ�����С��ָ��������
     * @param threshold �����ֵ
     * @param activityId �ID����ѡ��
     * @return
     */
    List<SeckillGoods> getLowStockGoods(Integer threshold, Long activityId);

    /**
     * ����������Ʒ�汾�ţ���ֹABA���⣩
     * @param goodsIds
     * @return
     */
    int batchUpdateVersion(List<Long> goodsIds);

    /**
     * ��ȡ��Ʒ�����գ����ڻ��棩
     * @param goodsId
     * @return
     */
    @Select("select id, available_stock, sold_count, version, update_time from seckill_goods where id = #{goodsId}")
    SeckillGoods getStockSnapshot(Long goodsId);
}