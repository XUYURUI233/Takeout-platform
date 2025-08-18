package com.sky.task;

import com.sky.service.SeckillOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 秒杀相关定时任务
 */
@Component
@Slf4j
public class SeckillTask {

    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 处理超时未支付的秒杀订单
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processExpiredOrders() {
        log.info("开始处理超时未支付的秒杀订单");
        seckillOrderService.handleExpiredOrders();
        log.info("处理超时未支付的秒杀订单完成");
    }
}



