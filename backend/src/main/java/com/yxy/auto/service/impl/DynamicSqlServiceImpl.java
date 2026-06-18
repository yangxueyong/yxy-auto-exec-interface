package com.yxy.auto.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.yxy.auto.mapper.DynamicSqlMapper;
import com.yxy.auto.service.DynamicSqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DynamicSqlServiceImpl implements DynamicSqlService {
    @Autowired
    private DynamicSqlMapper dynamicSqlMapper;

    @Override
    public Map<String, Object> executeSql(String sql) {
        List<Map<String, Object>> maps = dynamicSqlMapper.executeSql(sql);
        if(CollUtil.isEmpty(maps)){
            return new HashMap<>();
        }
        return maps.getFirst();
    }
}
