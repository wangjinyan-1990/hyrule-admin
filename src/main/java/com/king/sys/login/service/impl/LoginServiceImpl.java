package com.king.sys.login.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.utils.JwtUtil;
import com.king.common.utils.Md5Utils;
import com.king.sys.login.dto.LoginResult;
import com.king.sys.login.service.ILoginervice;
import com.king.sys.user.entity.TSysUser;
import com.king.sys.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service("loginServiceImpl")
public class LoginServiceImpl extends ServiceImpl<UserMapper, TSysUser> implements ILoginervice {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginResult login(TSysUser user) {
        System.out.println("=== 登录开始 ===");
        System.out.println("输入用户: " + user.getLoginName());
        
        // 校验输入参数
        if (!StringUtils.hasText(user.getLoginName())) {
            System.out.println("登录名为空");
            return LoginResult.loginNameEmpty();
        }
        if (!StringUtils.hasText(user.getPassword())) {
            System.out.println("密码为空");
            return LoginResult.passwordEmpty();
        }
        
        // 查询用户
        LambdaQueryWrapper<TSysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TSysUser::getLoginName, user.getLoginName());
        TSysUser loginUser = this.baseMapper.selectOne(wrapper);
        
        System.out.println("查询到的用户: " + (loginUser != null ? loginUser.getUserId() : "null"));
        
        // 用户不存在
        if (loginUser == null) {
            System.out.println("用户不存在");
            return LoginResult.userNotFound();
        }
        
        // 检查用户状态
        System.out.println("用户状态: " + loginUser.getStatus());
        if (loginUser.getStatus() == 0) {
            System.out.println("用户被禁用");
            return LoginResult.userDisabled();
        }
        
        // 校验密码
        String inputHashed = Md5Utils.hash(user.getPassword());
        System.out.println("输入密码MD5: " + inputHashed);
        System.out.println("数据库密码: " + loginUser.getPassword());
        
        if (!inputHashed.equals(loginUser.getPassword())) {
            System.out.println("密码错误");
            return LoginResult.passwordError();
        }
        
        // 登录成功，生成JWT，并可选存入Redis（会话缓存）
        String jwtToken = jwtUtil.generateToken(loginUser.getUserId());
        System.out.println("生成的JWT: " + jwtToken);
        
        String sessionKey = "user:" + UUID.randomUUID();
        loginUser.setPassword(null);
        // Redis会话过期时间与JWT一致（毫秒 -> 分钟/秒采用 TimeUnit.MILLISECONDS）
        Long ttlMillis = jwtUtil.getExpirationTime();
        if (ttlMillis != null && ttlMillis > 0) {
            redisTemplate.opsForValue().set(sessionKey, loginUser, ttlMillis, TimeUnit.MILLISECONDS);
        } else {
            // 兜底：默认30分钟
            redisTemplate.opsForValue().set(sessionKey, loginUser, 30, TimeUnit.MINUTES);
        }
        
        // 构建用户信息
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", loginUser.getUserId());
        userInfo.put("userName", loginUser.getUserName());
        userInfo.put("loginName", loginUser.getLoginName());
        userInfo.put("email", loginUser.getEmail());
        userInfo.put("phone", loginUser.getPhone());
        
        System.out.println("用户信息: " + userInfo);
        System.out.println("=== 登录成功 ===");
        
        // 将JWT作为对外token返回（与前端/控制器的Bearer解析兼容）
        return LoginResult.success(jwtToken, userInfo);
    }

    @Override
    public Map<String, Object> getUserInfo(String token) {
        // 先尝试Redis会话token
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

        // 再尝试JWT
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId != null) {
            TSysUser user = this.baseMapper.selectById(userId);
            if (user == null) {
                return null;
            }
            Map<String, Object> data = new HashMap<>();
            data.put("name", user.getUserName());
            data.put("phone", user.getPhone());
            List<String> roleList = this.baseMapper.getRoleNameByUserId(userId);
            data.put("roles", roleList);
            return data;
        }
        return null;
    }

    @Override
    public void logout(String token) {
        // 若是Redis会话token，尝试删除
        redisTemplate.delete(token);
        // 若是JWT，无状态，无需显式删除
    }

}