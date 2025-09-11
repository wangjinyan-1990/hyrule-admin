package com.king.sys.login.controller;

import com.king.common.Result;
import com.king.common.utils.JwtUtil;
import com.king.sys.login.dto.LoginResult;
import com.king.sys.login.service.ILoginervice;
import com.king.sys.menu.entity.SysMenu;
import com.king.sys.menu.service.IMenuService;
import com.king.sys.user.entity.TSysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/sys")
public class LoginController {

    @Autowired
    private ILoginervice loginService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IMenuService menuService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody TSysUser user, HttpServletResponse response) {
        LoginResult loginResult = loginService.login(user);
        if (loginResult.isSuccess()) {
            // 登录成功，构建返回数据
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("token", loginResult.getToken());
            data.put("userInfo", loginResult.getUserInfo());
            // 直接返回菜单，避免前端在未附带token时立即请求菜单导致未登录提示
            String userId = jwtUtil.getUserIdFromToken(loginResult.getToken());
            if (userId != null) {
                List<SysMenu> menus = menuService.getMenusByUserId(userId);
                data.put("menus", menus);
            }
            // 将token也写入响应头，方便前端统一从header读取
            response.setHeader("Authorization", "Bearer " + loginResult.getToken());
            response.setHeader("X-Token", loginResult.getToken());
            return Result.success(data);
        } else {
            // 登录失败，返回具体错误信息
            return Result.error(0, loginResult.getErrorMessage());
        }
    }

    @GetMapping("/getUserInfo")
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