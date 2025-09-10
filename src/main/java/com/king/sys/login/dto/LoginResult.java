package com.king.sys.login.dto;

import lombok.Data;

/**
 * 登录结果封装类
 */
@Data
public class LoginResult {
    
    /**
     * 登录是否成功
     */
    private boolean success;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 错误代码
     */
    private String errorCode;
    
    /**
     * 登录成功时的token
     */
    private String token;
    
    /**
     * 登录成功时的用户信息
     */
    private Object userInfo;
    
    /**
     * 创建成功结果
     */
    public static LoginResult success(String token, Object userInfo) {
        LoginResult result = new LoginResult();
        result.setSuccess(true);
        result.setToken(token);
        result.setUserInfo(userInfo);
        return result;
    }
    
    /**
     * 创建失败结果
     */
    public static LoginResult failure(String errorCode, String errorMessage) {
        LoginResult result = new LoginResult();
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        return result;
    }
    
    /**
     * 用户不存在
     */
    public static LoginResult userNotFound() {
        return failure("USER_NOT_FOUND", "用户不存在");
    }
    
    /**
     * 用户已被禁用
     */
    public static LoginResult userDisabled() {
        return failure("USER_DISABLED", "用户已被禁用，请联系管理员");
    }
    
    /**
     * 密码错误
     */
    public static LoginResult passwordError() {
        return failure("PASSWORD_ERROR", "密码错误");
    }
    
    /**
     * 登录名不能为空
     */
    public static LoginResult loginNameEmpty() {
        return failure("LOGIN_NAME_EMPTY", "登录名不能为空");
    }
    
    /**
     * 密码不能为空
     */
    public static LoginResult passwordEmpty() {
        return failure("PASSWORD_EMPTY", "密码不能为空");
    }
}
