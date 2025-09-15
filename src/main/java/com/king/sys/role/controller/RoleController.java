package com.king.sys.role.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.king.common.Result;
import com.king.sys.role.entity.TSysRole;
import com.king.sys.role.service.IRoleService;
import com.king.sys.role.service.IRoleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/role")
public class RoleController {

    @Autowired
    @Qualifier("roleServiceImpl")
    private IRoleService roleService;
    
    @Autowired
    @Qualifier("roleUserServiceImpl")
    private IRoleUserService roleUserService;

    /**
     * 获取所有角色
     */
    @GetMapping("/all")
    public Result<List<TSysRole>> getAll() {
        List<TSysRole> roles = roleService.list();
        return Result.success(roles);
    }

    /**
     * 分页查询角色列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getRoleList(@RequestParam(value = "roleName", required = false) String roleName,
                                                   @RequestParam("pageNo") Long pageNo,
                                                   @RequestParam("pageSize") Long pageSize) {
        LambdaQueryWrapper<TSysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasLength(roleName), TSysRole::getRoleName, roleName);
        wrapper.orderByAsc(TSysRole::getSortNo);

        Page<TSysRole> page = new Page<>(pageNo, pageSize);
        roleService.page(page, wrapper);

        Map<String, Object> data = new java.util.HashMap<>();
        data.put("total", page.getTotal());
        data.put("rows", page.getRecords());

        return Result.success(data);
    }

    /**
     * 创建角色
     */
    @PostMapping("/create")
    public Result<?> createRole(@RequestBody TSysRole role) {
        try {
            roleService.createRole(role);
            return Result.success("角色创建成功");
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("角色创建失败");
        }
    }

    /**
     * 编辑角色
     */
    @PutMapping("/update")
    public Result<?> updateRole(@RequestBody TSysRole role) {
        try {
            roleService.updateRole(role);
            return Result.success("角色更新成功");
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("角色更新失败");
        }
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/delete")
    public Result<?> deleteRole(@RequestParam("roleId") String roleId) {
        try {
            roleService.deleteRole(roleId);
            return Result.success("角色删除成功");
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("角色删除失败");
        }
    }

    /**
     * 根据ID获取角色详情
     */
    @GetMapping("/detail")
    public Result<TSysRole> getRoleDetail(@RequestParam("roleId") String roleId) {
        try {
            TSysRole role = roleService.getById(roleId);
            if (role != null) {
                return Result.success(role);
            } else {
                return Result.error("角色不存在");
            }
        } catch (Exception e) {
            return Result.error("获取角色详情失败");
        }
    }

    /**
     * 获取角色的用户列表
     */
    @GetMapping("/users")
    public Result<Map<String, Object>> getRoleUsers(@RequestParam("roleId") String roleId,
                                                    @RequestParam(value = "userName", required = false) String userName,
                                                    @RequestParam("pageNo") Long pageNo,
                                                    @RequestParam("pageSize") Long pageSize) {
        try {
            Map<String, Object> data = roleUserService.getRoleUsers(roleId, userName, pageNo, pageSize);
            return Result.success(data);
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("获取角色用户列表失败");
        }
    }

    /**
     * 获取可选用户列表（未拥有该角色的用户）
     */
    @GetMapping("/availableUsers")
    public Result<Map<String, Object>> getAvailableUsers(@RequestParam("roleId") String roleId,
                                                         @RequestParam(value = "userName", required = false) String userName,
                                                         @RequestParam("pageNo") Long pageNo,
                                                         @RequestParam("pageSize") Long pageSize) {
        try {
            Map<String, Object> data = roleUserService.getAvailableUsers(roleId, userName, pageNo, pageSize);
            return Result.success(data);
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("获取可选用户列表失败");
        }
    }

    /**
     * 添加用户角色
     */
    @PostMapping("/addUser")
    public Result<?> addUserRole(@RequestBody Map<String, String> request) {
        try {
            String roleId = request.get("roleId");
            String userId = request.get("userId");
            roleUserService.addUserRole(roleId, userId);
            return Result.success("用户角色添加成功");
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("用户角色添加失败");
        }
    }

    /**
     * 删除用户角色
     */
    @DeleteMapping("/removeUser")
    public Result<?> removeUserRole(@RequestParam("roleId") String roleId,
                                    @RequestParam("userId") String userId) {
        try {
            roleUserService.removeUserRole(roleId, userId);
            return Result.success("用户角色删除成功");
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("用户角色删除失败");
        }
    }
}
