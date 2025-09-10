package com.king.sys.login.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.utils.Md5Utils;
import com.king.sys.login.dto.LoginResult;
import com.king.sys.login.service.ILoginervice;
import com.king.sys.user.entity.TSysUser;
import com.king.sys.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Primary
@Service
public class LoginServiceImpl extends ServiceImpl<UserMapper, TSysUser> implements ILoginervice {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public LoginResult login(TSysUser user) {
        // 校验输入参数
        if (!StringUtils.hasText(user.getLoginName())) {
            return LoginResult.loginNameEmpty();
        }
        if (!StringUtils.hasText(user.getPassword())) {
            return LoginResult.passwordEmpty();
        }
        
        // 查询用户
        LambdaQueryWrapper<TSysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TSysUser::getLoginName, user.getLoginName());
        TSysUser loginUser = this.baseMapper.selectOne(wrapper);
        
        // 用户不存在
        if (loginUser == null) {
            return LoginResult.userNotFound();
        }
        
        // 检查用户状态
        if (loginUser.getStatus() == 0) {
            return LoginResult.userDisabled();
        }
        
        // 校验密码
        String inputHashed = Md5Utils.hash(user.getPassword());
        if (!inputHashed.equals(loginUser.getPassword())) {
            return LoginResult.passwordError();
        }
        
        // 登录成功，生成token
        String key = "user:" + UUID.randomUUID();
        loginUser.setPassword(null);
        redisTemplate.opsForValue().set(key, loginUser, 30, TimeUnit.MINUTES);
        
        // 构建用户信息
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", loginUser.getUserId());
        userInfo.put("userName", loginUser.getUserName());
        userInfo.put("loginName", loginUser.getLoginName());
        userInfo.put("email", loginUser.getEmail());
        userInfo.put("phone", loginUser.getPhone());
        
        return LoginResult.success(key, userInfo);
    }

    @Override
    public Map<String, Object> getUserInfo(String token) {
        Object obj = redisTemplate.opsForValue().get(token);
        if (obj != null) {
            TSysUser loginUser = JSON.parseObject(JSON.toJSONString(obj), TSysUser.class);

            Map<String, Object> data = new HashMap<>();
            data.put("name", loginUser.getUserName());
            data.put("phone", loginUser.getPhone());

            List<String> roleList = this.baseMapper.getRoleNameByUserId(loginUser.getUserId());
            data.put("roles", roleList);

            return data;
        }
        return null;
    }

    @Override
    public void logout(String token) {
        redisTemplate.delete(token);
    }

}