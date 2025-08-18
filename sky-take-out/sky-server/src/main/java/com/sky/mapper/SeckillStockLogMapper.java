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
 * ��ɱ�����־Mapper�ӿ�
 */
@Mapper
public interface SeckillStockLogMapper {

    /**
     * ���������־
     * @param seckillStockLog
     */
    void insert(SeckillStockLog seckillStockLog);

    /**
     * ������ɱ��ƷID��ѯ�����־
     * @param seckillGoodsId
     * @return
     */
    List<SeckillStockLog> getBySeckillGoodsId(Long seckillGoodsId);

    /**
     * ��ҳ��ѯ�����־
     * @param seckillGoodsId ��ɱ��ƷID
     * @param operationType ��������
     * @param startTime ��ʼʱ��
     * @param endTime ����ʱ��
     * @return ��ҳ���
     */
    Page<SeckillStockLog> pageQuery(Long seckillGoodsId, Integer operationType, 
                                   LocalDateTime startTime, LocalDateTime endTime);

    /**
     * ���ݲ�������ͳ�ƿ����־
     * @param seckillGoodsId ��ɱ��ƷID
     * @param operationType ��������
     * @return ͳ�ƽ��
     */
    Map<String, Object> getStatsByType(Long seckillGoodsId, Integer operationType);
}