package com.king.test.usecaseManage.usecaseRepository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试用例实体类
 * 对应表: tf_usecase
 */
@Data
@TableName("tf_usecase")
public class TfUsecase {

    /**
     * 用例Id
     */
    @TableId("USECASE_ID")
    private String usecaseId;

    /**
     * 用例所属目录Id
     */
    @TableField("DIRECTORY_ID")
    private String directoryId;

    /**
     * 用例名称
     */
    @TableField("USECASE_NAME")
    private String usecaseName;

    /**
     * 创建人Id
     */
    @TableField("CREATOR_ID")
    private String creatorId;

    /**
     * 创建人姓名（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String creator;

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
     * 测试系统ID
     */
    @TableField("SYSTEM_ID")
    private String systemId;

    /**
     * 是否冒烟测试:0-否;1-是
     */
    @TableField("IS_SMOKE_TEST")
    private String isSmokeTest;

    /**
     * 用例类型：功能测试、非功能测试
     */
    @TableField("USECASE_TYPE")
    private String usecaseType;

    /**
     * 用例类型名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String usecaseTypeName;

    /**
     * 测试要点：界面、功能、流程、规则......
     */
    @TableField("TEST_POINT")
    private String testPoint;

    /**
     * 测试要点名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String testPointName;

    /**
     * 用例性质：正向用例、反向用例
     */
    @TableField("USECASE_NATURE")
    private String usecaseNature;

    /**
     * 用例性质名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String usecaseNatureName;

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
     * 测试集下最新执行状态
     */
    @TableField("LATEST_EXE_STATUS")
    private String latestExeStatus;

    /**
     * 执行状态名称（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String latestExeStatusName;

    /**
     * 前置条件
     */
    @TableField("PRECONDITION")
    private String precondition;

    /**
     * 测试数据
     */
    @TableField("TEST_DATA")
    private String testData;

    /**
     * 测试步骤
     */
    @TableField("TEST_STEP")
    private String testStep;

    /**
     * 期望结果
     */
    @TableField("EXPECTED_RESULT")
    private String expectedResult;

    /**
     * 所属工作包Id
     */
    @TableField("WORK_PACKAGE_ID")
    private Integer workPackageId;

    // 构造函数
    public TfUsecase() {
    }

    public TfUsecase(String usecaseId, String directoryId, String usecaseName, String creatorId, 
                     LocalDateTime createTime, String modifierId, LocalDateTime modifyTime, 
                     String systemId, String isSmokeTest, String usecaseType, String testPoint, 
                     String usecaseNature, String prority, String latestExeStatus, 
                     String precondition, String testData, String testStep, 
                     String expectedResult, Integer workPackageId) {
        this.usecaseId = usecaseId;
        this.directoryId = directoryId;
        this.usecaseName = usecaseName;
        this.creatorId = creatorId;
        this.createTime = createTime;
        this.modifierId = modifierId;
        this.modifyTime = modifyTime;
        this.systemId = systemId;
        this.isSmokeTest = isSmokeTest;
        this.usecaseType = usecaseType;
        this.testPoint = testPoint;
        this.usecaseNature = usecaseNature;
        this.prority = prority;
        this.latestExeStatus = latestExeStatus;
        this.precondition = precondition;
        this.testData = testData;
        this.testStep = testStep;
        this.expectedResult = expectedResult;
        this.workPackageId = workPackageId;
    }

    // Getter和Setter方法（Lombok的@Data注解会自动生成，这里提供手动版本作为备选）
    public String getUsecaseId() {
        return usecaseId;
    }

    public void setUsecaseId(String usecaseId) {
        this.usecaseId = usecaseId;
    }

    public String getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(String directoryId) {
        this.directoryId = directoryId;
    }

    public String getUsecaseName() {
        return usecaseName;
    }

    public void setUsecaseName(String usecaseName) {
        this.usecaseName = usecaseName;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public LocalDateTime getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(LocalDateTime modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getIsSmokeTest() {
        return isSmokeTest;
    }

    public void setIsSmokeTest(String isSmokeTest) {
        this.isSmokeTest = isSmokeTest;
    }

    public String getUsecaseType() {
        return usecaseType;
    }

    public void setUsecaseType(String usecaseType) {
        this.usecaseType = usecaseType;
    }

    public String getUsecaseTypeName() {
        return usecaseTypeName;
    }

    public void setUsecaseTypeName(String usecaseTypeName) {
        this.usecaseTypeName = usecaseTypeName;
    }

    public String getTestPoint() {
        return testPoint;
    }

    public void setTestPoint(String testPoint) {
        this.testPoint = testPoint;
    }

    public String getTestPointName() {
        return testPointName;
    }

    public void setTestPointName(String testPointName) {
        this.testPointName = testPointName;
    }

    public String getUsecaseNature() {
        return usecaseNature;
    }

    public void setUsecaseNature(String usecaseNature) {
        this.usecaseNature = usecaseNature;
    }

    public String getUsecaseNatureName() {
        return usecaseNatureName;
    }

    public void setUsecaseNatureName(String usecaseNatureName) {
        this.usecaseNatureName = usecaseNatureName;
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

    public String getLatestExeStatus() {
        return latestExeStatus;
    }

    public void setLatestExeStatus(String latestExeStatus) {
        this.latestExeStatus = latestExeStatus;
    }

    public String getLatestExeStatusName() {
        return latestExeStatusName;
    }

    public void setLatestExeStatusName(String latestExeStatusName) {
        this.latestExeStatusName = latestExeStatusName;
    }

    public String getPrecondition() {
        return precondition;
    }

    public void setPrecondition(String precondition) {
        this.precondition = precondition;
    }

    public String getTestData() {
        return testData;
    }

    public void setTestData(String testData) {
        this.testData = testData;
    }

    public String getTestStep() {
        return testStep;
    }

    public void setTestStep(String testStep) {
        this.testStep = testStep;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public Integer getWorkPackageId() {
        return workPackageId;
    }

    public void setWorkPackageId(Integer workPackageId) {
        this.workPackageId = workPackageId;
    }
}
