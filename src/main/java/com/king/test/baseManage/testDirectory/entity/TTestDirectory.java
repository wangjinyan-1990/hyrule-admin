package com.king.test.baseManage.testDirectory.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试目录实体类
 * 对应表: t_test_directory
 */
@Data
@TableName("t_test_directory")
public class TTestDirectory {

    /**
     * 目录ID
     */
    @TableId("DIRECTORY_ID")
    private String directoryId;

    /**
     * 目录名称
     */
    @TableField("DIRECTORY_NAME")
    private String directoryName;

    /**
     * 父目录ID
     */
    @TableField("DIRECTORY_PARENTID")
    private String directoryParentId;

    /**
     * 层级
     */
    @TableField("LEVEL")
    private Integer level;

    /**
     * 完整路径
     */
    @TableField("FULLPATH")
    private String fullPath;

    /**
     * 系统ID
     */
    @TableField("SYSTEM_ID")
    private String systemId;

    /**
     * 第一级路径
     */
    @TableField("FIRST_PATH")
    private String firstPath;

    /**
     * 第二级路径
     */
    @TableField("SECOND_PATH")
    private String secondPath;

    /**
     * 第三级路径
     */
    @TableField("THIRD_PATH")
    private String thirdPath;

    /**
     * 用例库是否使用:0-不使用;1-使用
     */
    @TableField("IS_USE_TESTCASE")
    private String isUseTestcase;

    /**
     * 执行库是否使用:0-不使用;1-使用
     */
    @TableField("IS_USE_TESTSET")
    private String isUseTestset;

    /**
     * 是否为叶子目录(没有子目录):0-不是;1-是;
     */
    @TableField("IS_LEAF_DIRECTORY")
    private String isLeafDirectory;


    /**
     * 目录类型
     */
    @TableField("DIRECTORY_TYPE")
    private String directoryType;

    /**
     * 关联ID
     */
    @TableField("RELATE_ID")
    private String relateId;

    /**
     * 创建时间
     */
    @TableField("CREATE_TIME")
    private LocalDateTime createTime;

    // 构造函数
    public TTestDirectory() {
    }

    public TTestDirectory(String directoryId, String directoryName, String directoryParentId, Integer level, String fullPath, String systemId, String firstPath, String secondPath, String thirdPath, String isUseTestcase, String isUseTestset, String isLeafDirectory, String directoryType, String relateId, LocalDateTime createTime) {
        this.directoryId = directoryId;
        this.directoryName = directoryName;
        this.directoryParentId = directoryParentId;
        this.level = level;
        this.fullPath = fullPath;
        this.systemId = systemId;
        this.firstPath = firstPath;
        this.secondPath = secondPath;
        this.thirdPath = thirdPath;
        this.isUseTestcase = isUseTestcase;
        this.isUseTestset = isUseTestset;
        this.isLeafDirectory = isLeafDirectory;
        this.directoryType = directoryType;
        this.relateId = relateId;
        this.createTime = createTime;
    }

    // Getter和Setter方法（Lombok的@Data注解会自动生成，这里提供手动版本作为备选）
    public String getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(String directoryId) {
        this.directoryId = directoryId;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDirectoryParentId() {
        return directoryParentId;
    }

    public void setDirectoryParentId(String directoryParentId) {
        this.directoryParentId = directoryParentId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getFirstPath() {
        return firstPath;
    }

    public void setFirstPath(String firstPath) {
        this.firstPath = firstPath;
    }

    public String getSecondPath() {
        return secondPath;
    }

    public void setSecondPath(String secondPath) {
        this.secondPath = secondPath;
    }

    public String getThirdPath() {
        return thirdPath;
    }

    public void setThirdPath(String thirdPath) {
        this.thirdPath = thirdPath;
    }

    public String getIsUseTestcase() {
        return isUseTestcase;
    }

    public void setIsUseTestcase(String isUseTestcase) {
        this.isUseTestcase = isUseTestcase;
    }

    public String getIsUseTestset() {
        return isUseTestset;
    }

    public void setIsUseTestset(String isUseTestset) {
        this.isUseTestset = isUseTestset;
    }

    public String getIsLeafDirectory() {
        return isLeafDirectory;
    }

    public void setIsLeafDirectory(String isLeafDirectory) {
        this.isLeafDirectory = isLeafDirectory;
    }

    public String getDirectoryType() {
        return directoryType;
    }

    public void setDirectoryType(String directoryType) {
        this.directoryType = directoryType;
    }

    public String getRelateId() {
        return relateId;
    }

    public void setRelateId(String relateId) {
        this.relateId = relateId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
