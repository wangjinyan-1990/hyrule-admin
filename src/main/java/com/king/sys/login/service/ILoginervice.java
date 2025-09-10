package com.king.sys.login.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.sys.login.dto.LoginResult;
import com.king.sys.user.entity.TSysUser;

import java.util.Map;

public interface ILoginervice extends IService<TSysUser> {
    public LoginResult login(TSysUser user);
    public Map<String, Object> getUserInfo(String token);
    public void logout(String token);
}