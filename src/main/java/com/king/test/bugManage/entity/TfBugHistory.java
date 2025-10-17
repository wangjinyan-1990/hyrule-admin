package com.king.test.bugManage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 缺陷历史记录实体类
 * 对应表: tf_bug_history
 */
@Data
@TableName("tf_bug_history")
public class TfBugHistory {

    /**
     * 缺陷历史记录Id（自增主键）
     */
    @TableId("BUG_HISHORY_ID")
    private Integer bugHistoryId;

    /**
     * 缺陷Id
     */
    @TableField("BUG_ID")
    private String bugId;

    /**
     * 新状态
     */
    @TableField("NEW_STATE")
    private String newState;

    /**
     * 新状态名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String newStateName;

    /**
     * 旧状态
     */
    @TableField("OLD_STATE")
    private String oldState;

    /**
     * 旧状态名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String oldStateName;

    /**
     * 操作时间
     */
    @TableField("OPERATING_TIME")
    private LocalDateTime operatingTime;

    /**
     * 操作人ID
     */
    @TableField("OPERATOR_ID")
    private String operatorId;

    /**
     * 操作人姓名（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String operatorName;

    /**
     * 测试系统ID
     */
    @TableField("SYSTEM_ID")
    private String systemId;

    /**
     * 注释
     */
    @TableField("COMMENT")
    private String comment;

    /**
     * 上次操作时间
     */
    @TableField("LAST_OPERATION_TIME")
    private LocalDateTime lastOperationTime;

    // 构造函数
    public TfBugHistory() {
    }

    public TfBugHistory(Integer bugHistoryId, String bugId, String newState, String oldState, 
                       LocalDateTime operatingTime, String operatorId, String systemId, 
                       String comment, LocalDateTime lastOperationTime) {
        this.bugHistoryId = bugHistoryId;
        this.bugId = bugId;
        this.newState = newState;
        this.oldState = oldState;
        this.operatingTime = operatingTime;
        this.operatorId = operatorId;
        this.systemId = systemId;
        this.comment = comment;
        this.lastOperationTime = lastOperationTime;
    }

    public TfBugHistory(String bugId, String newState, String oldState, LocalDateTime operatingTime, 
                       String operatorId, String systemId, String comment, LocalDateTime lastOperationTime) {
        this.bugId = bugId;
        this.newState = newState;
        this.oldState = oldState;
        this.operatingTime = operatingTime;
        this.operatorId = operatorId;
        this.systemId = systemId;
        this.comment = comment;
        this.lastOperationTime = lastOperationTime;
    }

    // Getter和Setter方法（Lombok的@Data注解会自动生成，这里提供手动版本作为备选）
    public Integer getBugHistoryId() {
        return bugHistoryId;
    }

    public void setBugHistoryId(Integer bugHistoryId) {
        this.bugHistoryId = bugHistoryId;
    }

    public String getBugId() {
        return bugId;
    }

    public void setBugId(String bugId) {
        this.bugId = bugId;
    }

    public String getNewState() {
        return newState;
    }

    public void setNewState(String newState) {
        this.newState = newState;
    }

    public String getNewStateName() {
        return newStateName;
    }

    public void setNewStateName(String newStateName) {
        this.newStateName = newStateName;
    }

    public String getOldState() {
        return oldState;
    }

    public void setOldState(String oldState) {
        this.oldState = oldState;
    }

    public String getOldStateName() {
        return oldStateName;
    }

    public void setOldStateName(String oldStateName) {
        this.oldStateName = oldStateName;
    }

    public LocalDateTime getOperatingTime() {
        return operatingTime;
    }

    public void setOperatingTime(LocalDateTime operatingTime) {
        this.operatingTime = operatingTime;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getLastOperationTime() {
        return lastOperationTime;
    }

    public void setLastOperationTime(LocalDateTime lastOperationTime) {
        this.lastOperationTime = lastOperationTime;
    }
}
