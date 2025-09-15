package com.king.test.baseManage.testSystem.controller;

import com.king.common.Result;
import com.king.test.baseManage.testSystem.dto.UserSystemInfoDTO;
import com.king.test.baseManage.testSystem.service.ITestSystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试系统人员Controller
 */
@RestController
@RequestMapping("/test/systemUser")
public class TestSystemUserController {
    
    @Autowired
    @Qualifier("testSystemUserServiceImpl")
    private ITestSystemUserService testSystemUserService;
    
    /**
     * 根据角色ID获取用户列表（在职状态）
     * 每个人只出现一次，多个系统的systemId和systemName会拼接
     * @param roleId 角色ID
     * @return 用户系统信息列表
     */
    @GetMapping("/getUsersByRoleId")
    public Result<List<UserSystemInfoDTO>> getUsersByRoleId(@RequestParam("roleId") String roleId) {
        if (!StringUtils.hasText(roleId)) {
            return Result.error("角色ID不能为空");
        }
        
        try {
            List<UserSystemInfoDTO> users = testSystemUserService.getUsersByRoleId(roleId);
            return Result.success(users);
        } catch (Exception e) {
            return Result.error("查询用户列表失败：" + e.getMessage());
        }
    }
}
