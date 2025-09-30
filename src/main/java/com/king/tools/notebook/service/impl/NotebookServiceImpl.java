package com.king.tools.notebook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.tools.notebook.entity.TfNotebook;
import com.king.tools.notebook.mapper.NotebookMapper;
import com.king.tools.notebook.service.INotebookService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 记事本Service实现类
 */
@Service("notebookServiceImpl")
public class NotebookServiceImpl extends ServiceImpl<NotebookMapper, TfNotebook> implements INotebookService {
    
    @Override
    public String createNotebook(String noteTitle, String noteContent, String directoryId, String userId, Integer fileSize) {
        if (!StringUtils.hasText(noteTitle) || !StringUtils.hasText(userId)) {
            return null;
        }
        
        try {
            TfNotebook notebook = new TfNotebook();
            String noteId = UUID.randomUUID().toString().replace("-", "");
            notebook.setNoteId(noteId);
            notebook.setNoteTitle(noteTitle);
            notebook.setNoteContent(StringUtils.hasText(noteContent) ? noteContent : "");
            notebook.setDirectoryId(StringUtils.hasText(directoryId) ? directoryId : null);
            notebook.setUserId(userId);
            notebook.setCreateTime(LocalDateTime.now());
            notebook.setFileSize(fileSize != null ? fileSize : 0);
            
            boolean success = this.save(notebook);
            return success ? noteId : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public boolean updateNotebook(String noteId, String noteTitle, String noteContent) {
        if (!StringUtils.hasText(noteId) || !StringUtils.hasText(noteTitle)) {
            return false;
        }
        
        try {
            TfNotebook notebook = this.getById(noteId);
            if (notebook == null) {
                return false;
            }
            
            notebook.setNoteTitle(noteTitle);
            notebook.setNoteContent(StringUtils.hasText(noteContent) ? noteContent : "");
            
            return this.updateById(notebook);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean deleteNotebook(String noteId) {
        if (!StringUtils.hasText(noteId)) {
            return false;
        }
        
        try {
            return this.removeById(noteId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean batchDeleteNotebooks(List<String> noteIds) {
        if (noteIds == null || noteIds.isEmpty()) {
            return false;
        }
        
        try {
            return this.removeByIds(noteIds);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<TfNotebook> getNotebooksByDirectory(String directoryId, String userId) {
        if (!StringUtils.hasText(userId)) {
            return null;
        }
        
        try {
            LambdaQueryWrapper<TfNotebook> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TfNotebook::getUserId, userId);
            
            if (StringUtils.hasText(directoryId)) {
                wrapper.eq(TfNotebook::getDirectoryId, directoryId);
            } else {
                wrapper.isNull(TfNotebook::getDirectoryId);
            }
            
            wrapper.orderByDesc(TfNotebook::getCreateTime);
            
            return this.list(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public TfNotebook getNotebookById(String noteId) {
        if (!StringUtils.hasText(noteId)) {
            return null;
        }
        
        try {
            return this.getById(noteId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
