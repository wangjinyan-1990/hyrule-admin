package com.king.test.baseManage.testSystem.dto;

import lombok.Data;

/**
 * 用户缺陷角色权限DTO
 */
@Data
public class UserBugRolePermissionDTO {
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 是否为测试组长:0为否、1为是（可选）
     */
    private String testLeader;
    
    /**
     * 是否为开发组长:0为否、1为是（可选）
     */
    private String devLeader;
}
