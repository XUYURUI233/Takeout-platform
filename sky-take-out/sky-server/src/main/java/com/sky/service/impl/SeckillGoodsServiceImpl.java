package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.SeckillConstant;
import com.sky.context.BaseContext;
import com.sky.dto.SeckillGoodsDTO;
import com.sky.entity.SeckillGoods;
import com.sky.entity.SeckillStockLog;
import com.sky.entity.SeckillUserRecord;
import com.sky.exception.BaseException;
import com.sky.mapper.SeckillGoodsMapper;
import com.sky.mapper.SeckillOrderMapper;
import com.sky.mapper.SeckillStockLogMapper;
import com.sky.mapper.SeckillUserRecordMapper;
import com.sky.service.SeckillGoodsService;
import com.sky.vo.SeckillGoodsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ��ɱ��ƷServiceʵ����
 */
@Service
@Slf4j
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private SeckillUserRecordMapper seckillUserRecordMapper;
    @Autowired
    private SeckillStockLogMapper seckillStockLogMapper;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    /**
     * ��ѯ������Ʒ�б�
     * @param type
     * @param name
     * @return
     */
    @Override
    public List<SeckillGoodsVO> getAvailableGoods(Integer type, String name) {
        List<com.sky.vo.AvailableGoodsVO> availableGoods = seckillGoodsMapper.getAvailableGoods(type, name);
        List<SeckillGoodsVO> voList = new ArrayList<>();
        
        for (com.sky.vo.AvailableGoodsVO availableGood : availableGoods) {
            SeckillGoodsVO vo = new SeckillGoodsVO();
            // ������Ʒ������Ϣ���ֶ�����ǰ��ƥ��
            vo.setId(availableGood.getId());  // ǰ����Ҫ���� id
            vo.setName(availableGood.getName());  // ǰ����Ҫ���� name
            vo.setImage(availableGood.getImage());  // ǰ����Ҫ���� image
            vo.setPrice(availableGood.getPrice());  // ǰ����Ҫ���� price
            vo.setType(availableGood.getType());  // ǰ����Ҫ���� type
            vo.setCategoryName(availableGood.getCategoryName());
            
            // ͬʱ����ԭ���ֶ��Ա��ּ�����
            vo.setGoodsId(availableGood.getId());
            vo.setGoodsName(availableGood.getName());
            vo.setGoodsImage(availableGood.getImage());
            vo.setOriginalPrice(availableGood.getPrice());
            vo.setGoodsType(availableGood.getType());
            voList.add(vo);
        }
        
        return voList;
    }

    /**
     * �޸���ɱ��Ʒ��Ϣ
     * @param seckillGoodsDTO
     */
    @Override
    public void update(SeckillGoodsDTO seckillGoodsDTO) {
        SeckillGoods seckillGoods = new SeckillGoods();
        BeanUtils.copyProperties(seckillGoodsDTO, seckillGoods);
        seckillGoodsMapper.update(seckillGoods);
    }

    /**
     * ɾ����ɱ��Ʒ
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        seckillGoodsMapper.deleteById(id);
    }

    /**
     * ���ݻid��ѯ��ɱ��Ʒ�б�
     * @param activityId
     * @return
     */
    @Override
    public List<SeckillGoodsVO> getByActivityId(Long activityId) {
        List<SeckillGoods> goodsList = seckillGoodsMapper.getByActivityId(activityId);
        List<SeckillGoodsVO> voList = new ArrayList<>();
        
        Long userId = BaseContext.getCurrentId();
        for (SeckillGoods goods : goodsList) {
            SeckillGoodsVO vo = new SeckillGoodsVO();
            BeanUtils.copyProperties(goods, vo);
            
            // ��ѯ�û������¼
            if (userId != null) {
                SeckillUserRecord record = seckillUserRecordMapper.getByActivityGoodsUser(
                    activityId, goods.getId(), userId);
                if (record != null) {
                    vo.setUserPurchased(record.getQuantity());
                    vo.setCanPurchase(record.getQuantity() < goods.getLimitCount());
                } else {
                    vo.setUserPurchased(0);
                    vo.setCanPurchase(true);
                }
            }
            
            voList.add(vo);
        }
        
        return voList;
    }

    /**
     * ��ѯ��ɱ��Ʒ����
     * @param id
     * @return
     */
    @Override
    public SeckillGoodsVO getById(Long id) {
        SeckillGoods goods = seckillGoodsMapper.getById(id);
        if (goods == null) {
            throw new BaseException(MessageConstant.SECKILL_GOODS_NOT_EXISTS);
        }
        
        SeckillGoodsVO vo = new SeckillGoodsVO();
        BeanUtils.copyProperties(goods, vo);
        
        // ��ѯ�û������¼
        Long userId = BaseContext.getCurrentId();
        if (userId != null) {
            SeckillUserRecord record = seckillUserRecordMapper.getByActivityGoodsUser(
                goods.getActivityId(), goods.getId(), userId);
            if (record != null) {
                vo.setUserPurchased(record.getQuantity());
                vo.setCanPurchase(record.getQuantity() < goods.getLimitCount());
            } else {
                vo.setUserPurchased(0);
                vo.setCanPurchase(true);
            }
        }
        
        return vo;
    }

    /**
     * �����Ʒ���
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    @Override
    public boolean checkStock(Long seckillGoodsId, Integer quantity) {
        SeckillGoods goods = seckillGoodsMapper.getById(seckillGoodsId);
        if (goods == null) {
            return false;
        }
        return goods.getAvailableStock() >= quantity;
    }

    /**
     * �ۼ����
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    @Override
    @Transactional
    public boolean decreaseStock(Long seckillGoodsId, Integer quantity) {
        // ��ѯ��ǰ��Ʒ��Ϣ
        SeckillGoods goods = seckillGoodsMapper.getById(seckillGoodsId);
        if (goods == null) {
            throw new BaseException(MessageConstant.SECKILL_GOODS_NOT_EXISTS);
        }

        // ��¼�ۼ�ǰ���
        int beforeStock = goods.getAvailableStock();
        
        // ʹ���ֹ����ۼ����
        int rows = seckillGoodsMapper.deductStock(seckillGoodsId, quantity, goods.getVersion());
        
        if (rows > 0) {
            // ��¼��������־
            SeckillStockLog stockLog = SeckillStockLog.builder()
                .seckillGoodsId(seckillGoodsId)
                .userId(BaseContext.getCurrentId())
                .operationType(SeckillConstant.STOCK_OP_DEDUCT)
                .quantity(quantity)
                .beforeStock(beforeStock)
                .afterStock(beforeStock - quantity)
                .version(goods.getVersion())
                .remark("�û��µ��ۼ����")
                .createTime(LocalDateTime.now())
                .build();
            seckillStockLogMapper.insert(stockLog);
            
            return true;
        }
        
        return false;
    }

    /**
     * �ͷſ��
     * @param seckillGoodsId
     * @param quantity
     */
    @Override
    @Transactional
    public void releaseStock(Long seckillGoodsId, Integer quantity) {
        // ��ѯ��ǰ��Ʒ��Ϣ
        SeckillGoods goods = seckillGoodsMapper.getById(seckillGoodsId);
        if (goods == null) {
            return;
        }

        // ��¼�ͷ�ǰ���
        int beforeStock = goods.getAvailableStock();
        
        // �ͷſ��
        int rows = seckillGoodsMapper.releaseStock(seckillGoodsId, quantity);
        
        if (rows > 0) {
            // ��¼��������־
            SeckillStockLog stockLog = SeckillStockLog.builder()
                .seckillGoodsId(seckillGoodsId)
                .userId(BaseContext.getCurrentId())
                .operationType(SeckillConstant.STOCK_OP_RELEASE)
                .quantity(quantity)
                .beforeStock(beforeStock)
                .afterStock(beforeStock + quantity)
                .version(goods.getVersion())
                .remark("����ȡ���ͷſ��")
                .createTime(LocalDateTime.now())
                .build();
            seckillStockLogMapper.insert(stockLog);
        }
    }

    /**
     * �ۼ���棨����������
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    @Override
    public boolean deductStock(Long seckillGoodsId, Integer quantity) {
        return decreaseStock(seckillGoodsId, quantity);
    }

    /**
     * ����û������ʸ�
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    @Override
    public boolean checkEligibility(Long seckillGoodsId, Integer quantity) {
        // �����
        if (!checkStock(seckillGoodsId, quantity)) {
            return false;
        }
        
        // ����û��޹�
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            return false;
        }
        
        // ��ȡ�û��ѹ�������
        Integer userBoughtCount = seckillOrderMapper.getUserBoughtCount(seckillGoodsId, userId);
        if (userBoughtCount == null) {
            userBoughtCount = 0;
        }
        
        // ��ȡ��Ʒ�޹�����
        SeckillGoods goods = seckillGoodsMapper.getById(seckillGoodsId);
        if (goods == null) {
            return false;
        }
        
        // ����Ƿ񳬹��޹�����
        return userBoughtCount + quantity <= goods.getLimitCount();
    }
}


