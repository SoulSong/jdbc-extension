package com.shf.jdbc.extension.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shf.jdbc.extension.mybatis.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * description :
 *
 * @author songhaifeng
 * @date 2021/4/26 19:49
 */
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT id,name FROM user WHERE name = #{name}")
    List<User> findByName(@Param("name") String name);

    int batchInsert(List<User> users);

    @Select("SELECT * FROM user WHERE ${column} = #{value}")
    List<User> findByColumn(@Param("column") String column, @Param("value") String value);

    User loadUserById(@Param("id") int id);
}
