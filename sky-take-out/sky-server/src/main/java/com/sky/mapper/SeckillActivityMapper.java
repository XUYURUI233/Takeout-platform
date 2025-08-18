package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.SeckillActivity;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ��ɱ�Mapper�ӿ�
 */
@Mapper
public interface SeckillActivityMapper {

    /**
     * ��ҳ��ѯ��ɱ�
     * @param name �����
     * @param status �״̬
     * @return
     */
    Page<SeckillActivity> pageQuery(String name, Integer status);

    /**
     * ������ɱ�
     * @param seckillActivity
     */
    @AutoFill(OperationType.INSERT)
    void insert(SeckillActivity seckillActivity);

    /**
     * ����ID��ѯ��ɱ�
     * @param id
     * @return
     */
    @Select("select * from seckill_activity where id = #{id}")
    SeckillActivity getById(Long id);

    /**
     * �޸���ɱ�
     * @param seckillActivity
     */
    @AutoFill(OperationType.UPDATE)
    void update(SeckillActivity seckillActivity);

    /**
     * ����IDɾ����ɱ�
     * @param id
     */
    @Delete("delete from seckill_activity where id = #{id}")
    void deleteById(Long id);

    /**
     * ��ѯ�����е���ɱ�
     * @param currentTime ��ǰʱ��
     * @return
     */
    @Select("select * from seckill_activity where status = 1 and start_time <= #{currentTime} and end_time >= #{currentTime}")
    List<SeckillActivity> getActiveActivities(LocalDateTime currentTime);
}



