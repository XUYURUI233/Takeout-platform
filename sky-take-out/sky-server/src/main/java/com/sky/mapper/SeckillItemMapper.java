package com.sky.mapper;

import com.sky.entity.SeckillItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * ��ɱ��Ʒ��Mapper
 */
@Mapper
public interface SeckillItemMapper {

    /**
     * ������ɱ��Ʒ��
     * @param seckillItem
     */
    void insert(SeckillItem seckillItem);

    /**
     * ����ID��ѯ��ɱ��Ʒ��
     * @param id
     * @return
     */
    @Select("select * from seckill_item where id = #{id}")
    SeckillItem getById(Long id);

    /**
     * ������ɱ�ID��ѯ��Ʒ�б�
     * @param seckillId
     * @return
     */
    @Select("select * from seckill_item where seckill_id = #{seckillId} and status = 1")
    List<SeckillItem> getBySeckillId(Long seckillId);

    /**
     * ������ɱ��Ʒ��
     * @param seckillItem
     */
    void update(SeckillItem seckillItem);

    /**
     * ���¿��
     * @param id
     * @param stockChange
     * @return
     */
    @Update("update seckill_item set current_stock = current_stock + #{stockChange} where id = #{id} and current_stock + #{stockChange} >= 0")
    int updateStock(Long id, Integer stockChange);

    /**
     * ������ɱ�IDɾ����Ʒ��
     * @param seckillId
     */
    @Update("delete from seckill_item where seckill_id = #{seckillId}")
    void deleteBySeckillId(Long seckillId);
}

