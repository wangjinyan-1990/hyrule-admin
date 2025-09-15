package com.king.test.baseManage.testSystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 测试系统用户实体类
 */
@Data
@TableName("t_test_system_user")
public class TTestSystemUser {

    /**
     * 系统用户ID
     */
    @TableId(value = "SYSTEM_USER_ID", type = IdType.AUTO)
    private Integer systemUserId;

    /**
     * 用户ID
     */
    @TableField("USER_ID")
    private String userId;

    /**
     * 系统ID
     */
    @TableField("SYSTEM_ID")
    private String systemId;

    // Getter和Setter方法
    public Integer getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Integer systemUserId) {
        this.systemUserId = systemUserId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
}