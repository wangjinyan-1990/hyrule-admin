package com.king.tools.notebook.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 记事本目录实体类
 * 对应表: tf_notebook_directory
 */
@Data
@TableName("tf_notebook_directory")
public class TfNotebookDirectory {

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
     * 用户ID
     */
    @TableField("USER_ID")
    private String userId;

    /**
     * 是否为叶子目录:0-不是;1-是
     */
    @TableField("IS_LEAF_DIRECTORY")
    private String isLeafDirectory;

    /**
     * 创建时间
     */
    @TableField("CREATE_TIME")
    private LocalDateTime createTime;

    // 构造函数
    public TfNotebookDirectory() {
    }

    public TfNotebookDirectory(String directoryId, String directoryName, String directoryParentId, 
                               String userId, String isLeafDirectory, LocalDateTime createTime) {
        this.directoryId = directoryId;
        this.directoryName = directoryName;
        this.directoryParentId = directoryParentId;
        this.userId = userId;
        this.isLeafDirectory = isLeafDirectory;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIsLeafDirectory() {
        return isLeafDirectory;
    }

    public void setIsLeafDirectory(String isLeafDirectory) {
        this.isLeafDirectory = isLeafDirectory;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
