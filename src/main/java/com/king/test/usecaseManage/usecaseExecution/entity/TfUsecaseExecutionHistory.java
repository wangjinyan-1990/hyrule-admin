package com.king.test.usecaseManage.usecaseExecution.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用例执行历史记录实体类
 * 对应表: tf_usecase_execution_history
 */
@Data
@TableName("tf_usecase_execution_history")
public class TfUsecaseExecutionHistory {

    /**
     * 用例执行历史记录Id（自增主键）
     */
    @TableId("USECASE_EXECUTION_HISTORY_ID")
    private Integer usecaseExecutionHistoryId;

    /**
     * 用例所属目录Id
     */
    @TableField("DIRECTORY_ID")
    private String directoryId;

    /**
     * 用例Id
     */
    @TableField("USECASE_ID")
    private String usecaseId;

    /**
     * 实际执行时间
     */
    @TableField("EXECUTION_TIME")
    private LocalDateTime executionTime;

    /**
     * 执行状态
     */
    @TableField("RUN_STATUS")
    private String runStatus;

    /**
     * 执行状态名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String runStatusName;

    /**
     * 执行人Id
     */
    @TableField("EXECUTOR_ID")
    private String executorId;

    /**
     * 执行人姓名（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String executorName;

    /**
     * 执行备注
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 执行附件Id
     */
    @TableField("RUN_ATTACHMENT_ID")
    private String runAttachmentId;

    /**
     * 执行分类：功能、非功能、自动化
     */
    @TableField("RUN_TYPE")
    private String runType;

    /**
     * 执行分类名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String runTypeName;

    // 构造函数
    public TfUsecaseExecutionHistory() {
    }

    public TfUsecaseExecutionHistory(Integer usecaseExecutionHistoryId, String directoryId, String usecaseId, 
                                   LocalDateTime executionTime, String runStatus, String executorId, 
                                   String remark, String runAttachmentId, String runType) {
        this.usecaseExecutionHistoryId = usecaseExecutionHistoryId;
        this.directoryId = directoryId;
        this.usecaseId = usecaseId;
        this.executionTime = executionTime;
        this.runStatus = runStatus;
        this.executorId = executorId;
        this.remark = remark;
        this.runAttachmentId = runAttachmentId;
        this.runType = runType;
    }

    public TfUsecaseExecutionHistory(String directoryId, String usecaseId, LocalDateTime executionTime, 
                                   String runStatus, String executorId, String remark, String runAttachmentId, 
                                   String runType) {
        this.directoryId = directoryId;
        this.usecaseId = usecaseId;
        this.executionTime = executionTime;
        this.runStatus = runStatus;
        this.executorId = executorId;
        this.remark = remark;
        this.runAttachmentId = runAttachmentId;
        this.runType = runType;
    }

    // Getter和Setter方法（Lombok的@Data注解会自动生成，这里提供手动版本作为备选）
    public Integer getUsecaseExecutionHistoryId() {
        return usecaseExecutionHistoryId;
    }

    public void setUsecaseExecutionHistoryId(Integer usecaseExecutionHistoryId) {
        this.usecaseExecutionHistoryId = usecaseExecutionHistoryId;
    }

    public String getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(String directoryId) {
        this.directoryId = directoryId;
    }

    public String getUsecaseId() {
        return usecaseId;
    }

    public void setUsecaseId(String usecaseId) {
        this.usecaseId = usecaseId;
    }

    public LocalDateTime getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

    public String getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(String runStatus) {
        this.runStatus = runStatus;
    }

    public String getRunStatusName() {
        return runStatusName;
    }

    public void setRunStatusName(String runStatusName) {
        this.runStatusName = runStatusName;
    }

    public String getExecutorId() {
        return executorId;
    }

    public void setExecutorId(String executorId) {
        this.executorId = executorId;
    }

    public String getExecutorName() {
        return executorName;
    }

    public void setExecutorName(String executorName) {
        this.executorName = executorName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRunAttachmentId() {
        return runAttachmentId;
    }

    public void setRunAttachmentId(String runAttachmentId) {
        this.runAttachmentId = runAttachmentId;
    }

    public String getRunType() {
        return runType;
    }

    public void setRunType(String runType) {
        this.runType = runType;
    }

    public String getRunTypeName() {
        return runTypeName;
    }

    public void setRunTypeName(String runTypeName) {
        this.runTypeName = runTypeName;
    }
}
