package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.entity.SeckillStockLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 秒杀库存日志Mapper接口
 */
@Mapper
public interface SeckillStockLogMapper {

    /**
     * 新增库存日志
     * @param seckillStockLog
     */
    void insert(SeckillStockLog seckillStockLog);

    /**
     * 根据秒杀商品ID查询库存日志
     * @param seckillGoodsId
     * @return
     */
    List<SeckillStockLog> getBySeckillGoodsId(Long seckillGoodsId);

    /**
     * 分页查询库存日志
     * @param seckillGoodsId 秒杀商品ID
     * @param operationType 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    Page<SeckillStockLog> pageQuery(Long seckillGoodsId, Integer operationType, 
                                   LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据操作类型统计库存日志
     * @param seckillGoodsId 秒杀商品ID
     * @param operationType 操作类型
     * @return 统计结果
     */
    Map<String, Object> getStatsByType(Long seckillGoodsId, Integer operationType);
}