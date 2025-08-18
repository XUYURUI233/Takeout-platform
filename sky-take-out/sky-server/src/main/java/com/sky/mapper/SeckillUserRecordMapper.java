package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SeckillUserRecord;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * ��ɱ�û���¼Mapper�ӿ�
 */
@Mapper
public interface SeckillUserRecordMapper {

    /**
     * �����û�ID����ƷID��ѯ�����¼
     * @param userId �û�ID
     * @param seckillGoodsId ��ɱ��ƷID
     * @return
     */
    @Select("select * from seckill_user_record where user_id = #{userId} and seckill_goods_id = #{seckillGoodsId}")
    SeckillUserRecord getByUserIdAndGoodsId(Long userId, Long seckillGoodsId);

    /**
     * �����û�ID����ɱ��ƷID��ѯ�����¼���������������ּ����ԣ�
     * @param userId �û�ID
     * @param seckillGoodsId ��ɱ��ƷID
     * @return
     */
    default SeckillUserRecord getByUserIdAndSeckillGoodsId(Long userId, Long seckillGoodsId) {
        return getByUserIdAndGoodsId(userId, seckillGoodsId);
    }

    /**
     * �����û������¼
     * @param seckillUserRecord
     */
    @AutoFill(OperationType.INSERT)
    void insert(SeckillUserRecord seckillUserRecord);

    /**
     * �����û������¼
     * @param seckillUserRecord
     */
    @AutoFill(OperationType.UPDATE)
    void update(SeckillUserRecord seckillUserRecord);

    /**
     * ���������û������¼��ʹ��ON DUPLICATE KEY UPDATE���Ⲣ�����⣩
     * @param seckillUserRecord
     */
    @AutoFill(OperationType.INSERT)
    void insertOrUpdate(SeckillUserRecord seckillUserRecord);
}

