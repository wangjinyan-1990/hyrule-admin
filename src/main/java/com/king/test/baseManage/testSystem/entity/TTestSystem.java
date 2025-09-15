package com.king.test.baseManage.testSystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 测试系统实体类
 */
@Data
@TableName("t_test_system")
public class TTestSystem {

    /**
     * 测试系统ID
     */
    @TableId(value = "SYSTEM_ID")
    private String systemId;

    /**
     * 测试系统名称
     */
    @TableField("SYSTEM_NAME")
    private String systemName;

    /**
     * 负责的机构
     */
    @TableField("ORG_ID")
    private String orgId;

    /**
     * 系统类型：1、初测; 2、优化
     */
    @TableField("SYSTEM_TYPE")
    private String systemType;

    /**
     * 系统阶段:1、准备阶段;2、实施阶段;3、收尾阶段
     */
    @TableField("SYSTEM_STAGE")
    private String systemStage;

    /**
     * 是否在使用:0是不使用;1是在使用
     */
    @TableField("IS_USE")
    private String isUse;

    /**
     * 系统描述
     */
    @TableField("SYSTEM_DESCRIPTION")
    private String systemDescription;

    /**
     * 创建者
     */
    @TableField("CREATOR_ID")
    private String creatorId;

    /**
     * 创建日期
     */
    @TableField("CREATE_TIME")
    private Date createTime;

    /**
     * 修改者
     */
    @TableField("MENDER_ID")
    private String menderId;

    /**
     * 修改日期
     */
    @TableField("MODIFY_TIME")
    private Date modifyTime;

    /**
     * 计划开始日期
     */
    @TableField("PLAN_START_TIME")
    private Date planStartTime;

    /**
     * 计划结束日期
     */
    @TableField("PLAN_END_TIME")
    private Date planEndTime;

    /**
     * 实际开始日期
     */
    @TableField("ACTUAL_START_TIME")
    private Date actualStartTime;

    /**
     * 实际结束日期
     */
    @TableField("ACTUAL_END_TIME")
    private Date actualEndTime;

    /**
     * 系统测试经理Id
     */
    @TableField("SYSTEM_TEST_MANAGER_ID")
    private String systemTestManagerId;

    /**
     * 系统开发经理Id
     */
    @TableField("SYSTEM_DEV_MANAGER_ID")
    private String systemDevManagerId;

    /**
     * 修改历史记录
     */
    @TableField("CHANGE_HISTORY")
    private String changeHistory;

    // 用于显示的关联字段
    /**
     * 所属机构名称（用于前端显示）
     */
    @TableField(exist = false)
    private String orgName;

    /**
     * 测试经理名称（用于前端显示）
     */
    @TableField(exist = false)
    private String testManagerName;

    /**
     * 开发经理名称（用于前端显示）
     */
    @TableField(exist = false)
    private String devManagerName;

    // Getter和Setter方法
    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public String getSystemStage() {
        return systemStage;
    }

    public void setSystemStage(String systemStage) {
        this.systemStage = systemStage;
    }

    public String getIsUse() {
        return isUse;
    }

    public void setIsUse(String isUse) {
        this.isUse = isUse;
    }

    public String getSystemDescription() {
        return systemDescription;
    }

    public void setSystemDescription(String systemDescription) {
        this.systemDescription = systemDescription;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getMenderId() {
        return menderId;
    }

    public void setMenderId(String menderId) {
        this.menderId = menderId;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Date getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(Date planStartTime) {
        this.planStartTime = planStartTime;
    }

    public Date getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
    }

    public Date getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public Date getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(Date actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public String getSystemTestManagerId() {
        return systemTestManagerId;
    }

    public void setSystemTestManagerId(String systemTestManagerId) {
        this.systemTestManagerId = systemTestManagerId;
    }

    public String getSystemDevManagerId() {
        return systemDevManagerId;
    }

    public void setSystemDevManagerId(String systemDevManagerId) {
        this.systemDevManagerId = systemDevManagerId;
    }

    public String getChangeHistory() {
        return changeHistory;
    }

    public void setChangeHistory(String changeHistory) {
        this.changeHistory = changeHistory;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getTestManagerName() {
        return testManagerName;
    }

    public void setTestManagerName(String testManagerName) {
        this.testManagerName = testManagerName;
    }

    public String getDevManagerName() {
        return devManagerName;
    }

    public void setDevManagerName(String devManagerName) {
        this.devManagerName = devManagerName;
    }
}