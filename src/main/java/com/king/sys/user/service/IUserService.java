package com.king.sys.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.sys.user.entity.TSysUser;

import java.util.Map;

public interface IUserService extends IService<TSysUser> {
    public Map<String, Object> getUserInfo(String token);
    public void createUser(TSysUser user);
    public void updateUser(TSysUser user);
}