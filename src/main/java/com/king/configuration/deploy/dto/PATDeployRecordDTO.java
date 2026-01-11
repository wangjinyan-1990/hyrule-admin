package com.king.configuration.deploy.dto;

/**
 * PAT发版登记DTO
 * 用于接收外部API参数
 */
public class PATDeployRecordDTO {
    
    /**
     * 系统简称
     */
    private String sysAbbreviation;
    
    /**
     * 送测单编号
     */
    private String sendTestCode;
    
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

    public String getSendTestCode() {
        return sendTestCode;
    }

    public void setSendTestCode(String sendTestCode) {
        this.sendTestCode = sendTestCode;
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

