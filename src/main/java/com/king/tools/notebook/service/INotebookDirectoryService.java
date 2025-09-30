package com.king.tools.notebook.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.tools.notebook.entity.TfNotebookDirectory;

import java.util.List;

/**
 * 记事本目录服务接口
 */
public interface INotebookDirectoryService extends IService<TfNotebookDirectory> {
    
    /**
     * 创建记事本目录
     * @param directoryName 目录名称
     * @param directoryParentId 父目录ID
     * @param userId 用户ID
     * @return 创建结果
     */
    boolean createDirectory(String directoryName, String directoryParentId, String userId);
    
    /**
     * 获取记事本目录列表
     * @param userId 用户ID
     * @param parentId 父目录ID（可选，不传则查询根目录）
     * @return 目录列表
     */
    List<TfNotebookDirectory> getDirectoryList(String userId, String parentId);
    
    /**
     * 重命名记事本目录
     * @param directoryId 目录ID
     * @param directoryName 新目录名称
     * @return 重命名结果
     */
    boolean renameDirectory(String directoryId, String directoryName);
    
    /**
     * 删除记事本目录
     * @param directoryId 目录ID
     * @return 删除结果
     */
    boolean deleteDirectory(String directoryId);
    
    /**
     * 批量删除记事本目录
     * @param directoryIds 目录ID数组
     * @return 删除结果
     */
    boolean batchDeleteDirectory(List<String> directoryIds);
}