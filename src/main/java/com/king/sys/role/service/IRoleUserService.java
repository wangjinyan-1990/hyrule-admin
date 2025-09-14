package com.king.sys.role.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.sys.role.entity.TSysRoleUser;

import java.util.Map;

public interface IRoleUserService extends IService<TSysRoleUser> {
    
    /**
     * 分页查询角色的用户列表
     * @param roleId 角色ID
     * @param userName 用户名（模糊查询）
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Map<String, Object> getRoleUsers(String roleId, String userName, Long pageNo, Long pageSize);
    
    /**
     * 分页查询可选用户列表（未拥有该角色的用户）
     * @param roleId 角色ID
     * @param userName 用户名（模糊查询）
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Map<String, Object> getAvailableUsers(String roleId, String userName, Long pageNo, Long pageSize);
    
    /**
     * 添加用户角色关联
     * @param roleId 角色ID
     * @param userId 用户ID
     */
    void addUserRole(String roleId, String userId);
    
    /**
     * 删除用户角色关联
     * @param roleId 角色ID
     * @param userId 用户ID
     */
    void removeUserRole(String roleId, String userId);
}
