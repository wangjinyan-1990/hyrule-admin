package com.king.test.bugManage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 缺陷流程实体类
 * 对应表: tf_bug_flowlet
 */
@Data
@TableName("tf_bug_flowlet")
public class TfBugFlowlet {

    /**
     * 缺陷流程ID（自增主键）
     */
    @TableId("BUG_FLOWLET_ID")
    private Integer bugFlowletId;

    /**
     * 当前状态码
     */
    @TableField("CURRENT_STATE_CODE")
    private String currentStateCode;

    /**
     * 下一状态码
     */
    @TableField("NEXT_STATE_CODE")
    private String nextStateCode;

    /**
     * 缺陷流程编号
     */
    @TableField("BUG_FLOWLET_NUMBER")
    private Integer bugFlowletNumber;

    // 构造函数
    public TfBugFlowlet() {
    }

    public TfBugFlowlet(Integer bugFlowletId, String currentStateCode, String nextStateCode, Integer bugFlowletNumber) {
        this.bugFlowletId = bugFlowletId;
        this.currentStateCode = currentStateCode;
        this.nextStateCode = nextStateCode;
        this.bugFlowletNumber = bugFlowletNumber;
    }
}

