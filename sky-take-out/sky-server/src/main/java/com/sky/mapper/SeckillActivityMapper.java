package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SeckillActivityPageQueryDTO;
import com.sky.entity.SeckillActivity;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 秒杀活动Mapper接口
 */
@Mapper
public interface SeckillActivityMapper {

    /**
     * 插入秒杀活动
     * @param seckillActivity
     */
    @Insert("insert into seckill_activity (name, image, start_time, end_time, status, description, create_time, update_time, create_user, update_user) " +
            "values (#{name}, #{image}, #{startTime}, #{endTime}, #{status}, #{description}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    @Options(useGeneratedKeys = true, keyProperty = "id")
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
    @AutoFill(value = OperationType.UPDATE)
    void update(SeckillActivity seckillActivity);

    /**
     * 根据条件分页查询秒杀活动
     * @param seckillActivityPageQueryDTO
     * @return
     */
    Page<SeckillActivity> pageQuery(SeckillActivityPageQueryDTO seckillActivityPageQueryDTO);

    /**
     * 根据条件统计秒杀活动数量
     * @param seckillActivity
     * @return
     */
    Integer count(SeckillActivity seckillActivity);

    /**
     * 根据ID删除秒杀活动
     * @param id
     */
    @Delete("delete from seckill_activity where id = #{id}")
    void deleteById(Long id);

    /**
     * 启用/停用秒杀活动
     * @param status
     * @param id
     * @param updateUser
     */
    @AutoFill(value = OperationType.UPDATE)
    void startOrStop(Integer status, Long id, Long updateUser);

    /**
     * 查询活跃的秒杀活动列表
     * @param now
     * @return
     */
    List<SeckillActivity> getActiveActivities(LocalDateTime now);
}