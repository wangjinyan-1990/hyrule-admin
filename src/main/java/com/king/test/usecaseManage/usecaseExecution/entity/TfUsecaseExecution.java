package com.king.test.usecaseManage.usecaseExecution.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用例执行实体类
 * 对应表: tf_usecase_execution
 */
@Data
@TableName("tf_usecase_execution")
public class TfUsecaseExecution {

    /**
     * 用例执行Id（自增主键）
     */
    @TableId("USECASE_EXECUTION_ID")
    private Integer usecaseExecutionId;

    /**
     * 用例所属目录Id
     */
    @TableField("DIRECTORY_ID")
    private String directoryId;

    /**
     * 测试系统ID
     */
    @TableField("SYSTEM_ID")
    private String systemId;

    /**
     * 用例Id
     */
    @TableField("USECASE_ID")
    private String usecaseId;

    /**
     * 用例名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String usecaseName;

    /**
     * 用例类型（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String usecaseType;

    /**
     * 用例类型名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String usecaseTypeName;

    /**
     * 测试要点（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String testPoint;

    /**
     * 测试要点名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String testPointName;

    /**
     * 用例性质（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String usecaseNature;

    /**
     * 用例性质名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String usecaseNatureName;

    /**
     * 优先级（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String prority;

    /**
     * 优先级名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String prorityName;

    /**
     * 前置条件（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String precondition;

    /**
     * 测试数据（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String testData;

    /**
     * 测试步骤（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String testStep;

    /**
     * 预期结果（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String expectedResult;

    /**
     * 计划执行日期
     */
    @TableField("PLAN_EXECUTION_DATE")
    private Date planExecutionDate;

    /**
     * 实际执行日期
     */
    @TableField("ACT_EXECUTION_TIME")
    private LocalDateTime actExecutionTime;

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
     * 计划执行人Id
     */
    @TableField("PLAN_EXECUTOR_ID")
    private String planExecutorId;

    /**
     * 计划执行人姓名（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String planExecutorName;

    /**
     * 实际执行人Id
     */
    @TableField("ACT_EXECUTOR_ID")
    private String actExecutorId;

    /**
     * 实际执行人姓名（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String actExecutorName;

    /**
     * 执行备注
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 最后一次执行时间
     */
    @TableField("LAST_EXECUTION_TIME")
    private LocalDateTime lastExecutionTime;

    /**
     * 创建在执行库人员Id
     */
    @TableField("EXECUTION_CREATOR_ID")
    private String executionCreatorId;

    /**
     * 创建在执行库人员姓名（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String executionCreatorName;

    /**
     * 创建在执行库时间
     */
    @TableField("EXECUTION_CREATE_TIME")
    private LocalDateTime executionCreateTime;

    // 构造函数
    public TfUsecaseExecution() {
    }

    public TfUsecaseExecution(Integer usecaseExecutionId, String directoryId, String systemId,
                              String usecaseId, Date planExecutionDate, LocalDateTime actExecutionTime,
                             String runStatus, String planExecutorId, String actExecutorId,
                             String remark, LocalDateTime lastExecutionTime, String executionCreatorId,
                             LocalDateTime executionCreateTime) {
        this.usecaseExecutionId = usecaseExecutionId;
        this.directoryId = directoryId;
        this.systemId = systemId;
        this.usecaseId = usecaseId;
        this.planExecutionDate = planExecutionDate;
        this.actExecutionTime = actExecutionTime;
        this.runStatus = runStatus;
        this.planExecutorId = planExecutorId;
        this.actExecutorId = actExecutorId;
        this.remark = remark;
        this.lastExecutionTime = lastExecutionTime;
        this.executionCreatorId = executionCreatorId;
        this.executionCreateTime = executionCreateTime;
    }

    public TfUsecaseExecution(String directoryId, String usecaseId, Date planExecutionDate, String systemId,
                             LocalDateTime actExecutionTime, String runStatus, String planExecutorId,
                             String actExecutorId, String remark, LocalDateTime lastExecutionTime,
                             String executionCreatorId, LocalDateTime executionCreateTime) {
        this.directoryId = directoryId;
        this.usecaseId = usecaseId;
        this.systemId = systemId;
        this.planExecutionDate = planExecutionDate;
        this.actExecutionTime = actExecutionTime;
        this.runStatus = runStatus;
        this.planExecutorId = planExecutorId;
        this.actExecutorId = actExecutorId;
        this.remark = remark;
        this.lastExecutionTime = lastExecutionTime;
        this.executionCreatorId = executionCreatorId;
        this.executionCreateTime = executionCreateTime;
    }

    // Getter和Setter方法（Lombok的@Data注解会自动生成，这里提供手动版本作为备选）
    public Integer getUsecaseExecutionId() {
        return usecaseExecutionId;
    }

    public void setUsecaseExecutionId(Integer usecaseExecutionId) {
        this.usecaseExecutionId = usecaseExecutionId;
    }

    public String getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(String directoryId) {
        this.directoryId = directoryId;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getUsecaseId() {
        return usecaseId;
    }

    public void setUsecaseId(String usecaseId) {
        this.usecaseId = usecaseId;
    }

    public Date getPlanExecutionDate() {
        return planExecutionDate;
    }

    public void setPlanExecutionDate(Date planExecutionDate) {
        this.planExecutionDate = planExecutionDate;
    }

    public LocalDateTime getActExecutionTime() {
        return actExecutionTime;
    }

    public void setActExecutionTime(LocalDateTime actExecutionTime) {
        this.actExecutionTime = actExecutionTime;
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

    public String getPlanExecutorId() {
        return planExecutorId;
    }

    public void setPlanExecutorId(String planExecutorId) {
        this.planExecutorId = planExecutorId;
    }

    public String getPlanExecutorName() {
        return planExecutorName;
    }

    public void setPlanExecutorName(String planExecutorName) {
        this.planExecutorName = planExecutorName;
    }

    public String getActExecutorId() {
        return actExecutorId;
    }

    public void setActExecutorId(String actExecutorId) {
        this.actExecutorId = actExecutorId;
    }

    public String getActExecutorName() {
        return actExecutorName;
    }

    public void setActExecutorName(String actExecutorName) {
        this.actExecutorName = actExecutorName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getLastExecutionTime() {
        return lastExecutionTime;
    }

    public void setLastExecutionTime(LocalDateTime lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }

    public String getExecutionCreatorId() {
        return executionCreatorId;
    }

    public void setExecutionCreatorId(String executionCreatorId) {
        this.executionCreatorId = executionCreatorId;
    }

    public String getExecutionCreatorName() {
        return executionCreatorName;
    }

    public void setExecutionCreatorName(String executionCreatorName) {
        this.executionCreatorName = executionCreatorName;
    }

    public LocalDateTime getExecutionCreateTime() {
        return executionCreateTime;
    }

    public void setExecutionCreateTime(LocalDateTime executionCreateTime) {
        this.executionCreateTime = executionCreateTime;
    }
}
