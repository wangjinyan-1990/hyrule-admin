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

import java.util.List;
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
    public List<UserSystemInfoDTO> getUsersByRoleId(String roleId) {
        Assert.hasText(roleId, "角色ID不能为空");
        
        return baseMapper.getUsersByRoleId(roleId);
    }
}
