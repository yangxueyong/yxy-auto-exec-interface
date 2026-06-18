package com.yxy.auto.service;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface DynamicSqlService {
    Map<String, Object> executeSql(String sql);
}