package com.king.tools.notebook.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 记事本实体类
 * 对应数据库表：tf_notebook
 */
@Data
@TableName("tf_notebook")
public class TfNotebook {
    
    /**
     * 记事本ID
     */
    @TableId("NOTE_ID")
    private String noteId;
    
    /**
     * 目录ID
     */
    @TableField("DIRECTORY_ID")
    private String directoryId;
    
    /**
     * 记事本标题
     */
    @TableField("NOTE_TITLE")
    private String noteTitle;
    
    /**
     * 记事本内容
     */
    @TableField("NOTE_CONTENT")
    private String noteContent;
    
    /**
     * 用户ID
     */
    @TableField("USER_ID")
    private String userId;
    
    /**
     * 创建时间
     */
    @TableField("CREATE_TIME")
    private LocalDateTime createTime;
    
    /**
     * 文件大小（字节）
     */
    @TableField("FILESIZE")
    private Integer fileSize;
    
    /**
     * 无参构造函数
     */
    public TfNotebook() {
    }
    
    /**
     * 全参构造函数
     * @param noteId 记事本ID
     * @param directoryId 目录ID
     * @param noteTitle 记事本标题
     * @param noteContent 记事本内容
     * @param userId 用户ID
     * @param createTime 创建时间
     * @param fileSize 文件大小
     */
    public TfNotebook(String noteId, String directoryId, String noteTitle, String noteContent, String userId, LocalDateTime createTime, Integer fileSize) {
        this.noteId = noteId;
        this.directoryId = directoryId;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.userId = userId;
        this.createTime = createTime;
        this.fileSize = fileSize;
    }
    
    /**
     * 获取记事本ID
     * @return 记事本ID
     */
    public String getNoteId() {
        return noteId;
    }
    
    /**
     * 设置记事本ID
     * @param noteId 记事本ID
     */
    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }
    
    /**
     * 获取目录ID
     * @return 目录ID
     */
    public String getDirectoryId() {
        return directoryId;
    }
    
    /**
     * 设置目录ID
     * @param directoryId 目录ID
     */
    public void setDirectoryId(String directoryId) {
        this.directoryId = directoryId;
    }
    
    /**
     * 获取记事本标题
     * @return 记事本标题
     */
    public String getNoteTitle() {
        return noteTitle;
    }
    
    /**
     * 设置记事本标题
     * @param noteTitle 记事本标题
     */
    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }
    
    /**
     * 获取记事本内容
     * @return 记事本内容
     */
    public String getNoteContent() {
        return noteContent;
    }
    
    /**
     * 设置记事本内容
     * @param noteContent 记事本内容
     */
    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }
    
    /**
     * 获取用户ID
     * @return 用户ID
     */
    public String getUserId() {
        return userId;
    }
    
    /**
     * 设置用户ID
     * @param userId 用户ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    /**
     * 获取创建时间
     * @return 创建时间
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    /**
     * 设置创建时间
     * @param createTime 创建时间
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    /**
     * 获取文件大小
     * @return 文件大小（字节）
     */
    public Integer getFileSize() {
        return fileSize;
    }
    
    /**
     * 设置文件大小
     * @param fileSize 文件大小（字节）
     */
    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }
}