package com.king.sys.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.utils.DateUtil;
import com.king.sys.user.entity.TSysUser;
import com.king.sys.user.mapper.UserMapper;
import com.king.sys.user.service.IUserService;
import com.king.common.constant.Constants;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.king.common.utils.Md5Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, TSysUser> implements IUserService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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
    public void createUser(TSysUser user) {
        Assert.notNull(user, "用户不能为空");
        
        // 校验必填字段
        Assert.isTrue(StringUtils.hasText(user.getLoginName()), "loginName不能为空");
        Assert.isTrue(StringUtils.hasText(user.getUserName()), "userName不能为空");
        Assert.isTrue(StringUtils.hasText(user.getPhone()), "phone不能为空");
        Assert.isTrue(user.getPhone().matches("^1[3-9]\\d{9}$"), "手机号格式不正确");

        // 校验格式
        validateLoginNameFormat(user.getLoginName());
        
        // 校验唯一性
        validateLoginNameUniqueForCreate(user.getLoginName());
        validateUserNameUniqueForCreate(user.getUserName());
        validatePhoneUniqueForCreate(user.getPhone());

        // userId = loginName
        user.setUserId(user.getLoginName());
        // 默认密码 123456（明文按题意设置；若需要加密，可在此加密）
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword(Md5Utils.hash(Constants.PASSWORD_INIT));
        }
        String currentDateStr = DateUtil.getDateFormatYMD();
        user.setSortNo(Integer.parseInt(currentDateStr));
        // 仅允许字段：userId、userName、loginName、email、status、password、phone、sortNo
        TSysUser toSave = new TSysUser();
        toSave.setUserId(user.getUserId());
        toSave.setLoginName(user.getLoginName());
        toSave.setUserName(user.getUserName());
        toSave.setEmail(user.getEmail());
        toSave.setStatus(user.getStatus());
        toSave.setPassword(user.getPassword());
        toSave.setPhone(user.getPhone());
        toSave.setSortNo(user.getSortNo());

        this.baseMapper.insert(toSave);
    }

    @Override
    public void updateUser(TSysUser user) {
        Assert.notNull(user, "用户不能为空");
        Assert.isTrue(StringUtils.hasText(user.getUserId()), "用户ID不能为空");
        
        // 检查用户是否存在
        TSysUser existingUser = this.baseMapper.selectById(user.getUserId());
        Assert.notNull(existingUser, "用户不存在");
        
        // 校验必填字段
        Assert.isTrue(StringUtils.hasText(user.getUserName()), "用户名不能为空");
        Assert.isTrue(StringUtils.hasText(user.getPhone()), "手机号不能为空");
        Assert.isTrue(user.getPhone().matches("^1[3-9]\\d{9}$"), "手机号格式不正确");
        
        // 校验格式
        validateLoginNameFormat(user.getLoginName());
        
        // 校验唯一性（只有字段值发生变化时才校验）
        if (StringUtils.hasText(user.getLoginName()) && !existingUser.getLoginName().equals(user.getLoginName())) {
            validateLoginNameUniqueForUpdate(user.getLoginName(), user.getUserId());
        }
        
        if (!existingUser.getUserName().equals(user.getUserName())) {
            validateUserNameUniqueForUpdate(user.getUserName(), user.getUserId());
        }
        
        if (!existingUser.getPhone().equals(user.getPhone())) {
            validatePhoneUniqueForUpdate(user.getPhone(), user.getUserId());
        }
        
        // 构建更新对象，只更新允许的字段
        TSysUser toUpdate = new TSysUser();
        toUpdate.setUserName(user.getUserName());
        toUpdate.setEmail(user.getEmail());
        toUpdate.setStatus(user.getStatus());
        toUpdate.setPhone(user.getPhone());
        
        // 如果提供了loginName，则更新loginName
        if (StringUtils.hasText(user.getLoginName())) {
            toUpdate.setLoginName(user.getLoginName());
        }
        
        // 如果提供了新密码，则更新密码
        if (StringUtils.hasText(user.getPassword())) {
            toUpdate.setPassword(Md5Utils.hash(user.getPassword()));
        }
        
        this.baseMapper.updateById(toUpdate);
    }

    /**
     * 校验登录名格式
     * @param loginName 登录名
     */
    private void validateLoginNameFormat(String loginName) {
        if (StringUtils.hasText(loginName)) {
            Assert.isTrue(loginName.matches("^[A-Za-z0-9]+$"), "loginName只能包含字母和数字");
        }
    }

    /**
     * 校验登录名唯一性（创建用户时使用）
     * @param loginName 登录名
     */
    private void validateLoginNameUniqueForCreate(String loginName) {
        if (StringUtils.hasText(loginName)) {
            LambdaQueryWrapper<TSysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TSysUser::getLoginName, loginName);
            TSysUser exist = this.baseMapper.selectOne(wrapper);
            Assert.isTrue(exist == null, "登录名已存在");
        }
    }

    /**
     * 校验登录名唯一性（更新用户时使用）
     * @param loginName 登录名
     * @param currentUserId 当前用户ID
     */
    private void validateLoginNameUniqueForUpdate(String loginName, String currentUserId) {
        if (StringUtils.hasText(loginName)) {
            LambdaQueryWrapper<TSysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TSysUser::getLoginName, loginName)
                   .ne(TSysUser::getUserId, currentUserId);
            TSysUser exist = this.baseMapper.selectOne(wrapper);
            Assert.isTrue(exist == null, "登录名已被其他用户使用");
        }
    }

    /**
     * 校验用户名唯一性（创建用户时使用）
     * @param userName 用户名
     */
    private void validateUserNameUniqueForCreate(String userName) {
        if (StringUtils.hasText(userName)) {
            LambdaQueryWrapper<TSysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TSysUser::getUserName, userName);
            TSysUser exist = this.baseMapper.selectOne(wrapper);
            Assert.isTrue(exist == null, "用户名已存在");
        }
    }

    /**
     * 校验用户名唯一性（更新用户时使用）
     * @param userName 用户名
     * @param currentUserId 当前用户ID
     */
    private void validateUserNameUniqueForUpdate(String userName, String currentUserId) {
        if (StringUtils.hasText(userName)) {
            LambdaQueryWrapper<TSysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TSysUser::getUserName, userName)
                   .ne(TSysUser::getUserId, currentUserId);
            TSysUser exist = this.baseMapper.selectOne(wrapper);
            Assert.isTrue(exist == null, "用户名已被其他用户使用");
        }
    }

    /**
     * 校验手机号唯一性（创建用户时使用）
     * @param phone 手机号
     */
    private void validatePhoneUniqueForCreate(String phone) {
        if (StringUtils.hasText(phone)) {
            LambdaQueryWrapper<TSysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TSysUser::getPhone, phone);
            TSysUser exist = this.baseMapper.selectOne(wrapper);
            Assert.isTrue(exist == null, "手机号已存在");
        }
    }

    /**
     * 校验手机号唯一性（更新用户时使用）
     * @param phone 手机号
     * @param currentUserId 当前用户ID
     */
    private void validatePhoneUniqueForUpdate(String phone, String currentUserId) {
        if (StringUtils.hasText(phone)) {
            LambdaQueryWrapper<TSysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TSysUser::getPhone, phone)
                   .ne(TSysUser::getUserId, currentUserId);
            TSysUser exist = this.baseMapper.selectOne(wrapper);
            Assert.isTrue(exist == null, "手机号已被其他用户使用");
        }
    }
}