package com.king.sys.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName("t_sys_user")
public class TSysUser {

    @TableId(value = "USER_ID")
    private String userId;

    @TableField("LOGIN_NAME")
    private String loginName;

    @TableField("USER_NAME")
    private String userName;

    @TableField("PASSWORD")
    private String password;

    @TableField("EMAIL")
    private String email;

    @TableField("PHONE")
    private String phone;

    @TableField("ORG_ID")
    private String orgId; //所属机构id

    @TableField("STATUS")
    private Integer status;

    @TableField("SORT_NO")
    private int sortNo;

    @TableField(exist = false)
    private Integer deleted;

    // 用户角色名称（逗号分隔的字符串，用于前端显示）
    @TableField(exist = false)
    private String roleNames;
    
    // 用户角色ID（逗号分隔的字符串，用于前端显示）
    @TableField(exist = false)
    private String roleIds;
    
    // 用户所属机构名称（用于前端显示）
    @TableField(exist = false)
    private String orgName;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public int getSortNo() {
        return sortNo;
    }

    public void setSortNo(int sortNo) {
        this.sortNo = sortNo;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}