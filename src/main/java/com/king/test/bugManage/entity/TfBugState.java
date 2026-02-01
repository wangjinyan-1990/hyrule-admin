package com.king.test.bugManage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 缺陷状态实体类
 * 对应表: tf_bug_state
 */
@Data
@TableName("tf_bug_state")
public class TfBugState {

    /**
     * 缺陷状态Id（自增主键）
     */
    @TableId("BUG_STATE_ID")
    private Integer bugStateId;

    /**
     * 缺陷流程编号
     */
    @TableField("BUG_FLOWLET_NUMBER")
    private Integer bugFlowletNumber;

    /**
     * 缺陷状态码
     */
    @TableField("BUG_STATE_CODE")
    private String bugStateCode;

    /**
     * 缺陷状态名
     */
    @TableField("BUG_STATE_NAME")
    private String bugStateName;

    /**
     * 排序号
     */
    @TableField("SORT_NO")
    private Integer sortNo;

    // 构造函数
    public TfBugState() {
    }

    public TfBugState(Integer bugStateId, Integer bugFlowletNumber, String bugStateCode, 
                      String bugStateName, Integer sortNo) {
        this.bugStateId = bugStateId;
        this.bugFlowletNumber = bugFlowletNumber;
        this.bugStateCode = bugStateCode;
        this.bugStateName = bugStateName;
        this.sortNo = sortNo;
    }

    public TfBugState(Integer bugFlowletNumber, String bugStateCode, String bugStateName, Integer sortNo) {
        this.bugFlowletNumber = bugFlowletNumber;
        this.bugStateCode = bugStateCode;
        this.bugStateName = bugStateName;
        this.sortNo = sortNo;
    }

    // Getter和Setter方法（Lombok的@Data注解会自动生成，这里提供手动版本作为备选）
    public Integer getBugStateId() {
        return bugStateId;
    }

    public void setBugStateId(Integer bugStateId) {
        this.bugStateId = bugStateId;
    }

    public Integer getBugFlowletNumber() {
        return bugFlowletNumber;
    }

    public void setBugFlowletNumber(Integer bugFlowletNumber) {
        this.bugFlowletNumber = bugFlowletNumber;
    }

    public String getBugStateCode() {
        return bugStateCode;
    }

    public void setBugStateCode(String bugStateCode) {
        this.bugStateCode = bugStateCode;
    }

    public String getBugStateName() {
        return bugStateName;
    }

    public void setBugStateName(String bugStateName) {
        this.bugStateName = bugStateName;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }
}
