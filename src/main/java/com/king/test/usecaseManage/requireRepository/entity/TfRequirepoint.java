package com.king.test.usecaseManage.requireRepository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 需求点实体类
 * 对应表: tf_requirepoint
 */
@Data
@TableName("tf_requirepoint")
public class TfRequirepoint {

    /**
     * 需求点Id
     */
    @TableId("REQUIRE_POINT_ID")
    private String requirePointId;

    /**
     * 需求点概述
     */
    @TableField("REQUIRE_POINT_DESC")
    private String requirePointDesc;

    /**
     * 测试系统ID
     */
    @TableField("SYSTEM_ID")
    private String systemId;

    /**
     * 系统名称（用于前端显示，不在数据库表中）
     */
    @TableField(exist = false)
    private String systemName;

    /**
     * 目录Id
     */
    @TableField("DIRECTORY_ID")
    private String directoryId;

    /**
     * 需求点类型
     */
    @TableField("REQUIRE_POINT_TYPE")
    private String requirePointType;

    /**
     * 评审状态
     */
    @TableField("REVIEW_STATUS")
    private String reviewStatus;

    /**
     * 评审状态值对应的数据字典名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String reviewStatusName;

    /**
     * 分析方法
     */
    @TableField("ANALYSIS_METHOD")
    private String analysisMethod;

    /**
     * 分析方法值对应的数据字典名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String analysisMethodName;

    /**
     * 需求状态仅用来查询显示，通过查询用例和需求关联表查询覆盖状态 0：未覆盖，1：已覆盖
     */
    @TableField(exist = false)
    private String requireStatus;

    /**
     * 需求状态值对应的数据字典名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String requireStatusName;

    /**
     * 设计人Id
     */
    @TableField("DESIGNER_ID")
    private String designerId;

    /**
     * 设计人姓名（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String designer;

    /**
     * 创建时间
     */
    @TableField("CREATE_TIME")
    private LocalDateTime createTime;

    /**
     * 修改人Id
     */
    @TableField("MODIFIER_ID")
    private String modifierId;

    /**
     * 修改人姓名（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String modifier;

    /**
     * 修改时间
     */
    @TableField("MODIFY_TIME")
    private LocalDateTime modifyTime;

    /**
     * 备注
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 所属送测单Id
     */
    @TableField("SEND_TEST_ID")
    private String sendTestId;

    /**
     * 所属工作包Id
     */
    @TableField("WORK_PACKAGE_ID")
    private Integer workPackageId;


    // 构造函数
    public TfRequirepoint() {
    }

    public TfRequirepoint(String requirePointId, String requirePointDesc, String systemId,
                         String directoryId, String requirePointType, String reviewStatus,
                         String analysisMethod, String requireStatus, String designerId,
                         LocalDateTime createTime, String modifierId, LocalDateTime modifyTime,
                         String remark, String sendTestId, Integer workPackageId) {
        this.requirePointId = requirePointId;
        this.requirePointDesc = requirePointDesc;
        this.systemId = systemId;
        this.directoryId = directoryId;
        this.requirePointType = requirePointType;
        this.reviewStatus = reviewStatus;
        this.analysisMethod = analysisMethod;
        this.requireStatus = requireStatus;
        this.designerId = designerId;
        this.createTime = createTime;
        this.modifierId = modifierId;
        this.modifyTime = modifyTime;
        this.remark = remark;
        this.sendTestId = sendTestId;
        this.workPackageId = workPackageId;
    }

    // Getter和Setter方法（Lombok的@Data注解会自动生成，这里提供手动版本作为备选）
    public String getRequirePointId() {
        return requirePointId;
    }

    public void setRequirePointId(String requirePointId) {
        this.requirePointId = requirePointId;
    }

    public String getRequirePointDesc() {
        return requirePointDesc;
    }

    public void setRequirePointDesc(String requirePointDesc) {
        this.requirePointDesc = requirePointDesc;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(String directoryId) {
        this.directoryId = directoryId;
    }

    public String getRequirePointType() {
        return requirePointType;
    }

    public void setRequirePointType(String requirePointType) {
        this.requirePointType = requirePointType;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getAnalysisMethod() {
        return analysisMethod;
    }

    public void setAnalysisMethod(String analysisMethod) {
        this.analysisMethod = analysisMethod;
    }

    public String getRequireStatus() {
        return requireStatus;
    }

    public void setRequireStatus(String requireStatus) {
        this.requireStatus = requireStatus;
    }

    public String getDesignerId() {
        return designerId;
    }

    public void setDesignerId(String designerId) {
        this.designerId = designerId;
    }

    public String getDesigner() {
        return designer;
    }

    public void setDesigner(String designer) {
        this.designer = designer;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getModifierId() {
        return modifierId;
    }

    public void setModifierId(String modifierId) {
        this.modifierId = modifierId;
    }

    public LocalDateTime getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(LocalDateTime modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSendTestId() {
        return sendTestId;
    }

    public void setSendTestId(String sendTestId) {
        this.sendTestId = sendTestId;
    }

    public Integer getWorkPackageId() {
        return workPackageId;
    }

    public void setWorkPackageId(Integer workPackageId) {
        this.workPackageId = workPackageId;
    }

    // 新增字段的 getter 和 setter 方法
    public String getReviewStatusName() {
        return reviewStatusName;
    }

    public void setReviewStatusName(String reviewStatusName) {
        this.reviewStatusName = reviewStatusName;
    }

    public String getAnalysisMethodName() {
        return analysisMethodName;
    }

    public void setAnalysisMethodName(String analysisMethodName) {
        this.analysisMethodName = analysisMethodName;
    }

    public String getRequireStatusName() {
        return requireStatusName;
    }

    public void setRequireStatusName(String requireStatusName) {
        this.requireStatusName = requireStatusName;
    }
}
