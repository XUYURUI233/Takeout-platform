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
 * 秒杀活动Mapper接口
 */
@Mapper
public interface SeckillActivityMapper {

    /**
     * 分页查询秒杀活动
     * @param name 活动名称
     * @param status 活动状态
     * @return
     */
    Page<SeckillActivity> pageQuery(String name, Integer status);

    /**
     * 新增秒杀活动
     * @param seckillActivity
     */
    @AutoFill(OperationType.INSERT)
    void insert(SeckillActivity seckillActivity);

    /**
     * 根据ID查询秒杀活动
     * @param id
     * @return
     */
    @Select("select * from seckill_activity where id = #{id}")
    SeckillActivity getById(Long id);

    /**
     * 修改秒杀活动
     * @param seckillActivity
     */
    @AutoFill(OperationType.UPDATE)
    void update(SeckillActivity seckillActivity);

    /**
     * 根据ID删除秒杀活动
     * @param id
     */
    @Delete("delete from seckill_activity where id = #{id}")
    void deleteById(Long id);

    /**
     * 查询进行中的秒杀活动
     * @param currentTime 当前时间
     * @return
     */
    @Select("select * from seckill_activity where status = 1 and start_time <= #{currentTime} and end_time >= #{currentTime}")
    List<SeckillActivity> getActiveActivities(LocalDateTime currentTime);
}



