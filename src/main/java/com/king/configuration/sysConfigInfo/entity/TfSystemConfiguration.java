package com.king.configuration.sysConfigInfo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统配置信息实体类
 * 对应表: tf_system_configuration
 */
@Data
@TableName("tf_system_configuration")
public class TfSystemConfiguration {

    /**
     * 系统配置Id
     */
    @TableId(value = "CONFIGURATION_ID", type = IdType.AUTO)
    private Integer configurationId;

    /**
     * 系统ID
     */
    @TableField("SYSTEM_ID")
    private String systemId;

    /**
     * 访问令牌
     */
    @TableField("PRIVATE_TOKEN")
    private String privateToken;

    /**
     * 配置人员
     */
    @TableField("CONFIGURATION_PEOPLEIDS")
    private String configurationPeopleIds;

    /**
     * 系统名称（用于前端显示，不在数据库表中）
     */
    @TableField(exist = false)
    private String systemName;

    /**
     * 系统简称（用于前端显示，不对应数据库表字段）
     */
    @TableField(exist = false)
    private String sysAbbreviation;

    /**
     * 配置人员名称（用于前端显示，不在数据库表中）
     */
    @TableField(exist = false)
    private String configurationPeopleNames;

    /**
     * 无参构造函数
     */
    public TfSystemConfiguration() {
    }

    /**
     * 全参构造函数
     * @param configurationId 系统配置Id
     * @param systemId 系统ID
     * @param privateToken 访问令牌
     * @param configurationPeopleIds 配置人员
     */
    public TfSystemConfiguration(Integer configurationId, String systemId, String privateToken, String configurationPeopleIds) {
        this.configurationId = configurationId;
        this.systemId = systemId;
        this.privateToken = privateToken;
        this.configurationPeopleIds = configurationPeopleIds;
    }

    // Getter和Setter方法
    /**
     * 获取系统配置Id
     * @return 系统配置Id
     */
    public Integer getConfigurationId() {
        return configurationId;
    }

    /**
     * 设置系统配置Id
     * @param configurationId 系统配置Id
     */
    public void setConfigurationId(Integer configurationId) {
        this.configurationId = configurationId;
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
     * 获取访问令牌
     * @return 访问令牌
     */
    public String getPrivateToken() {
        return privateToken;
    }

    /**
     * 设置访问令牌
     * @param privateToken 访问令牌
     */
    public void setPrivateToken(String privateToken) {
        this.privateToken = privateToken;
    }

    /**
     * 获取配置人员
     * @return 配置人员
     */
    public String getConfigurationPeopleIds() {
        return configurationPeopleIds;
    }

    /**
     * 设置配置人员
     * @param configurationPeopleIds 配置人员
     */
    public void setConfigurationPeopleIds(String configurationPeopleIds) {
        this.configurationPeopleIds = configurationPeopleIds;
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

    /**
     * 获取系统简称
     * @return 系统简称
     */
    public String getSysAbbreviation() {
        return sysAbbreviation;
    }

    /**
     * 设置系统简称
     * @param sysAbbreviation 系统简称
     */
    public void setSysAbbreviation(String sysAbbreviation) {
        this.sysAbbreviation = sysAbbreviation;
    }

    /**
     * 获取配置人员名称
     * @return 配置人员名称
     */
    public String getConfigurationPeopleNames() {
        return configurationPeopleNames;
    }

    /**
     * 设置配置人员名称
     * @param configurationPeopleNames 配置人员名称
     */
    public void setConfigurationPeopleNames(String configurationPeopleNames) {
        this.configurationPeopleNames = configurationPeopleNames;
    }
}

