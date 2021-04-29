package com.shf.jdbc.extension.mybatis.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * description :
 *
 * @author songhaifeng
 * @date 2021/4/26 23:18
 */
public interface DatabaseMapper {

    void dropTable(@Param("tableName") String tableName);

    void createTable(@Param("sql") String sql);

}
