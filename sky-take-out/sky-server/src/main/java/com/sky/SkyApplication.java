package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
@Slf4j
@EnableCaching//开发缓存注解功能
@EnableScheduling //开启任务调度
public class SkyApplication implements CommandLineRunner {
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
        System.out.println("server started");
        log.info("server started");
    }
    
    /**
     * 应用启动时初始化店铺状态
     */
    @Override
    public void run(String... args) throws Exception {
        // 初始化店铺状态，如果Redis中没有则设置为营业中(1)
        String shopStatusKey = "SHOP_STATUS";
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get(shopStatusKey);
        if (shopStatus == null) {
            redisTemplate.opsForValue().set(shopStatusKey, 1);
            log.info("应用启动：初始化店铺状态为营业中");
        } else {
            log.info("应用启动：店铺状态已存在，当前状态为：{}", shopStatus == 1 ? "营业中" : "打烊中");
        }
    }
}
