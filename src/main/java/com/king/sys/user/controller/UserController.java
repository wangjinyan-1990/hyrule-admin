package com.king.sys.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.king.sys.user.entity.TSysUser;
import com.king.sys.user.mapper.UserMapper;
import com.king.common.Result;
import com.king.sys.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/user")
public class UserController {

    @Autowired
    @Qualifier("userServiceImpl")
    private IUserService userService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/all")
    public Result<List<TSysUser>> getAll(){
        List<TSysUser> users = userService.list();
        return Result.success(users);
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> getUserList(@RequestParam(value = "userName", required = false) String userName,
                                                   @RequestParam(value = "loginName", required = false) String loginName,
                                                   @RequestParam(value = "phone", required = false) String phone,
                                                   @RequestParam("pageNo") Long pageNo,
                                                   @RequestParam("pageSize") Long pageSize) {
        Page<TSysUser> page = new Page<>(pageNo, pageSize);
        
        // 使用新的查询方法，包含角色信息（SQL中已拼接好）
        IPage<TSysUser> userList = userMapper.selectUserPageWithRoles(page, userName, loginName, phone);

        Map<String, Object> data = new java.util.HashMap<>();
        data.put("total", userList.getTotal());
        data.put("rows", userList.getRecords());

        return Result.success(data);
    }


    @PostMapping("/create")
    public Result<?> createUser(@RequestBody Map<String, Object> requestData) {
        try {
            // 提取用户信息
            TSysUser user = new TSysUser();
            user.setLoginName((String) requestData.get("loginName"));
            user.setUserName((String) requestData.get("userName"));
            user.setEmail((String) requestData.get("email"));
            user.setPhone((String) requestData.get("phone"));
            user.setOrgId((String) requestData.get("orgId"));
            user.setStatus((Integer) requestData.get("status"));
            user.setPassword((String) requestData.get("password"));
            
            // 提取角色ID列表
            String roleIds = (String) requestData.get("roleIds");
            
            // 创建用户
            userService.createUser(user);
            
            // 分配用户角色
            if (roleIds != null) {
                userService.updateUserRoles(user.getUserId(), roleIds);
            }
            
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<?> updateUser(@RequestBody Map<String, Object> requestData) {
        try {
            // 提取用户信息
            TSysUser user = new TSysUser();
            user.setUserId((String) requestData.get("userId"));
            user.setLoginName((String) requestData.get("loginName"));
            user.setUserName((String) requestData.get("userName"));
            user.setEmail((String) requestData.get("email"));
            user.setPhone((String) requestData.get("phone"));
            user.setOrgId((String) requestData.get("orgId"));
            user.setStatus((Integer) requestData.get("status"));
            user.setPassword((String) requestData.get("password"));
            
            // 提取角色ID列表
            String roleIds = (String) requestData.get("roleIds");
            
            // 更新用户基本信息
            userService.updateUser(user);
            
            // 更新用户角色
            if (roleIds != null) {
                userService.updateUserRoles(user.getUserId(), roleIds);
            }
            
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        }
    }

}