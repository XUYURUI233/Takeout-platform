package com.sky.mapper;

import com.sky.entity.Seckill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 秒杀活动Mapper
 */
@Mapper
public interface SeckillMapper {

    /**
     * 插入秒杀活动
     * @param seckill
     */
    void insert(Seckill seckill);

    /**
     * 根据ID查询秒杀活动
     * @param id
     * @return
     */
    @Select("select * from seckill where id = #{id}")
    Seckill getById(Long id);

    /**
     * 分页查询秒杀活动
     * @param seckill
     * @return
     */
    List<Seckill> list(Seckill seckill);

    /**
     * 更新秒杀活动
     * @param seckill
     */
    void update(Seckill seckill);

    /**
     * 删除秒杀活动
     * @param id
     */
    @Update("delete from seckill where id = #{id}")
    void deleteById(Long id);

    /**
     * 查询当前可显示的秒杀活动
     * @param now
     * @return
     */
    @Select("select * from seckill where status = 1 and start_time <= #{now} and end_time >= #{now}")
    List<Seckill> getActiveSeckills(LocalDateTime now);
}

