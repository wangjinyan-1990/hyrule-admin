package com.king.tools.networkPolicyList.dto;

/**
 * 网络策略清单DTO
 * 用于存储网络策略清单的各个字段信息
 */
public class NetworkListDto {

    /**
     * 序号
     */
    private Integer serialNumber;

    /**
     * A端系统名称
     */
    private String aSideSystemName;

    /**
     * A端行内属性
     */
    private String aSideInRowAttribute;

    /**
     * A端IP地址（可能包含多个IP，用换行符分隔）
     */
    private String aSideIpAddress;

    /**
     * A端端口号
     */
    private String aSidePortNumber;

    /**
     * 访问方向（箭头符号，如：→）
     */
    private String accessDirection;

    /**
     * B端系统名称
     */
    private String bSideSystemName;

    /**
     * B端行内属性
     */
    private String bSideInRowAttribute;

    /**
     * B端IP地址（可能包含多个IP，用换行符分隔）
     */
    private String bSideIpAddress;

    /**
     * B端端口号
     */
    private String bSidePortNumber;

    /**
     * 端口类型（如：TCP、UDP等）
     */
    private String portType;

    /**
     * 是否为长连接（是/否）
     */
    private String isLongConnection;

    /**
     * 业务描述
     */
    private String businessDescription;

    public NetworkListDto() {
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getASideSystemName() {
        return aSideSystemName;
    }

    public void setASideSystemName(String aSideSystemName) {
        this.aSideSystemName = aSideSystemName;
    }

    public String getASideInRowAttribute() {
        return aSideInRowAttribute;
    }

    public void setASideInRowAttribute(String aSideInRowAttribute) {
        this.aSideInRowAttribute = aSideInRowAttribute;
    }

    public String getASideIpAddress() {
        return aSideIpAddress;
    }

    public void setASideIpAddress(String aSideIpAddress) {
        this.aSideIpAddress = aSideIpAddress;
    }

    public String getASidePortNumber() {
        return aSidePortNumber;
    }

    public void setASidePortNumber(String aSidePortNumber) {
        this.aSidePortNumber = aSidePortNumber;
    }

    public String getAccessDirection() {
        return accessDirection;
    }

    public void setAccessDirection(String accessDirection) {
        this.accessDirection = accessDirection;
    }

    public String getBSideSystemName() {
        return bSideSystemName;
    }

    public void setBSideSystemName(String bSideSystemName) {
        this.bSideSystemName = bSideSystemName;
    }

    public String getBSideInRowAttribute() {
        return bSideInRowAttribute;
    }

    public void setBSideInRowAttribute(String bSideInRowAttribute) {
        this.bSideInRowAttribute = bSideInRowAttribute;
    }

    public String getBSideIpAddress() {
        return bSideIpAddress;
    }

    public void setBSideIpAddress(String bSideIpAddress) {
        this.bSideIpAddress = bSideIpAddress;
    }

    public String getBSidePortNumber() {
        return bSidePortNumber;
    }

    public void setBSidePortNumber(String bSidePortNumber) {
        this.bSidePortNumber = bSidePortNumber;
    }

    public String getPortType() {
        return portType;
    }

    public void setPortType(String portType) {
        this.portType = portType;
    }

    public String getIsLongConnection() {
        return isLongConnection;
    }

    public void setIsLongConnection(String isLongConnection) {
        this.isLongConnection = isLongConnection;
    }

    public String getBusinessDescription() {
        return businessDescription;
    }

    public void setBusinessDescription(String businessDescription) {
        this.businessDescription = businessDescription;
    }

    @Override
    public String toString() {
        return "NetworkListDto{" +
                "serialNumber=" + serialNumber +
                ", aSideSystemName='" + aSideSystemName + '\'' +
                ", aSideInRowAttribute='" + aSideInRowAttribute + '\'' +
                ", aSideIpAddress='" + aSideIpAddress + '\'' +
                ", aSidePortNumber='" + aSidePortNumber + '\'' +
                ", accessDirection='" + accessDirection + '\'' +
                ", bSideSystemName='" + bSideSystemName + '\'' +
                ", bSideInRowAttribute='" + bSideInRowAttribute + '\'' +
                ", bSideIpAddress='" + bSideIpAddress + '\'' +
                ", bSidePortNumber='" + bSidePortNumber + '\'' +
                ", portType='" + portType + '\'' +
                ", isLongConnection='" + isLongConnection + '\'' +
                ", businessDescription='" + businessDescription + '\'' +
                '}';
    }
}
