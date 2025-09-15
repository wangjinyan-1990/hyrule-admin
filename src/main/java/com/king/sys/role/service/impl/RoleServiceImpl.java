package com.king.sys.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.utils.DateUtil;
import com.king.sys.role.entity.TSysRole;
import com.king.sys.role.mapper.RoleMapper;
import com.king.sys.role.service.IRoleService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;

@Service("roleServiceImpl")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, TSysRole> implements IRoleService {

    @Override
    public void createRole(TSysRole role) {
        Assert.notNull(role, "角色不能为空");
        
        // 校验必填字段
        Assert.isTrue(StringUtils.hasText(role.getRoleName()), "角色名称不能为空");
        
        // 校验角色名称唯一性
        validateRoleNameUniqueForCreate(role.getRoleName());
        
        // 生成角色ID（从数据库查询最大ID并加1）
        if (!StringUtils.hasText(role.getRoleId())) {
            String maxRoleId = getMaxRoleId();
            String newRoleId = generateNextRoleId(maxRoleId);
            role.setRoleId(newRoleId);
        }
        
        // 设置排序号（如果没有提供）
        if (role.getSortNo() == null) {
            String currentDateStr = DateUtil.getDateFormatYMD();
            role.setSortNo(Integer.parseInt(currentDateStr));
        }
        
        this.baseMapper.insert(role);
    }

    @Override
    public void updateRole(TSysRole role) {
        Assert.notNull(role, "角色不能为空");
        Assert.isTrue(StringUtils.hasText(role.getRoleId()), "角色ID不能为空");
        
        // 检查角色是否存在
        TSysRole existingRole = this.baseMapper.selectById(role.getRoleId());
        Assert.notNull(existingRole, "角色不存在");
        
        // 校验必填字段
        Assert.isTrue(StringUtils.hasText(role.getRoleName()), "角色名称不能为空");
        
        // 校验角色名称唯一性（只有角色名称发生变化时才校验）
        if (!existingRole.getRoleName().equals(role.getRoleName())) {
            validateRoleNameUniqueForUpdate(role.getRoleName(), role.getRoleId());
        }
        
        // 构建更新对象，只更新允许的字段（不更新roleId）
        TSysRole toUpdate = new TSysRole();
        toUpdate.setRoleId(role.getRoleId()); // 保持原有roleId不变
        toUpdate.setRoleName(role.getRoleName());
        toUpdate.setSortNo(role.getSortNo());
        toUpdate.setRemark(role.getRemark());
        
        this.baseMapper.updateById(toUpdate);
    }

    @Override
    public void deleteRole(String roleId) {
        Assert.isTrue(StringUtils.hasText(roleId), "角色ID不能为空");
        
        // 检查角色是否存在
        TSysRole existingRole = this.baseMapper.selectById(roleId);
        Assert.notNull(existingRole, "角色不存在");
        
        // 检查角色是否被用户使用（这里可以添加业务逻辑检查）
        // validateRoleNotInUse(roleId);
        
        this.baseMapper.deleteById(roleId);
    }

    /**
     * 校验角色名称唯一性（创建角色时使用）
     * @param roleName 角色名称
     */
    private void validateRoleNameUniqueForCreate(String roleName) {
        if (StringUtils.hasText(roleName)) {
            LambdaQueryWrapper<TSysRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TSysRole::getRoleName, roleName);
            TSysRole exist = this.baseMapper.selectOne(wrapper);
            Assert.isTrue(exist == null, "角色名称已存在");
        }
    }

    /**
     * 校验角色名称唯一性（更新角色时使用）
     * @param roleName 角色名称
     * @param currentRoleId 当前角色ID
     */
    private void validateRoleNameUniqueForUpdate(String roleName, String currentRoleId) {
        if (StringUtils.hasText(roleName)) {
            LambdaQueryWrapper<TSysRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TSysRole::getRoleName, roleName)
                   .ne(TSysRole::getRoleId, currentRoleId);
            TSysRole exist = this.baseMapper.selectOne(wrapper);
            Assert.isTrue(exist == null, "角色名称已被其他角色使用");
        }
    }

    /**
     * 获取数据库中最大的角色ID
     * @return 最大的角色ID，如果没有数据则返回"0000"
     */
    private String getMaxRoleId() {
        LambdaQueryWrapper<TSysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(TSysRole::getRoleId).last("LIMIT 1");
        TSysRole maxRole = this.baseMapper.selectOne(wrapper);
        
        if (maxRole != null && StringUtils.hasText(maxRole.getRoleId())) {
            return maxRole.getRoleId();
        }
        return "0000"; // 如果没有数据，从0000开始
    }

    /**
     * 生成下一个角色ID
     * @param currentMaxId 当前最大ID
     * @return 下一个角色ID
     */
    private String generateNextRoleId(String currentMaxId) {
        try {
            // 将字符串转换为数字，加1，然后格式化为4位数字符串
            int currentId = Integer.parseInt(currentMaxId);
            int nextId = currentId + 1;
            return String.format("%04d", nextId);
        } catch (NumberFormatException e) {
            // 如果当前ID不是数字格式，从0001开始
            return "0001";
        }
    }
}
