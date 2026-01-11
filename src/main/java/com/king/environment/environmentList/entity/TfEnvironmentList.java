package com.king.environment.environmentList.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 环境清单实体类
 * 对应表: tf_environment_list
 */
@Data
@TableName("tf_environment_list")
public class TfEnvironmentList {

    /**
     * 环境清单Id
     */
    @TableId(value = "ENV_LIST_ID", type = IdType.AUTO)
    private Integer envListId;

    /**
     * 环境Id
     */
    @TableField("ENV_ID")
    private Integer envId;

    /**
     * 系统ID
     */
    @TableField("SYSTEM_ID")
    private String systemId;

    /**
     * 服务名称
     */
    @TableField("SERVER_NAME")
    private String serverName;

    /**
     * 主机地址
     */
    @TableField("IP_ADDRESS")
    private String ipAddress;

    /**
     * 端口信息
     */
    @TableField("PORT_INFO")
    private String portInfo;

    /**
     * 链接地址
     */
    @TableField("LINK_ADDRESS")
    private String linkAddress;

    /**
     * 配置人员
     */
    @TableField("CONFIGURATION_PEOPLEIDS")
    private String configurationPeopleIds;

    /**
     * 备注
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 环境名称（用于关联查询，不在数据库表中）
     */
    @TableField(exist = false)
    private String envName;

    /**
     * 系统名称（用于关联查询，不在数据库表中）
     */
    @TableField(exist = false)
    private String systemName;

    /**
     * 无参构造函数
     */
    public TfEnvironmentList() {
    }

    /**
     * 全参构造函数
     * @param envListId 环境清单Id
     * @param envId 环境Id
     * @param systemId 系统ID
     * @param serverName 服务名称
     * @param ipAddress 主机地址
     * @param portInfo 端口信息
     * @param linkAddress 链接地址
     * @param configurationPeopleIds 配置人员
     * @param remark 备注
     */
    public TfEnvironmentList(Integer envListId, Integer envId, String systemId, String serverName,
                              String ipAddress, String portInfo, String linkAddress,
                              String configurationPeopleIds, String remark) {
        this.envListId = envListId;
        this.envId = envId;
        this.systemId = systemId;
        this.serverName = serverName;
        this.ipAddress = ipAddress;
        this.portInfo = portInfo;
        this.linkAddress = linkAddress;
        this.configurationPeopleIds = configurationPeopleIds;
        this.remark = remark;
    }

    // Getter和Setter方法
    /**
     * 获取环境清单Id
     * @return 环境清单Id
     */
    public Integer getEnvListId() {
        return envListId;
    }

    /**
     * 设置环境清单Id
     * @param envListId 环境清单Id
     */
    public void setEnvListId(Integer envListId) {
        this.envListId = envListId;
    }

    /**
     * 获取环境Id
     * @return 环境Id
     */
    public Integer getEnvId() {
        return envId;
    }

    /**
     * 设置环境Id
     * @param envId 环境Id
     */
    public void setEnvId(Integer envId) {
        this.envId = envId;
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
     * 获取服务名称
     * @return 服务名称
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * 设置服务名称
     * @param serverName 服务名称
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * 获取主机地址
     * @return 主机地址
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * 设置主机地址
     * @param ipAddress 主机地址
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * 获取端口信息
     * @return 端口信息
     */
    public String getPortInfo() {
        return portInfo;
    }

    /**
     * 设置端口信息
     * @param portInfo 端口信息
     */
    public void setPortInfo(String portInfo) {
        this.portInfo = portInfo;
    }

    /**
     * 获取链接地址
     * @return 链接地址
     */
    public String getLinkAddress() {
        return linkAddress;
    }

    /**
     * 设置链接地址
     * @param linkAddress 链接地址
     */
    public void setLinkAddress(String linkAddress) {
        this.linkAddress = linkAddress;
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
     * 获取备注
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取环境名称
     * @return 环境名称
     */
    public String getEnvName() {
        return envName;
    }

    /**
     * 设置环境名称
     * @param envName 环境名称
     */
    public void setEnvName(String envName) {
        this.envName = envName;
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
