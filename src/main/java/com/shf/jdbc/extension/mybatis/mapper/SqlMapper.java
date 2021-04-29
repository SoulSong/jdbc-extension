package com.shf.jdbc.extension.mybatis.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * description :
 *
 * @author songhaifeng
 * @date 2021/4/26 23:50
 */
public interface SqlMapper {

    List<Map<String, Object>> query(@Param("query") String query);

}
