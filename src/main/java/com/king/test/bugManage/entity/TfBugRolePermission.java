package com.king.test.bugManage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 缺陷角色权限实体类
 * 对应表: tf_bug_role_permission
 */
@Data
@TableName("tf_bug_role_permission")
public class TfBugRolePermission {

    /**
     * 缺陷角色权限Id（自增主键）
     */
    @TableId("ROLE_PERMISSION_ID")
    private Integer rolePermissionId;

    /**
     * 用户ID
     */
    @TableField("USER_ID")
    private String userId;

    /**
     * 是否为测试组长:0为否、1为是
     */
    @TableField("TEST_LEADER")
    private String testLeader;

    /**
     * 是否为开发组长:0为否、1为是
     */
    @TableField("DEV_LEADER")
    private String devLeader;

    // 构造函数
    public TfBugRolePermission() {
    }

    public TfBugRolePermission(Integer rolePermissionId, String userId, String testLeader, String devLeader) {
        this.rolePermissionId = rolePermissionId;
        this.userId = userId;
        this.testLeader = testLeader;
        this.devLeader = devLeader;
    }

    public TfBugRolePermission(String userId, String testLeader, String devLeader) {
        this.userId = userId;
        this.testLeader = testLeader;
        this.devLeader = devLeader;
    }

    // Getter和Setter方法（Lombok的@Data注解会自动生成，这里提供手动版本作为备选）
    public Integer getRolePermissionId() {
        return rolePermissionId;
    }

    public void setRolePermissionId(Integer rolePermissionId) {
        this.rolePermissionId = rolePermissionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTestLeader() {
        return testLeader;
    }

    public void setTestLeader(String testLeader) {
        this.testLeader = testLeader;
    }

    public String getDevLeader() {
        return devLeader;
    }

    public void setDevLeader(String devLeader) {
        this.devLeader = devLeader;
    }
}
