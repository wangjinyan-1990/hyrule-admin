package com.king.configuration.deploy.dto;

import com.baomidou.mybatisplus.annotation.TableField;

/**
 * PAT发版登记DTO
 * 用于接收外部API参数
 */
public class DeployRecordApiDTO {

    /**
     * 系统简称
     */
    private String sysAbbreviation;

    /**
     * 测试阶段:SIT、PAT
     */
    private String testStage;

    /**
     * 送测单信息
     */
    private String sendTestInfo;

    /**
     * 组件信息
     */
    private String componentInfo;

    /**
     * 是否执行SQL
     */
    private Boolean isRunSql;

    /**
     * 是否更新配置
     */
    private Boolean isUpdateConfig;

    /**
     * 版本登记数量
     */
    private Integer recordNum;

    /**
     * 代码清单
     */
    private String codeList;

    public String getSysAbbreviation() {
        return sysAbbreviation;
    }

    public void setSysAbbreviation(String sysAbbreviation) {
        this.sysAbbreviation = sysAbbreviation;
    }

    public String getTestStage() {
        return testStage;
    }

    public void setTestStage(String testStage) {
        this.testStage = testStage;
    }

    public String getSendTestInfo() {
        return sendTestInfo;
    }

    public void setSendTestInfo(String sendTestInfo) {
        this.sendTestInfo = sendTestInfo;
    }

    public String getComponentInfo() {
        return componentInfo;
    }

    public void setComponentInfo(String componentInfo) {
        this.componentInfo = componentInfo;
    }

    public Boolean getIsRunSql() {
        return isRunSql;
    }

    public void setIsRunSql(Boolean isRunSql) {
        this.isRunSql = isRunSql;
    }

    public Boolean getIsUpdateConfig() {
        return isUpdateConfig;
    }

    public void setIsUpdateConfig(Boolean isUpdateConfig) {
        this.isUpdateConfig = isUpdateConfig;
    }

    public Integer getRecordNum() {
        return recordNum;
    }

    public void setRecordNum(Integer recordNum) {
        this.recordNum = recordNum;
    }

    public String getCodeList() {
        return codeList;
    }

    public void setCodeList(String codeList) {
        this.codeList = codeList;
    }
}

