package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.SeckillOrder;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ��ɱ����Mapper�ӿ�
 */
@Mapper
public interface SeckillOrderMapper {

    /**
     * ������ɱ����
     * @param seckillOrder
     */
    @AutoFill(OperationType.INSERT)
    void insert(SeckillOrder seckillOrder);

    /**
     * ����ID��ѯ��ɱ����
     * @param id
     * @return
     */
    @Select("select * from seckill_order where id = #{id}")
    SeckillOrder getById(Long id);

    /**
     * ���ݶ����ź��û�ID��ѯ��ɱ����
     * @param orderNumber
     * @param userId
     * @return
     */
    @Select("select * from seckill_order where number = #{orderNumber} and user_id = #{userId}")
    SeckillOrder getByNumberAndUserId(String orderNumber, Long userId);

    /**
     * ���ݶ���ID��ѯ��ɱ����
     * @param orderId
     * @return
     */
    @Select("select * from seckill_order where order_id = #{orderId}")
    SeckillOrder getByOrderId(Long orderId);

    /**
     * ������ɱ����
     * @param seckillOrder
     */
    @AutoFill(OperationType.UPDATE)
    void update(SeckillOrder seckillOrder);

    /**
     * �û��˷�ҳ��ѯ��ɱ����
     * @param userId
     * @param status
     * @return
     */
    Page<SeckillOrder> pageQueryByUser(Long userId, Integer status);

    /**
     * ��ѯ��ʱδ֧������ɱ����
     * @param currentTime
     * @return
     */
    @Select("select * from seckill_order where status = 1 and pay_expire_time < #{currentTime}")
    List<SeckillOrder> getExpiredOrders(LocalDateTime currentTime);

    /**
     * ��ѯ�ɻع��Ķ�������֧��״̬��
     * @param seckillGoodsId ��ɱ��ƷID
     * @param maxCount �������
     * @return �ɻع������б�
     */
    List<SeckillOrder> getRollbackableOrders(Long seckillGoodsId, Integer maxCount);

    /**
     * ����ʱ�䷶Χ��ѯ����
     * @param seckillGoodsId ��ɱ��ƷID
     * @param startTime ��ʼʱ��
     * @param endTime ����ʱ��
     * @return �����б�
     */
    List<SeckillOrder> getOrdersByTimeRange(Long seckillGoodsId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * ͳ������ɶ�����������
     * @param seckillGoodsId ��ɱ��ƷID
     * @return ����ɶ���������
     */
    Integer getCompletedOrderQuantity(Long seckillGoodsId);

    /**
     * ͳ�ƻ�ܶ�����
     * @param activityId �ID
     * @param beginDate ��ʼ����
     * @param endDate ��������
     * @return
     */
    Integer countTotalOrders(Long activityId, String beginDate, String endDate);

    /**
     * ͳ�ƻ�ɹ�������
     * @param activityId �ID
     * @param beginDate ��ʼ����
     * @param endDate ��������
     * @return
     */
    Integer countSuccessOrders(Long activityId, String beginDate, String endDate);

    /**
     * ͳ�ƻ�ܽ��
     * @param activityId �ID
     * @param beginDate ��ʼ����
     * @param endDate ��������
     * @return
     */
    Double sumTotalAmount(Long activityId, String beginDate, String endDate);
}