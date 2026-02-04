package com.king.test.bugManage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 缺陷流程权限实体类
 * 对应表: tf_bug_flowlet_permission
 */
@Data
@TableName("tf_bug_flowlet_permission")
public class TfBugFlowletPermission {

    /**
     * 缺陷流程权限ID（自增主键）
     */
    @TableId("FLOWLET_PERMISSION_ID")
    private Integer flowletPermissionId;

    /**
     * 缺陷流程ID
     */
    @TableField("BUG_FLOWLET_ID")
    private Integer bugFlowletId;

    /**
     * 缺陷角色代码
     */
    @TableField("BUG_ROLE_CODE")
    private String bugRoleCode;

    /**
     * 下一状态码
     */
    @TableField("NEXT_STATE_CODE")
    private String nextStateCode;

    // 构造函数
    public TfBugFlowletPermission() {
    }

    public TfBugFlowletPermission(Integer flowletPermissionId, Integer bugFlowletId, String bugRoleCode, String nextStateCode) {
        this.flowletPermissionId = flowletPermissionId;
        this.bugFlowletId = bugFlowletId;
        this.bugRoleCode = bugRoleCode;
        this.nextStateCode = nextStateCode;
    }
}

