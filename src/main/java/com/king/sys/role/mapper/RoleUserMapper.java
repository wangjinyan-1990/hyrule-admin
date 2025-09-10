package com.king.sys.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.sys.role.entity.SysRoleUser;
import com.king.sys.user.entity.TSysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleUserMapper extends BaseMapper<SysRoleUser> {
    
    /**
     * 根据角色ID查询用户列表
     * @param roleId 角色ID
     * @param userName 用户名（模糊查询）
     * @return 用户列表
     */
    List<TSysUser> selectUsersByRoleId(@Param("roleId") String roleId, @Param("userName") String userName);
    
    /**
     * 查询未拥有指定角色的用户列表
     * @param roleId 角色ID
     * @param userName 用户名（模糊查询）
     * @return 用户列表
     */
    List<TSysUser> selectAvailableUsersByRoleId(@Param("roleId") String roleId, @Param("userName") String userName);
}
