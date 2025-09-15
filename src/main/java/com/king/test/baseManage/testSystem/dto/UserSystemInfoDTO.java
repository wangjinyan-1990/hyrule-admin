package com.king.test.baseManage.testSystem.dto;

import lombok.Data;

/**
 * 用户系统信息DTO
 * 用于返回用户基本信息和关联的系统信息
 */
@Data
public class UserSystemInfoDTO {
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 登录名
     */
    private String loginName;
    
    /**
     * 用户姓名
     */
    private String userName;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 电话
     */
    private String phone;
    
    /**
     * 所属机构ID
     */
    private String orgId;
    
    /**
     * 所属机构名称
     */
    private String orgName;
    
    /**
     * 用户状态
     */
    private Integer status;
    
    /**
     * 排序号
     */
    private Integer sortNo;
    
    /**
     * 系统ID列表（逗号分隔）
     */
    private String systemIds;
    
    /**
     * 系统名称列表（逗号分隔）
     */
    private String systemNames;
    
    public UserSystemInfoDTO() {
    }
    
    public UserSystemInfoDTO(String userId, String loginName, String userName, 
                           String email, String phone, String orgId, String orgName,
                           Integer status, Integer sortNo, String systemIds, String systemNames) {
        this.userId = userId;
        this.loginName = loginName;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.orgId = orgId;
        this.orgName = orgName;
        this.status = status;
        this.sortNo = sortNo;
        this.systemIds = systemIds;
        this.systemNames = systemNames;
    }
}
