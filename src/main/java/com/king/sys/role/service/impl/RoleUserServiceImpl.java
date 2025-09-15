package com.king.sys.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.sys.role.entity.TSysRoleUser;
import com.king.sys.role.mapper.RoleUserMapper;
import com.king.sys.role.service.IRoleUserService;
import com.king.sys.user.entity.TSysUser;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("roleUserServiceImpl")
public class RoleUserServiceImpl extends ServiceImpl<RoleUserMapper, TSysRoleUser> implements IRoleUserService {

    @Override
    public Map<String, Object> getRoleUsers(String roleId, String userName, Long pageNo, Long pageSize) {
        Assert.isTrue(StringUtils.hasText(roleId), "角色ID不能为空");
        
        // 使用自定义查询方法
        List<TSysUser> users = this.baseMapper.selectUsersByRoleId(roleId, userName);
        
        // 手动分页
        int total = users.size();
        int start = (int) ((pageNo - 1) * pageSize);
        int end = Math.min(start + pageSize.intValue(), total);
        
        List<TSysUser> pageUsers = users.subList(start, end);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("rows", pageUsers);
        
        return result;
    }

    @Override
    public Map<String, Object> getAvailableUsers(String roleId, String userName, Long pageNo, Long pageSize) {
        Assert.isTrue(StringUtils.hasText(roleId), "角色ID不能为空");
        
        // 使用自定义查询方法
        List<TSysUser> users = this.baseMapper.selectAvailableUsersByRoleId(roleId, userName);
        
        // 手动分页
        int total = users.size();
        int start = (int) ((pageNo - 1) * pageSize);
        int end = Math.min(start + pageSize.intValue(), total);
        
        List<TSysUser> pageUsers = users.subList(start, end);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("rows", pageUsers);
        
        return result;
    }

    @Override
    public void addUserRole(String roleId, String userId) {
        Assert.isTrue(StringUtils.hasText(roleId), "角色ID不能为空");
        Assert.isTrue(StringUtils.hasText(userId), "用户ID不能为空");
        
        // 检查关联是否已存在
        LambdaQueryWrapper<TSysRoleUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TSysRoleUser::getRoleId, roleId)
               .eq(TSysRoleUser::getUserId, userId);
        TSysRoleUser existing = this.baseMapper.selectOne(wrapper);
        Assert.isTrue(existing == null, "用户角色关联已存在");
        
        // 创建新的关联
        TSysRoleUser roleUser = new TSysRoleUser();
        roleUser.setRoleId(roleId);
        roleUser.setUserId(userId);
        
        this.baseMapper.insert(roleUser);
    }

    @Override
    public void removeUserRole(String roleId, String userId) {
        Assert.isTrue(StringUtils.hasText(roleId), "角色ID不能为空");
        Assert.isTrue(StringUtils.hasText(userId), "用户ID不能为空");
        
        // 查找并删除关联
        LambdaQueryWrapper<TSysRoleUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TSysRoleUser::getRoleId, roleId)
               .eq(TSysRoleUser::getUserId, userId);
        
        TSysRoleUser existing = this.baseMapper.selectOne(wrapper);
        Assert.notNull(existing, "用户角色关联不存在");
        
        this.baseMapper.delete(wrapper);
    }
}
