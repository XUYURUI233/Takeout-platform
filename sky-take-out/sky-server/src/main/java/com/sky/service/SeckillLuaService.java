package com.sky.service;

import java.util.Map;

/**
 * Lua脚本执行服务
 */
public interface SeckillLuaService {

    /**
     * 执行本地 stock_deduct.lua 脚本
     * @param goodsId 秒杀商品ID
     * @param userId 用户ID
     * @param quantity 购买数量
     * @param limitCount 限购数量
     * @param expireSeconds 用户购买记录过期秒数
     * @return 结果map：code/msg/data
     */
    Map<String, Object> executeStockDeduct(Long goodsId, Long userId, Integer quantity, Integer limitCount, Integer expireSeconds);
}



