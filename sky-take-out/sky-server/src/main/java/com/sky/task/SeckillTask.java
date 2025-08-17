package com.sky.task;

import com.sky.service.SeckillOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ��ɱ��ض�ʱ����
 */
@Component
@Slf4j
public class SeckillTask {

    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * ����ʱδ֧������ɱ����
     * ÿ����ִ��һ��
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processExpiredOrders() {
        log.info("��ʼ����ʱδ֧������ɱ����");
        seckillOrderService.handleExpiredOrders();
        log.info("����ʱδ֧������ɱ�������");
    }
}



