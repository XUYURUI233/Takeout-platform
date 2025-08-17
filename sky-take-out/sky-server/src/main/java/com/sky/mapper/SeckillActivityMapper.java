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
 * ��ɱ�Mapper�ӿ�
 */
@Mapper
public interface SeckillActivityMapper {

    /**
     * ������ɱ�
     * @param seckillActivity
     */
    @Insert("insert into seckill_activity (name, image, start_time, end_time, status, description, create_time, update_time, create_user, update_user) " +
            "values (#{name}, #{image}, #{startTime}, #{endTime}, #{status}, #{description}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    @Options(useGeneratedKeys = true, keyProperty = "id")
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
    @AutoFill(value = OperationType.UPDATE)
    void update(SeckillActivity seckillActivity);

    /**
     * ����������ҳ��ѯ��ɱ�
     * @param seckillActivityPageQueryDTO
     * @return
     */
    Page<SeckillActivity> pageQuery(SeckillActivityPageQueryDTO seckillActivityPageQueryDTO);

    /**
     * ��������ͳ����ɱ�����
     * @param seckillActivity
     * @return
     */
    Integer count(SeckillActivity seckillActivity);

    /**
     * ����IDɾ����ɱ�
     * @param id
     */
    @Delete("delete from seckill_activity where id = #{id}")
    void deleteById(Long id);

    /**
     * ����/ͣ����ɱ�
     * @param status
     * @param id
     * @param updateUser
     */
    @AutoFill(value = OperationType.UPDATE)
    void startOrStop(Integer status, Long id, Long updateUser);

    /**
     * ��ѯ��Ծ����ɱ��б�
     * @param now
     * @return
     */
    List<SeckillActivity> getActiveActivities(LocalDateTime now);
}