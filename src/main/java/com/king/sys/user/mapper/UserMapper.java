package com.king.sys.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.sys.user.entity.TSysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<TSysUser> {
    List<String> getRoleNameByUserId(String userId);
    List<TSysUser> selectUserList();
}