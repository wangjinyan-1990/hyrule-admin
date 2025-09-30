package com.king.tools.notebook.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.tools.notebook.entity.TfNotebook;

import java.util.List;

/**
 * 记事本Service接口
 */
public interface INotebookService extends IService<TfNotebook> {
    
    /**
     * 创建记事本
     * @param noteTitle 记事本标题
     * @param noteContent 记事本内容
     * @param directoryId 目录ID
     * @param userId 用户ID
     * @param fileSize 文件大小
     * @return 创建的记事本ID，失败返回null
     */
    String createNotebook(String noteTitle, String noteContent, String directoryId, String userId, Integer fileSize);
    
    /**
     * 更新记事本
     * @param noteId 记事本ID
     * @param noteTitle 记事本标题
     * @param noteContent 记事本内容
     * @return 更新结果
     */
    boolean updateNotebook(String noteId, String noteTitle, String noteContent);
    
    /**
     * 删除记事本
     * @param noteId 记事本ID
     * @return 删除结果
     */
    boolean deleteNotebook(String noteId);
    
    /**
     * 批量删除记事本
     * @param noteIds 记事本ID列表
     * @return 删除结果
     */
    boolean batchDeleteNotebooks(List<String> noteIds);
    
    /**
     * 根据目录ID获取记事本列表
     * @param directoryId 目录ID
     * @param userId 用户ID
     * @return 记事本列表
     */
    List<TfNotebook> getNotebooksByDirectory(String directoryId, String userId);
    
    /**
     * 根据记事本ID获取记事本详情
     * @param noteId 记事本ID
     * @return 记事本详情
     */
    TfNotebook getNotebookById(String noteId);
}
