package com.yxy.auto.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yxy.auto.dto.UserInfoDTO;
import com.yxy.auto.entity.UserInfo;
import com.yxy.auto.io.QueryUserInfoIO;
import com.yxy.auto.mapper.UserInfoMapper;
import com.yxy.auto.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {
    
    private final UserInfoMapper userInfoMapper;

    @Override
    public List<UserInfoDTO> getByIo(QueryUserInfoIO io) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(io.getName() != null, UserInfo::getUserName, io.getName());
        List<UserInfo> entities = userInfoMapper.selectList(wrapper);
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
//        return entities;
    }

//    @Override
//    public List<ApiDefinitionDTO> listAll() {
//        LambdaQueryWrapper<ApiDefinition> wrapper = new LambdaQueryWrapper<>();
//        wrapper.orderByDesc(ApiDefinition::getCreatedTime);
//        List<ApiDefinition> entities = apiDefinitionMapper.selectList(wrapper);
//        return entities.stream()
//                .map(this::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public ApiDefinitionDTO getById(Long id) {
//        ApiDefinition entity = apiDefinitionMapper.selectById(id);
//        return toDTO(entity);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public ApiDefinitionDTO create(ApiDefinitionDTO dto) {
//        ApiDefinition entity = new ApiDefinition();
//        BeanUtil.copyProperties(dto, entity);
//        // 处理responseRuleIds
//        if (dto.getResponseRuleIds() != null && !dto.getResponseRuleIds().isEmpty()) {
//            entity.setResponseRuleIds(dto.getResponseRuleIds());
//        }
//        apiDefinitionMapper.insert(entity);
//        dto.setId(entity.getId());
//        return enrichDTO(dto);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public ApiDefinitionDTO update(ApiDefinitionDTO dto) {
//        ApiDefinition entity = new ApiDefinition();
//        BeanUtil.copyProperties(dto, entity);
//        if (dto.getResponseRuleIds() != null && !dto.getResponseRuleIds().isEmpty()) {
//            entity.setResponseRuleIds(dto.getResponseRuleIds());
//        }
//        apiDefinitionMapper.updateById(entity);
//        return enrichDTO(dto);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void delete(Long id) {
//        apiDefinitionMapper.deleteById(id);
//    }

    private UserInfoDTO toDTO(UserInfo entity) {
        if (entity == null) {
            return null;
        }
        UserInfoDTO dto = new UserInfoDTO();
        BeanUtil.copyProperties(entity, dto);

        return dto;
    }



}