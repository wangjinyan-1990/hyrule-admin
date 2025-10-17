package com.king.test.bugManage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 缺陷实体类
 * 对应表: tf_bug
 */
@Data
@TableName("tf_bug")
public class TfBug {

    /**
     * 缺陷Id（自增主键）
     */
    @TableId("BUG_ID")
    private Integer bugId;

    /**
     * 缺陷名称
     */
    @TableField("BUG_NAME")
    private String bugName;

    /**
     * 测试系统ID
     */
    @TableField("SYSTEM_ID")
    private String systemId;

    /**
     * 缺陷状态
     */
    @TableField("BUG_STATE")
    private String bugState;

    /**
     * 缺陷状态名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String bugStateName;

    /**
     * 关联的用例Id
     */
    @TableField("USECASE_ID")
    private String usecaseId;

    /**
     * 缺陷来源
     */
    @TableField("BUG_SOURCE")
    private String bugSource;

    /**
     * 缺陷来源名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String bugSourceName;

    /**
     * 优先级
     */
    @TableField("PRORITY")
    private String prority;

    /**
     * 优先级名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String prorityName;

    /**
     * 缺陷描述
     */
    @TableField("BUG_DESCRIPITION")
    private String bugDescription;

    /**
     * 缺陷严重级别
     */
    @TableField("BUG_SEVERITY_LEVEL")
    private Integer bugSeverityLevel;

    /**
     * 缺陷严重级别名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String bugSeverityLevelName;

    /**
     * 缺陷关闭原因
     */
    @TableField("CLOSE_REASON")
    private String closeReason;

    /**
     * 提交人Id
     */
    @TableField("SUBMITTER_ID")
    private String submitterId;

    /**
     * 提交人姓名（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String submitterName;

    /**
     * 开发人员Id
     */
    @TableField("DEVELOPER_ID")
    private String developerId;

    /**
     * 开发人员姓名（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String developerName;

    /**
     * 开发组长Id
     */
    @TableField("DEV_LEADER_ID")
    private String devLeaderId;

    /**
     * 开发组长姓名（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String devLeaderName;

    /**
     * 验证人Id
     */
    @TableField("CHECKER_ID")
    private String checkerId;

    /**
     * 验证人姓名（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String checkerName;

    /**
     * 缺陷类型
     */
    @TableField("BUG_TYPE")
    private String bugType;

    /**
     * 缺陷类型名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String bugTypeName;

    /**
     * 执行库目录Id
     */
    @TableField("DIRECTORY_ID")
    private Integer directoryId;

    /**
     * 提交时间
     */
    @TableField("COMMIT_TIME")
    private LocalDateTime commitTime;

    /**
     * 确认时间
     */
    @TableField("CONFIRMED_TIME")
    private LocalDateTime confirmedTime;

    /**
     * 分配时间
     */
    @TableField("ASSIGNED_TIME")
    private LocalDateTime assignedTime;

    /**
     * 解决时间
     */
    @TableField("RESOLVED_TIME")
    private LocalDateTime resolvedTime;

    /**
     * 待验证时间
     */
    @TableField("WAITCHECK_TIME")
    private LocalDateTime waitCheckTime;

    /**
     * 关闭时间
     */
    @TableField("CLOSE_TIME")
    private LocalDateTime closeTime;

    /**
     * 解决次数
     */
    @TableField("SOLVE_VOLUME")
    private Integer solveVolume;

    /**
     * 提交次数
     */
    @TableField("SUBMITTED_VOLUME")
    private Integer submittedVolume;

    /**
     * 缺陷流程模板
     */
    @TableField("BUG_WORKFLOW_ID")
    private Integer bugWorkflowId;

    // 构造函数
    public TfBug() {
    }

    public TfBug(Integer bugId, String bugName, String systemId, String bugState, String usecaseId, 
                 String bugSource, String prority, String bugDescription, Integer bugSeverityLevel, 
                 String closeReason, String submitterId, String developerId, String devLeaderId, 
                 String checkerId, String bugType, Integer directoryId, LocalDateTime commitTime, 
                 LocalDateTime confirmedTime, LocalDateTime assignedTime, LocalDateTime resolvedTime, 
                 LocalDateTime waitCheckTime, LocalDateTime closeTime, Integer solveVolume, 
                 Integer submittedVolume, Integer bugWorkflowId) {
        this.bugId = bugId;
        this.bugName = bugName;
        this.systemId = systemId;
        this.bugState = bugState;
        this.usecaseId = usecaseId;
        this.bugSource = bugSource;
        this.prority = prority;
        this.bugDescription = bugDescription;
        this.bugSeverityLevel = bugSeverityLevel;
        this.closeReason = closeReason;
        this.submitterId = submitterId;
        this.developerId = developerId;
        this.devLeaderId = devLeaderId;
        this.checkerId = checkerId;
        this.bugType = bugType;
        this.directoryId = directoryId;
        this.commitTime = commitTime;
        this.confirmedTime = confirmedTime;
        this.assignedTime = assignedTime;
        this.resolvedTime = resolvedTime;
        this.waitCheckTime = waitCheckTime;
        this.closeTime = closeTime;
        this.solveVolume = solveVolume;
        this.submittedVolume = submittedVolume;
        this.bugWorkflowId = bugWorkflowId;
    }

    // Getter和Setter方法（Lombok的@Data注解会自动生成，这里提供手动版本作为备选）
    public Integer getBugId() {
        return bugId;
    }

    public void setBugId(Integer bugId) {
        this.bugId = bugId;
    }

    public String getBugName() {
        return bugName;
    }

    public void setBugName(String bugName) {
        this.bugName = bugName;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getBugState() {
        return bugState;
    }

    public void setBugState(String bugState) {
        this.bugState = bugState;
    }

    public String getBugStateName() {
        return bugStateName;
    }

    public void setBugStateName(String bugStateName) {
        this.bugStateName = bugStateName;
    }

    public String getUsecaseId() {
        return usecaseId;
    }

    public void setUsecaseId(String usecaseId) {
        this.usecaseId = usecaseId;
    }

    public String getBugSource() {
        return bugSource;
    }

    public void setBugSource(String bugSource) {
        this.bugSource = bugSource;
    }

    public String getBugSourceName() {
        return bugSourceName;
    }

    public void setBugSourceName(String bugSourceName) {
        this.bugSourceName = bugSourceName;
    }

    public String getPrority() {
        return prority;
    }

    public void setPrority(String prority) {
        this.prority = prority;
    }

    public String getProrityName() {
        return prorityName;
    }

    public void setProrityName(String prorityName) {
        this.prorityName = prorityName;
    }

    public String getBugDescription() {
        return bugDescription;
    }

    public void setBugDescription(String bugDescription) {
        this.bugDescription = bugDescription;
    }

    public Integer getBugSeverityLevel() {
        return bugSeverityLevel;
    }

    public void setBugSeverityLevel(Integer bugSeverityLevel) {
        this.bugSeverityLevel = bugSeverityLevel;
    }

    public String getBugSeverityLevelName() {
        return bugSeverityLevelName;
    }

    public void setBugSeverityLevelName(String bugSeverityLevelName) {
        this.bugSeverityLevelName = bugSeverityLevelName;
    }

    public String getCloseReason() {
        return closeReason;
    }

    public void setCloseReason(String closeReason) {
        this.closeReason = closeReason;
    }

    public String getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(String submitterId) {
        this.submitterId = submitterId;
    }

    public String getSubmitterName() {
        return submitterName;
    }

    public void setSubmitterName(String submitterName) {
        this.submitterName = submitterName;
    }

    public String getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(String developerId) {
        this.developerId = developerId;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public String getDevLeaderId() {
        return devLeaderId;
    }

    public void setDevLeaderId(String devLeaderId) {
        this.devLeaderId = devLeaderId;
    }

    public String getDevLeaderName() {
        return devLeaderName;
    }

    public void setDevLeaderName(String devLeaderName) {
        this.devLeaderName = devLeaderName;
    }

    public String getCheckerId() {
        return checkerId;
    }

    public void setCheckerId(String checkerId) {
        this.checkerId = checkerId;
    }

    public String getCheckerName() {
        return checkerName;
    }

    public void setCheckerName(String checkerName) {
        this.checkerName = checkerName;
    }

    public String getBugType() {
        return bugType;
    }

    public void setBugType(String bugType) {
        this.bugType = bugType;
    }

    public String getBugTypeName() {
        return bugTypeName;
    }

    public void setBugTypeName(String bugTypeName) {
        this.bugTypeName = bugTypeName;
    }

    public Integer getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(Integer directoryId) {
        this.directoryId = directoryId;
    }

    public LocalDateTime getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(LocalDateTime commitTime) {
        this.commitTime = commitTime;
    }

    public LocalDateTime getConfirmedTime() {
        return confirmedTime;
    }

    public void setConfirmedTime(LocalDateTime confirmedTime) {
        this.confirmedTime = confirmedTime;
    }

    public LocalDateTime getAssignedTime() {
        return assignedTime;
    }

    public void setAssignedTime(LocalDateTime assignedTime) {
        this.assignedTime = assignedTime;
    }

    public LocalDateTime getResolvedTime() {
        return resolvedTime;
    }

    public void setResolvedTime(LocalDateTime resolvedTime) {
        this.resolvedTime = resolvedTime;
    }

    public LocalDateTime getWaitCheckTime() {
        return waitCheckTime;
    }

    public void setWaitCheckTime(LocalDateTime waitCheckTime) {
        this.waitCheckTime = waitCheckTime;
    }

    public LocalDateTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalDateTime closeTime) {
        this.closeTime = closeTime;
    }

    public Integer getSolveVolume() {
        return solveVolume;
    }

    public void setSolveVolume(Integer solveVolume) {
        this.solveVolume = solveVolume;
    }

    public Integer getSubmittedVolume() {
        return submittedVolume;
    }

    public void setSubmittedVolume(Integer submittedVolume) {
        this.submittedVolume = submittedVolume;
    }

    public Integer getBugWorkflowId() {
        return bugWorkflowId;
    }

    public void setBugWorkflowId(Integer bugWorkflowId) {
        this.bugWorkflowId = bugWorkflowId;
    }
}

