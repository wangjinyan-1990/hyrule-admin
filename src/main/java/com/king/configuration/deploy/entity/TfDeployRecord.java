package com.king.configuration.deploy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 发版登记实体类
 * 对应表: tf_deploy_record
 */
@Data
@TableName("tf_deploy_record")
public class TfDeployRecord {

    /**
     * 部署Id
     */
    @TableId(value = "DEPLOY_ID", type = IdType.AUTO)
    private Integer deployId;

    /**
     * 测试阶段:SIT、PAT
     */
    @TableField("TEST_STAGE")
    private String testStage;

    /**
     * 系统ID
     */
    @TableField("SYSTEM_ID")
    private String systemId;

    /**
     * 组件信息
     */
    @TableField("COMPONENT_INFO")
    private String componentInfo;

    /**
     * 版本号
     */
    @TableField("VERSION_CODE")
    private String versionCode;

    /**
     * 版本登记数量
     */
    @TableField("RECORD_NUM")
    private Integer recordNum;

    /**
     * 代码清单
     */
    @TableField("CODE_LIST")
    private String codeList;

    /**
     * 是否执行sql
     */
    @TableField("IS_RUN_SQL")
    private Boolean isRunSql;

    /**
     * 是否更新配置
     */
    @TableField("IS_UPDATE_CONFIG")
    private Boolean isUpdateConfig;

    /**
     * 部署时间点
     */
    @TableField("DEPLOY_TIME")
    private LocalDateTime deployTime;

    /**
     * 送测单编号
     */
    @TableField("SEND_TEST_CODE")
    private String sendTestCode;

    /**
     * 系统名称（用于前端显示，不在数据库表中）
     */
    @TableField(exist = false)
    private String systemName;


    /**
     * 无参构造函数
     */
    public TfDeployRecord() {
    }

    /**
     * 全参构造函数
     * @param deployId 部署Id
     * @param testStage 测试阶段
     * @param systemId 系统ID
     * @param componentInfo 组件信息
     * @param versionCode 版本号
     * @param recordNum 版本登记数量
     * @param codeList 代码清单
     * @param isRunSql 是否执行sql
     * @param isUpdateConfig 是否更新配置
     * @param deployTime 部署时间点
     * @param sendTestCode 送测单编号
     */
    public TfDeployRecord(Integer deployId, String testStage, String systemId, String componentInfo,
                          String versionCode, Integer recordNum, String codeList, Boolean isRunSql,
                          Boolean isUpdateConfig, LocalDateTime deployTime, String sendTestCode) {
        this.deployId = deployId;
        this.testStage = testStage;
        this.systemId = systemId;
        this.componentInfo = componentInfo;
        this.versionCode = versionCode;
        this.recordNum = recordNum;
        this.codeList = codeList;
        this.isRunSql = isRunSql;
        this.isUpdateConfig = isUpdateConfig;
        this.deployTime = deployTime;
        this.sendTestCode = sendTestCode;
    }

    // Getter和Setter方法
    /**
     * 获取部署Id
     * @return 部署Id
     */
    public Integer getDeployId() {
        return deployId;
    }

    /**
     * 设置部署Id
     * @param deployId 部署Id
     */
    public void setDeployId(Integer deployId) {
        this.deployId = deployId;
    }

    /**
     * 获取测试阶段
     * @return 测试阶段
     */
    public String getTestStage() {
        return testStage;
    }

    /**
     * 设置测试阶段
     * @param testStage 测试阶段
     */
    public void setTestStage(String testStage) {
        this.testStage = testStage;
    }

    /**
     * 获取系统ID
     * @return 系统ID
     */
    public String getSystemId() {
        return systemId;
    }

    /**
     * 设置系统ID
     * @param systemId 系统ID
     */
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    /**
     * 获取组件信息
     * @return 组件信息
     */
    public String getComponentInfo() {
        return componentInfo;
    }

    /**
     * 设置组件信息
     * @param componentInfo 组件信息
     */
    public void setComponentInfo(String componentInfo) {
        this.componentInfo = componentInfo;
    }

    /**
     * 获取版本号
     * @return 版本号
     */
    public String getVersionCode() {
        return versionCode;
    }

    /**
     * 设置版本号
     * @param versionCode 版本号
     */
    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    /**
     * 获取版本登记数量
     * @return 版本登记数量
     */
    public Integer getRecordNum() {
        return recordNum;
    }

    /**
     * 设置版本登记数量
     * @param recordNum 版本登记数量
     */
    public void setRecordNum(Integer recordNum) {
        this.recordNum = recordNum;
    }

    /**
     * 获取代码清单
     * @return 代码清单
     */
    public String getCodeList() {
        return codeList;
    }

    /**
     * 设置代码清单
     * @param codeList 代码清单
     */
    public void setCodeList(String codeList) {
        this.codeList = codeList;
    }

    /**
     * 获取是否执行sql
     * @return 是否执行sql
     */
    public Boolean getIsRunSql() {
        return isRunSql;
    }

    /**
     * 设置是否执行sql
     * @param isRunSql 是否执行sql
     */
    public void setIsRunSql(Boolean isRunSql) {
        this.isRunSql = isRunSql;
    }

    /**
     * 获取是否更新配置
     * @return 是否更新配置
     */
    public Boolean getIsUpdateConfig() {
        return isUpdateConfig;
    }

    /**
     * 设置是否更新配置
     * @param isUpdateConfig 是否更新配置
     */
    public void setIsUpdateConfig(Boolean isUpdateConfig) {
        this.isUpdateConfig = isUpdateConfig;
    }

    /**
     * 获取部署时间点
     * @return 部署时间点
     */
    public LocalDateTime getDeployTime() {
        return deployTime;
    }

    /**
     * 设置部署时间点
     * @param deployTime 部署时间点
     */
    public void setDeployTime(LocalDateTime deployTime) {
        this.deployTime = deployTime;
    }

    /**
     * 获取送测单编号
     * @return 送测单编号
     */
    public String getSendTestCode() {
        return sendTestCode;
    }

    /**
     * 设置送测单编号
     * @param sendTestCode 送测单编号
     */
    public void setSendTestCode(String sendTestCode) {
        this.sendTestCode = sendTestCode;
    }

    /**
     * 获取系统名称
     * @return 系统名称
     */
    public String getSystemName() {
        return systemName;
    }

    /**
     * 设置系统名称
     * @param systemName 系统名称
     */
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

}

