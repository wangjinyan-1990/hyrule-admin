package com.king.tools.notebook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.tools.notebook.entity.TfNotebookDirectory;
import com.king.tools.notebook.mapper.NotebookDirectoryMapper;
import com.king.tools.notebook.service.INotebookDirectoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 记事本目录服务实现类
 */
@Service("notebookDirectoryServiceImpl")
public class NotebookDirectoryServiceImpl extends ServiceImpl<NotebookDirectoryMapper, TfNotebookDirectory> 
        implements INotebookDirectoryService {

    @Override
    public boolean createDirectory(String directoryName, String directoryParentId, String userId) {
        if (!StringUtils.hasText(directoryName) || !StringUtils.hasText(userId)) {
            return false;
        }
        
        try {
            // 检查是否存在重名目录
            if (checkDuplicateDirectoryName(userId, directoryName, directoryParentId, null)) {
                return false; // 存在重名目录
            }
            
            TfNotebookDirectory directory = new TfNotebookDirectory();
            directory.setDirectoryId(UUID.randomUUID().toString().replace("-", ""));
            directory.setDirectoryName(directoryName);
            directory.setDirectoryParentId(StringUtils.hasText(directoryParentId) ? directoryParentId : null);
            directory.setUserId(userId);
            directory.setIsLeafDirectory("1"); // 新创建的目录默认为叶子目录
            directory.setCreateTime(LocalDateTime.now());
            
            // 保存新目录
            boolean saveResult = this.save(directory);
            
            // 如果父目录存在，需要将父目录的isLeafDirectory设为0（非叶子目录）
            if (saveResult && StringUtils.hasText(directoryParentId)) {
                TfNotebookDirectory parentDirectory = this.getById(directoryParentId);
                if (parentDirectory != null && "1".equals(parentDirectory.getIsLeafDirectory())) {
                    parentDirectory.setIsLeafDirectory("0");
                    this.updateById(parentDirectory);
                }
            }
            
            return saveResult;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<TfNotebookDirectory> getDirectoryList(String userId, String parentId) {
        if (!StringUtils.hasText(userId)) {
            return null;
        }
        
        try {
            LambdaQueryWrapper<TfNotebookDirectory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TfNotebookDirectory::getUserId, userId);
            
            // 如果parentId为空或null，查询根目录（parentId为null的记录）
            if (StringUtils.hasText(parentId)) {
                queryWrapper.eq(TfNotebookDirectory::getDirectoryParentId, parentId);
            } else {
                queryWrapper.isNull(TfNotebookDirectory::getDirectoryParentId);
            }
            
            // 按创建时间倒序排列
            queryWrapper.orderByDesc(TfNotebookDirectory::getCreateTime);
            
            return this.list(queryWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean renameDirectory(String directoryId, String directoryName) {
        if (!StringUtils.hasText(directoryId) || !StringUtils.hasText(directoryName)) {
            return false;
        }
        
        try {
            TfNotebookDirectory directory = this.getById(directoryId);
            if (directory == null) {
                return false;
            }
            
            // 检查是否存在重名目录（排除当前目录）
            if (checkDuplicateDirectoryName(directory.getUserId(), directoryName, directory.getDirectoryParentId(), directoryId)) {
                return false; // 存在重名目录
            }
            
            directory.setDirectoryName(directoryName);
            return this.updateById(directory);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDirectory(String directoryId) {
        if (!StringUtils.hasText(directoryId)) {
            return false;
        }
        
        try {
            TfNotebookDirectory directory = this.getById(directoryId);
            if (directory == null) {
                return false;
            }
            
            // 检查是否有子目录
            LambdaQueryWrapper<TfNotebookDirectory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TfNotebookDirectory::getDirectoryParentId, directoryId);
            long childCount = this.count(queryWrapper);
            
            if (childCount > 0) {
                // 如果有子目录，不能删除
                return false;
            }
            
            // 删除目录
            boolean deleteResult = this.removeById(directoryId);
            
            // 如果删除成功，检查父目录是否还有其他子目录
            if (deleteResult && StringUtils.hasText(directory.getDirectoryParentId())) {
                LambdaQueryWrapper<TfNotebookDirectory> parentQueryWrapper = new LambdaQueryWrapper<>();
                parentQueryWrapper.eq(TfNotebookDirectory::getDirectoryParentId, directory.getDirectoryParentId());
                long remainingChildCount = this.count(parentQueryWrapper);
                
                // 如果父目录没有其他子目录了，将父目录设为叶子目录
                if (remainingChildCount == 0) {
                    TfNotebookDirectory parentDirectory = this.getById(directory.getDirectoryParentId());
                    if (parentDirectory != null) {
                        parentDirectory.setIsLeafDirectory("1");
                        this.updateById(parentDirectory);
                    }
                }
            }
            
            return deleteResult;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean batchDeleteDirectory(List<String> directoryIds) {
        if (directoryIds == null || directoryIds.isEmpty()) {
            return false;
        }
        
        try {
            // 检查所有目录是否都没有子目录
            for (String directoryId : directoryIds) {
                LambdaQueryWrapper<TfNotebookDirectory> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(TfNotebookDirectory::getDirectoryParentId, directoryId);
                long childCount = this.count(queryWrapper);
                
                if (childCount > 0) {
                    // 如果有子目录，不能删除
                    return false;
                }
            }
            
            // 批量删除
            boolean deleteResult = this.removeByIds(directoryIds);
            
            // 如果删除成功，需要更新相关父目录的叶子状态
            if (deleteResult) {
                // 获取所有被删除目录的父目录ID
                List<TfNotebookDirectory> deletedDirectories = this.listByIds(directoryIds);
                for (TfNotebookDirectory deletedDir : deletedDirectories) {
                    if (StringUtils.hasText(deletedDir.getDirectoryParentId())) {
                        LambdaQueryWrapper<TfNotebookDirectory> parentQueryWrapper = new LambdaQueryWrapper<>();
                        parentQueryWrapper.eq(TfNotebookDirectory::getDirectoryParentId, deletedDir.getDirectoryParentId());
                        long remainingChildCount = this.count(parentQueryWrapper);
                        
                        // 如果父目录没有其他子目录了，将父目录设为叶子目录
                        if (remainingChildCount == 0) {
                            TfNotebookDirectory parentDirectory = this.getById(deletedDir.getDirectoryParentId());
                            if (parentDirectory != null) {
                                parentDirectory.setIsLeafDirectory("1");
                                this.updateById(parentDirectory);
                            }
                        }
                    }
                }
            }
            
            return deleteResult;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 检查同一用户在同一个父目录下是否已存在同名目录
     * @param userId 用户ID
     * @param directoryName 目录名称
     * @param directoryParentId 父目录ID（可为null表示根目录）
     * @param excludeDirectoryId 排除的目录ID（重命名时使用，创建时传null）
     * @return true表示存在重名，false表示不存在重名
     */
    private boolean checkDuplicateDirectoryName(String userId, String directoryName, String directoryParentId, String excludeDirectoryId) {
        try {
            LambdaQueryWrapper<TfNotebookDirectory> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(TfNotebookDirectory::getUserId, userId)
                       .eq(TfNotebookDirectory::getDirectoryName, directoryName);
            
            // 如果指定了排除的目录ID（重命名时使用），则排除该目录
            if (StringUtils.hasText(excludeDirectoryId)) {
                checkWrapper.ne(TfNotebookDirectory::getDirectoryId, excludeDirectoryId);
            }
            
            // 根据父目录ID设置查询条件
            if (StringUtils.hasText(directoryParentId)) {
                checkWrapper.eq(TfNotebookDirectory::getDirectoryParentId, directoryParentId);
            } else {
                checkWrapper.isNull(TfNotebookDirectory::getDirectoryParentId);
            }
            
            long existingCount = this.count(checkWrapper);
            return existingCount > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // 发生异常时返回true，阻止操作
        }
    }
}