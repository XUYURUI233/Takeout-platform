package com.sky.service;

import java.util.Map;

/**
 * Lua�ű�ִ�з���
 */
public interface SeckillLuaService {

    /**
     * ִ�б��� stock_deduct.lua �ű�
     * @param goodsId ��ɱ��ƷID
     * @param userId �û�ID
     * @param quantity ��������
     * @param limitCount �޹�����
     * @param expireSeconds �û������¼��������
     * @return ���map��code/msg/data
     */
    Map<String, Object> executeStockDeduct(Long goodsId, Long userId, Integer quantity, Integer limitCount, Integer expireSeconds);
}



