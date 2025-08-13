package com.sky.mapper;

import com.sky.entity.Seckill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ��ɱ�Mapper
 */
@Mapper
public interface SeckillMapper {

    /**
     * ������ɱ�
     * @param seckill
     */
    void insert(Seckill seckill);

    /**
     * ����ID��ѯ��ɱ�
     * @param id
     * @return
     */
    @Select("select * from seckill where id = #{id}")
    Seckill getById(Long id);

    /**
     * ��ҳ��ѯ��ɱ�
     * @param seckill
     * @return
     */
    List<Seckill> list(Seckill seckill);

    /**
     * ������ɱ�
     * @param seckill
     */
    void update(Seckill seckill);

    /**
     * ɾ����ɱ�
     * @param id
     */
    @Update("delete from seckill where id = #{id}")
    void deleteById(Long id);

    /**
     * ��ѯ��ǰ����ʾ����ɱ�
     * @param now
     * @return
     */
    @Select("select * from seckill where status = 1 and start_time <= #{now} and end_time >= #{now}")
    List<Seckill> getActiveSeckills(LocalDateTime now);
}

