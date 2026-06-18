package com.yxy.auto.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DynamicSqlMapper {
    @Select("${sql}")
    List<Map<String, Object>> executeSql(@Param("sql") String sql);
}