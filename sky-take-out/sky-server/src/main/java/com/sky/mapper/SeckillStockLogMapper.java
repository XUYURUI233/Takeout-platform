package com.sky.mapper;

import com.sky.entity.SeckillStockLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * ��ɱ��������־Mapper�ӿ�
 */
@Mapper
public interface SeckillStockLogMapper {

    /**
     * �����������־
     * @param seckillStockLog
     */
    @Insert("insert into seckill_stock_log (seckill_goods_id, user_id, order_id, operation_type, quantity, before_stock, after_stock, version, remark, create_time) " +
            "values (#{seckillGoodsId}, #{userId}, #{orderId}, #{operationType}, #{quantity}, #{beforeStock}, #{afterStock}, #{version}, #{remark}, #{createTime})")
    void insert(SeckillStockLog seckillStockLog);
}



