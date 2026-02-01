package com.king.test.baseManage.testSystem.controller;

import com.king.common.Result;
import com.king.test.baseManage.testSystem.dto.UserSystemUpdateDTO;
import com.king.test.baseManage.testSystem.dto.UserBugRolePermissionDTO;
import com.king.test.baseManage.testSystem.service.ITestSystemUserService;
import com.king.test.bugManage.service.ITfBugRolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 测试系统人员Controller
 */
@RestController
@RequestMapping("/test/systemUser")
public class TestSystemUserController {
    
    @Autowired
    @Qualifier("testSystemUserServiceImpl")
    private ITestSystemUserService testSystemUserService;
    
    @Autowired
    @Qualifier("tfBugRolePermissionServiceImpl")
    private ITfBugRolePermissionService bugRolePermissionService;
    
    /**
     * 开发人员页签、测试人员页签，根据角色ID获取用户列表，用于系统成员维护（在职状态）
     * 每个人只出现一次，多个系统的systemId和systemName会拼接
     * @param roleId 角色ID
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param userName 用户姓名（可选）
     * @param loginName 登录名（可选）
     * @param phone 电话（可选）
     * @return 用户系统信息列表
     */
    @GetMapping("/getUsersByRoleId")
    public Result<Map<String, Object>> getUsersByRoleId(@RequestParam("roleId") String roleId,
                                                        @RequestParam("pageNo") Long pageNo,
                                                        @RequestParam("pageSize") Long pageSize,
                                                        @RequestParam(value = "userName", required = false) String userName,
                                                        @RequestParam(value = "loginName", required = false) String loginName,
                                                        @RequestParam(value = "phone", required = false) String phone) {
        if (!StringUtils.hasText(roleId)) {
            return Result.error("角色ID不能为空");
        }
        
        try {
            Map<String, Object> data = testSystemUserService.getUsersByRoleId(roleId, pageNo, pageSize, userName, loginName, phone);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("查询用户列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新用户所属系统
     * @param userData 用户系统更新数据
     * @return 更新结果
     */
    @PutMapping("/update")
    public Result<?> updateUser(@RequestBody UserSystemUpdateDTO userData) {
        if (userData == null || !StringUtils.hasText(userData.getUserId())) {
            return Result.error("用户ID不能为空");
        }
        
        try {
            boolean success = testSystemUserService.updateUserSystems(userData.getUserId(), userData.getSystemIds());
            if (success) {
                return Result.success("用户系统关系更新成功");
            } else {
                return Result.error("用户系统关系更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新用户系统关系失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新用户缺陷角色权限（测试组长/开发组长）
     * @param userData 用户缺陷角色权限数据（包含userId和testLeader或devLeader）
     * @return 更新结果
     */
    @PutMapping("/updateUserBugRolePermission")
    public Result<?> updateUserBugRolePermission(@RequestBody UserBugRolePermissionDTO userData) {
        if (userData == null || !StringUtils.hasText(userData.getUserId())) {
            return Result.error("用户ID不能为空");
        }
        
        // 至少需要提供一个角色权限参数
        if (!StringUtils.hasText(userData.getTestLeader()) && !StringUtils.hasText(userData.getDevLeader())) {
            return Result.error("至少需要提供testLeader或devLeader参数");
        }
        
        try {
            boolean success = bugRolePermissionService.updateUserBugRolePermission(
                    userData.getUserId(), 
                    userData.getTestLeader(), 
                    userData.getDevLeader()
            );
            if (success) {
                return Result.success("用户缺陷角色权限更新成功");
            } else {
                return Result.error("用户缺陷角色权限更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新用户缺陷角色权限失败：" + e.getMessage());
        }
    }
}
