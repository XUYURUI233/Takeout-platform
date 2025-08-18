package com.sky.service.impl;

import com.sky.entity.SeckillGoods;
import com.sky.entity.SeckillOrder;
import com.sky.entity.SeckillStockLog;
import com.sky.mapper.SeckillGoodsMapper;
import com.sky.mapper.SeckillOrderMapper;
import com.sky.mapper.SeckillStockLogMapper;
import com.sky.service.SeckillStockRollbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ��ɱ���ع�����ʵ����
 */
@Service
@Slf4j
public class SeckillStockRollbackServiceImpl implements SeckillStockRollbackService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillStockLogMapper seckillStockLogMapper;

    @Override
    @Transactional
    public void rollbackByOrderId(Long orderId) {
        log.info("��ʼ�ع�������棬����ID: {}", orderId);
        
        try {
            // ��ѯ������Ϣ
            SeckillOrder seckillOrder = seckillOrderMapper.getByOrderId(orderId);
            if (seckillOrder == null) {
                log.warn("���������ڣ��޷��ع���棬����ID: {}", orderId);
                return;
            }

            // �ع����
            rollbackStock(seckillOrder.getSeckillGoodsId(), seckillOrder.getQuantity(), 
                         orderId, "����ȡ���ع�");
            
            log.info("�������ع��ɹ�������ID: {}", orderId);
        } catch (Exception e) {
            log.error("�������ع�ʧ�ܣ�����ID: {}", orderId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void rollbackByGoodsId(Long seckillGoodsId, Integer quantity, String reason) {
        log.info("��ʼ�ع���Ʒ��棬��ƷID: {}, ����: {}, ԭ��: {}", seckillGoodsId, quantity, reason);
        
        try {
            rollbackStock(seckillGoodsId, quantity, null, reason);
            log.info("��Ʒ���ع��ɹ�����ƷID: {}", seckillGoodsId);
        } catch (Exception e) {
            log.error("��Ʒ���ع�ʧ�ܣ���ƷID: {}", seckillGoodsId, e);
            throw e;
        }
    }

    @Override
    public List<SeckillStockLog> getStockLogs(Long seckillGoodsId) {
        return seckillStockLogMapper.getBySeckillGoodsId(seckillGoodsId);
    }

    @Override
    @Transactional
    public void batchRollback(List<Long> orderIds) {
        log.info("��ʼ�����ع���棬��������: {}", orderIds.size());
        
        for (Long orderId : orderIds) {
            try {
                rollbackByOrderId(orderId);
            } catch (Exception e) {
                log.error("�����ع��е�������ʧ�ܣ�����ID: {}", orderId, e);
                // ����������һ�����������ж�������������
            }
        }
        
        log.info("�����ع������ɣ���������: {}", orderIds.size());
    }

    /**
     * ִ�п��ع�
     */
    private void rollbackStock(Long seckillGoodsId, Integer quantity, Long orderId, String reason) {
        // ��ѯ��ǰ�����Ϣ
        SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillGoodsId);
        if (seckillGoods == null) {
            throw new RuntimeException("��ɱ��Ʒ������: " + seckillGoodsId);
        }

        Integer beforeStock = seckillGoods.getAvailableStock();
        Integer afterStock = beforeStock + quantity;

        // ���¿��
        seckillGoods.setAvailableStock(afterStock);
        seckillGoods.setSoldCount(seckillGoods.getSoldCount() - quantity);
        seckillGoods.setUpdateTime(LocalDateTime.now());
        
        seckillGoodsMapper.update(seckillGoods);
        log.info("���ع��ɹ�����ƷID: {}, ����: {}, �ع�ǰ: {}, �ع���: {}", 
                seckillGoodsId, quantity, beforeStock, afterStock);

        // ��¼�����־
        SeckillStockLog stockLog = SeckillStockLog.builder()
                .seckillGoodsId(seckillGoodsId)
                .operationType(2) // �ͷſ��
                .quantity(quantity)
                .beforeStock(beforeStock)
                .afterStock(afterStock)
                .orderId(orderId)
                .description(reason)
                .createTime(LocalDateTime.now())
                .build();
        
        seckillStockLogMapper.insert(stockLog);
    }

    /**
     * �ع��������
     * @param orderId ����ID
     * @param userId �û�ID����Ϊnull��
     * @param reason �ع�ԭ��
     * @return �Ƿ�ع��ɹ�
     */
    @Override
    @Transactional
    public boolean rollbackOrderStock(Long orderId, Long userId, String reason) {
        log.info("�ع�������棬����ID��{}���û�ID��{}��ԭ��{}", orderId, userId, reason);
        
        try {
            // ��ѯ��ɱ������Ϣ
            SeckillOrder seckillOrder = seckillOrderMapper.getByOrderId(orderId);
            if (seckillOrder == null) {
                log.warn("��ɱ���������ڣ��޷��ع���棬����ID��{}", orderId);
                return false;
            }
            
            // �ع����
            rollbackStock(seckillOrder.getSeckillGoodsId(), seckillOrder.getQuantity(), 
                         orderId, "�������ع���" + reason);
            
            log.info("�������ع��ɹ�������ID��{}", orderId);
            return true;
            
        } catch (Exception e) {
            log.error("�������ع�ʧ�ܣ�����ID��{}", orderId, e);
            return false;
        }
    }

    /**
     * �����ع����
     * @param seckillGoodsId ��ɱ��ƷID
     * @param maxRollbackCount ���ع�����
     * @param reason �ع�ԭ��
     * @return ʵ�ʻع�����
     */
    @Override
    @Transactional
    public int batchRollbackStock(Long seckillGoodsId, Integer maxRollbackCount, String reason) {
        log.info("�����ع���棬��ƷID��{}�����ع�������{}��ԭ��{}", seckillGoodsId, maxRollbackCount, reason);
        
        try {
            // ��ѯ�ɻع��Ķ�������֧��״̬�Ķ�����
            List<SeckillOrder> rollbackOrders = seckillOrderMapper.getRollbackableOrders(
                    seckillGoodsId, maxRollbackCount);
            
            int actualRollbackCount = 0;
            for (SeckillOrder order : rollbackOrders) {
                try {
                    rollbackStock(order.getSeckillGoodsId(), order.getQuantity(), 
                                 order.getOrderId(), "�����ع���" + reason);
                    actualRollbackCount += order.getQuantity();
                } catch (Exception e) {
                    log.error("�����ع��е�������ʧ�ܣ�����ID��{}", order.getOrderId(), e);
                }
            }
            
            log.info("�����ع������ɣ���ƷID��{}��ʵ�ʻع�������{}", seckillGoodsId, actualRollbackCount);
            return actualRollbackCount;
            
        } catch (Exception e) {
            log.error("�����ع����ʧ�ܣ���ƷID��{}", seckillGoodsId, e);
            return 0;
        }
    }

    /**
     * ����ʱ�䷶Χ�ع����
     * @param seckillGoodsId ��ɱ��ƷID
     * @param startTime ��ʼʱ��
     * @param endTime ����ʱ��
     * @param reason �ع�ԭ��
     * @return �ع�������
     */
    @Override
    @Transactional
    public int rollbackStockByTimeRange(Long seckillGoodsId, String startTime, String endTime, String reason) {
        log.info("����ʱ�䷶Χ�ع���棬��ƷID��{}��ʱ�䷶Χ��{} - {}��ԭ��{}", 
                seckillGoodsId, startTime, endTime, reason);
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startTime, formatter);
            LocalDateTime end = LocalDateTime.parse(endTime, formatter);
            
            // ��ѯʱ�䷶Χ�ڵĶ���
            List<SeckillOrder> ordersInRange = seckillOrderMapper.getOrdersByTimeRange(
                    seckillGoodsId, start, end);
            
            int rollbackCount = 0;
            for (SeckillOrder order : ordersInRange) {
                try {
                    rollbackStock(order.getSeckillGoodsId(), order.getQuantity(), 
                                 order.getOrderId(), "ʱ�䷶Χ�ع���" + reason);
                    rollbackCount++;
                } catch (Exception e) {
                    log.error("ʱ�䷶Χ�ع��е�������ʧ�ܣ�����ID��{}", order.getOrderId(), e);
                }
            }
            
            log.info("ʱ�䷶Χ�ع���ɣ���ƷID��{}���ع���������{}", seckillGoodsId, rollbackCount);
            return rollbackCount;
            
        } catch (Exception e) {
            log.error("ʱ�䷶Χ�ع�ʧ�ܣ���ƷID��{}", seckillGoodsId, e);
            return 0;
        }
    }

    /**
     * �����һ����
     * @param seckillGoodsId ��ɱ��ƷID
     * @return һ���Լ����
     */
    @Override
    public Object checkStockConsistency(Long seckillGoodsId) {
        log.info("�����һ���ԣ���ƷID��{}", seckillGoodsId);
        
        try {
            // ��ȡ��Ʒ�����Ϣ
            SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillGoodsId);
            if (seckillGoods == null) {
                throw new RuntimeException("��ɱ��Ʒ������");
            }
            
            // ͳ������ɶ�����������
            Integer completedOrderQuantity = seckillOrderMapper.getCompletedOrderQuantity(seckillGoodsId);
            if (completedOrderQuantity == null) {
                completedOrderQuantity = 0;
            }
            
            // ����Ԥ�ڵĿ��ÿ��
            Integer expectedAvailableStock = seckillGoods.getTotalStock() - completedOrderQuantity;
            
            // ���һ����
            boolean isConsistent = seckillGoods.getAvailableStock().equals(expectedAvailableStock);
            
            Map<String, Object> result = new HashMap<>();
            result.put("seckillGoodsId", seckillGoodsId);
            result.put("totalStock", seckillGoods.getTotalStock());
            result.put("currentAvailableStock", seckillGoods.getAvailableStock());
            result.put("currentSoldCount", seckillGoods.getSoldCount());
            result.put("completedOrderQuantity", completedOrderQuantity);
            result.put("expectedAvailableStock", expectedAvailableStock);
            result.put("isConsistent", isConsistent);
            result.put("stockDifference", seckillGoods.getAvailableStock() - expectedAvailableStock);
            
            return result;
            
        } catch (Exception e) {
            log.error("�����һ����ʧ�ܣ���ƷID��{}", seckillGoodsId, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "���ʧ�ܣ�" + e.getMessage());
            return errorResult;
        }
    }

    /**
     * �޸���治һ������
     * @param seckillGoodsId ��ɱ��ƷID
     * @param targetStock Ŀ����ֵ
     * @param reason �޸�ԭ��
     * @return �Ƿ��޸��ɹ�
     */
    @Override
    @Transactional
    public boolean fixStockInconsistency(Long seckillGoodsId, Integer targetStock, String reason) {
        log.info("�޸���治һ�£���ƷID��{}��Ŀ���棺{}��ԭ��{}", seckillGoodsId, targetStock, reason);
        
        try {
            // ��ȡ��ǰ��Ʒ��Ϣ
            SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillGoodsId);
            if (seckillGoods == null) {
                log.error("��ɱ��Ʒ�����ڣ���ƷID��{}", seckillGoodsId);
                return false;
            }
            
            Integer beforeStock = seckillGoods.getAvailableStock();
            
            // ���Ŀ�����Ƿ����
            if (targetStock < 0 || targetStock > seckillGoods.getTotalStock()) {
                log.error("Ŀ����ֵ��������ƷID��{}��Ŀ���棺{}���ܿ�棺{}", 
                        seckillGoodsId, targetStock, seckillGoods.getTotalStock());
                return false;
            }
            
            // ���¿��
            SeckillGoods updateGoods = SeckillGoods.builder()
                    .id(seckillGoodsId)
                    .availableStock(targetStock)
                    .soldCount(seckillGoods.getTotalStock() - targetStock)
                    .updateTime(LocalDateTime.now())
                    .build();
            
            seckillGoodsMapper.update(updateGoods);
            
            // ��¼�޸���־
            SeckillStockLog stockLog = SeckillStockLog.builder()
                    .seckillGoodsId(seckillGoodsId)
                    .operationType(3) // �޸�����
                    .quantity(targetStock - beforeStock)
                    .beforeStock(beforeStock)
                    .afterStock(targetStock)
                    .description("���һ�����޸���" + reason)
                    .createTime(LocalDateTime.now())
                    .build();
            
            seckillStockLogMapper.insert(stockLog);
            
            log.info("��治һ���޸��ɹ�����ƷID��{}���޸�ǰ��{}���޸���{}", 
                    seckillGoodsId, beforeStock, targetStock);
            return true;
            
        } catch (Exception e) {
            log.error("��治һ���޸�ʧ�ܣ���ƷID��{}", seckillGoodsId, e);
            return false;
        }
    }
}
