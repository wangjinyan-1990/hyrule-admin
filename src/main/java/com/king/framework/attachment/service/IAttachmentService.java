package com.king.framework.attachment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.framework.attachment.entity.TfAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 附件Service接口
 */
public interface IAttachmentService extends IService<TfAttachment> {
    
    /**
     * 上传附件
     * @param file 上传的文件
     * @param module 模块名称
     * @param relateId 关联ID
     * @return 附件信息
     */
    TfAttachment uploadAttachment(MultipartFile file, String module, String relateId);
    
    /**
     * 根据模块和关联ID查询附件列表（组合查询）
     * @param module 模块名称（可选）
     * @param relateId 关联ID（可选）
     * @return 附件列表
     */
    List<TfAttachment> getAttachments(String module, String relateId);
    
    /**
     * 根据关联ID查询附件列表
     * @param relateId 关联ID
     * @return 附件列表
     */
    List<TfAttachment> getAttachmentsByRelateId(String relateId);
    

    /**
     * 删除附件
     * @param attachmentId 附件ID
     * @return 是否删除成功
     */
    boolean deleteAttachment(String attachmentId);
    
    /**
     * 批量删除附件
     * @param attachmentIds 附件ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteAttachments(List<String> attachmentIds);
    
    /**
     * 查看文本文件内容
     * @param attachmentId 附件ID
     * @return 文件内容和元数据
     */
    Map<String, Object> viewTextFile(String attachmentId);
}
