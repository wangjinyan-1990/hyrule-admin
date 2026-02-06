package com.king.framework.attachment.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 附件实体类
 * 对应数据库表：tf_attachment
 */
@Data
@TableName("tf_attachment")
public class TfAttachment {

    /**
     * 附件ID
     */
    @TableId(value = "ATTACHMENT_ID")
    private String attachmentId;

    /**
     * 原始文件名
     */
    @TableField("ORIGINAL_FILE_NAME")
    private String originalFileName;

    /**
     * 服务器文件名
     */
    @TableField("SERVER_FILE_NAME")
    private String serverFileName;

    /**
     * 附件大小
     */
    @TableField("ATTACHMENT_SIZE")
    private Integer attachmentSize;

    /**
     * 上传日期
     */
    @TableField("UPLOAD_DATE")
    private Date uploadDate;

    /**
     * 上传用户ID
     */
    @TableField("UPLOAD_USER_ID")
    private String uploadUserId;

    /**
     * 上传用户姓名（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String uploadUserName;

    /**
     * 上传路径
     */
    @TableField("UPLOAD_PATH")
    private String uploadPath;

    /**
     * 模块
     */
    @TableField("MODULE")
    private String module;

    /**
     * 关联ID
     */
    @TableField(value = "RELATE_ID", insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String relateId;

    // Getter和Setter方法
    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getServerFileName() {
        return serverFileName;
    }

    public void setServerFileName(String serverFileName) {
        this.serverFileName = serverFileName;
    }

    public Integer getAttachmentSize() {
        return attachmentSize;
    }

    public void setAttachmentSize(Integer attachmentSize) {
        this.attachmentSize = attachmentSize;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUploadUserId() {
        return uploadUserId;
    }

    public void setUploadUserId(String uploadUserId) {
        this.uploadUserId = uploadUserId;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getRelateId() {
        return relateId;
    }

    public void setRelateId(String relateId) {
        this.relateId = relateId;
    }
}
