package com.king.sys.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.king.sys.user.entity.TSysUser;
import org.springframework.util.StringUtils;
import com.king.common.Result;
import com.king.sys.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/sys/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/all")
    public Result<List<TSysUser>> getAll(){
        List<TSysUser> users = userService.list();
        return Result.success(users);
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> getUserList(@RequestParam(value = "userName", required = false) String userName,
                                                   @RequestParam(value = "phone", required = false) String phone,
                                                   @RequestParam("pageNo") Long pageNo,
                                                   @RequestParam("pageSize") Long pageSize) {
        LambdaQueryWrapper<TSysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasLength(userName), TSysUser::getUserName, userName);
        wrapper.eq(StringUtils.hasLength(phone), TSysUser::getPhone, phone);

        Page<TSysUser> page = new Page<>(pageNo, pageSize);
        userService.page(page, wrapper);

        Map<String, Object> data = new java.util.HashMap<>();
        data.put("total", page.getTotal());
        data.put("rows", page.getRecords());

        return Result.success(data);
    }


    @PostMapping("/create")
    public Result<?> createUser(@RequestBody TSysUser user) {
        try {
            userService.createUser(user);
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<?> updateUser(@RequestBody TSysUser user) {
        try {
            userService.updateUser(user);
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        }
    }

}