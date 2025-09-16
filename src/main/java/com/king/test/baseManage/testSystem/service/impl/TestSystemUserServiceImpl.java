package com.king.test.baseManage.testSystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.test.baseManage.testSystem.entity.TTestSystemUser;
import com.king.test.baseManage.testSystem.mapper.TestSystemUserMapper;
import com.king.test.baseManage.testSystem.service.ITestSystemUserService;
import com.king.test.baseManage.testSystem.dto.UserSystemInfoDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 测试系统用户Service实现类
 */
@Service("testSystemUserServiceImpl")
public class TestSystemUserServiceImpl extends ServiceImpl<TestSystemUserMapper, TTestSystemUser> implements ITestSystemUserService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateSystemUsers(String systemId, List<String> userIds) {
        Assert.hasText(systemId, "系统ID不能为空");
        Assert.notEmpty(userIds, "用户ID列表不能为空");
        
        // 获取当前系统已分配的用户ID列表
        List<String> currentUserIds = getUserIdsBySystemId(systemId);
        
        // 找出需要添加的用户（新用户中不在当前用户中的）
        List<String> usersToAdd = userIds.stream()
                .filter(userId -> !currentUserIds.contains(userId))
                .collect(Collectors.toList());
        
        // 找出需要删除的用户（当前用户中不在新用户中的）
        List<String> usersToRemove = currentUserIds.stream()
                .filter(userId -> !userIds.contains(userId))
                .collect(Collectors.toList());
        
        // 删除需要移除的用户关系
        if (!usersToRemove.isEmpty()) {
            removeSystemUsers(systemId, usersToRemove);
        }
        
        // 添加新的用户关系
        if (!usersToAdd.isEmpty()) {
            for (String userId : usersToAdd) {
                TTestSystemUser systemUser = new TTestSystemUser();
                systemUser.setSystemId(systemId);
                systemUser.setUserId(userId);
                baseMapper.insert(systemUser);
            }
        }
        
        return true;
    }
    
    @Override
    public List<String> getUserIdsBySystemId(String systemId) {
        Assert.hasText(systemId, "系统ID不能为空");
        
        LambdaQueryWrapper<TTestSystemUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TTestSystemUser::getSystemId, systemId)
               .select(TTestSystemUser::getUserId);
        
        List<TTestSystemUser> list = baseMapper.selectList(wrapper);
        return list.stream()
                .map(TTestSystemUser::getUserId)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getSystemIdsByUserId(String userId) {
        Assert.hasText(userId, "用户ID不能为空");
        
        LambdaQueryWrapper<TTestSystemUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TTestSystemUser::getUserId, userId)
               .select(TTestSystemUser::getSystemId);
        
        List<TTestSystemUser> list = baseMapper.selectList(wrapper);
        return list.stream()
                .map(TTestSystemUser::getSystemId)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeSystemUsers(String systemId, List<String> userIds) {
        Assert.hasText(systemId, "系统ID不能为空");
        Assert.notEmpty(userIds, "用户ID列表不能为空");
        
        LambdaQueryWrapper<TTestSystemUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TTestSystemUser::getSystemId, systemId)
               .in(TTestSystemUser::getUserId, userIds);
        
        return baseMapper.delete(wrapper) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeAllUsersBySystemId(String systemId) {
        Assert.hasText(systemId, "系统ID不能为空");
        
        LambdaQueryWrapper<TTestSystemUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TTestSystemUser::getSystemId, systemId);
        
        return baseMapper.delete(wrapper) > 0;
    }
    
    @Override
    public Map<String, Object> getUsersByRoleId(String roleId, Long pageNo, Long pageSize, String userName, String loginName, String phone) {
        Assert.hasText(roleId, "角色ID不能为空");
        
        // 使用自定义查询方法
        List<UserSystemInfoDTO> users = baseMapper.getUsersByRoleId(roleId, userName, loginName, phone);
        
        // 手动分页
        int total = users.size();
        int start = (int) ((pageNo - 1) * pageSize);
        int end = Math.min(start + pageSize.intValue(), total);
        
        List<UserSystemInfoDTO> pageUsers = users.subList(start, end);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("rows", pageUsers);
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserSystems(String userId, List<String> systemIds) {
        Assert.hasText(userId, "用户ID不能为空");
        
        // 如果systemIds为null，则清空用户的所有系统关系
        final List<String> targetSystemIds = systemIds == null ? new java.util.ArrayList<>() : systemIds;
        
        // 获取用户当前已分配的系统ID列表
        List<String> currentSystemIds = getSystemIdsByUserId(userId);
        
        // 找出需要添加的系统（新系统中不在当前系统中的）
        List<String> systemsToAdd = targetSystemIds.stream()
                .filter(systemId -> !currentSystemIds.contains(systemId))
                .collect(Collectors.toList());
        
        // 找出需要删除的系统（当前系统中不在新系统中的）
        List<String> systemsToRemove = currentSystemIds.stream()
                .filter(systemId -> !targetSystemIds.contains(systemId))
                .collect(Collectors.toList());
        
        // 删除需要移除的系统关系
        if (!systemsToRemove.isEmpty()) {
            for (String systemId : systemsToRemove) {
                LambdaQueryWrapper<TTestSystemUser> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(TTestSystemUser::getUserId, userId)
                       .eq(TTestSystemUser::getSystemId, systemId);
                baseMapper.delete(wrapper);
            }
        }
        
        // 添加新的系统关系
        if (!systemsToAdd.isEmpty()) {
            for (String systemId : systemsToAdd) {
                TTestSystemUser systemUser = new TTestSystemUser();
                systemUser.setUserId(userId);
                systemUser.setSystemId(systemId);
                baseMapper.insert(systemUser);
            }
        }
        
        return true;
    }
}
