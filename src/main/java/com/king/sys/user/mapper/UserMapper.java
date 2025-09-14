package com.king.sys.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.king.sys.user.entity.TSysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<TSysUser> {
    List<String> getRoleNameByUserId(String userId);
    List<String> getRoleIdByUserId(String userId);
    List<TSysUser> selectUserList();
    
    /**
     * 分页查询用户列表（包含角色信息）
     */
    IPage<TSysUser> selectUserPageWithRoles(Page<TSysUser> page, 
                                           @Param("userName") String userName,
                                           @Param("loginName") String loginName,
                                           @Param("phone") String phone);
}