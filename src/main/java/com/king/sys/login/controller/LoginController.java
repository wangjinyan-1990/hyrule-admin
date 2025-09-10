package com.king.sys.login.controller;

import com.king.common.Result;
import com.king.sys.login.dto.LoginResult;
import com.king.sys.login.service.ILoginervice;
import com.king.sys.user.entity.TSysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/sys")
public class LoginController {

    @Autowired
    private ILoginervice loginService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody TSysUser user) {
        LoginResult loginResult = loginService.login(user);
        if (loginResult.isSuccess()) {
            // 登录成功，构建返回数据
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("token", loginResult.getToken());
            data.put("userInfo", loginResult.getUserInfo());
            return Result.success(data);
        } else {
            // 登录失败，返回具体错误信息
            return Result.error(0, loginResult.getErrorMessage());
        }
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(@RequestParam("token") String token) {
        Map<String, Object> data = loginService.getUserInfo(token);
        if (data != null) {
            return Result.success(data);
        }
        return Result.error(0, "Token已过期或无效，请重新登录");
    }

    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader("X-Token") String token) {
        loginService.logout(token);
        return Result.success();
    }

}