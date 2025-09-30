package com.king.tools.notebook.controller;

import com.king.common.Result;
import com.king.tools.notebook.entity.TfNotebook;
import com.king.tools.notebook.service.INotebookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 记事本Controller
 */
@RestController
@RequestMapping("/tools/notebook/note")
public class NotebookController {
    
    @Autowired
    @Qualifier("notebookServiceImpl")
    private INotebookService tfNotebookService;
    
    /**
     * 创建记事本
     * @param data 请求数据
     * @return 创建结果
     */
    @PostMapping("/create")
    public Result<?> createNotebook(@RequestBody Map<String, Object> data) {
        try {
            String noteTitle = (String) data.get("noteTitle");
            String noteContent = (String) data.get("noteContent");
            String directoryId = (String) data.get("directoryId");
            String userId = (String) data.get("userId");
            Integer fileSize = (Integer) data.get("fileSize");
            
            if (!StringUtils.hasText(noteTitle)) {
                return Result.error("记事本标题不能为空");
            }
            if (!StringUtils.hasText(userId)) {
                return Result.error("用户ID不能为空");
            }
            
            String noteId = tfNotebookService.createNotebook(noteTitle, noteContent, directoryId, userId, fileSize);
            if (noteId != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("noteId", noteId);
                return Result.success(result, "记事本创建成功");
            } else {
                return Result.error("记事本创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建记事本失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新记事本
     * @param data 请求数据
     * @return 更新结果
     */
    @PutMapping("/update")
    public Result<?> updateNotebook(@RequestBody Map<String, Object> data) {
        try {
            String noteId = (String) data.get("noteId");
            String noteTitle = (String) data.get("noteTitle");
            String noteContent = (String) data.get("noteContent");
            
            if (!StringUtils.hasText(noteId)) {
                return Result.error("记事本ID不能为空");
            }
            if (!StringUtils.hasText(noteTitle)) {
                return Result.error("记事本标题不能为空");
            }
            
            boolean success = tfNotebookService.updateNotebook(noteId, noteTitle, noteContent);
            if (success) {
                return Result.success("记事本更新成功");
            } else {
                return Result.error("记事本更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新记事本失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除记事本
     * @param noteId 记事本ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{noteId}")
    public Result<?> deleteNotebook(@PathVariable String noteId) {
        try {
            if (!StringUtils.hasText(noteId)) {
                return Result.error("记事本ID不能为空");
            }
            
            boolean success = tfNotebookService.deleteNotebook(noteId);
            if (success) {
                return Result.success("记事本删除成功");
            } else {
                return Result.error("记事本删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除记事本失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量删除记事本
     * @param data 请求数据
     * @return 删除结果
     */
    @DeleteMapping("/batchDelete")
    public Result<?> batchDeleteNotebooks(@RequestBody Map<String, Object> data) {
        try {
            @SuppressWarnings("unchecked")
            List<String> noteIds = (List<String>) data.get("noteIds");
            
            if (noteIds == null || noteIds.isEmpty()) {
                return Result.error("记事本ID列表不能为空");
            }
            
            boolean success = tfNotebookService.batchDeleteNotebooks(noteIds);
            if (success) {
                return Result.success("批量删除记事本成功");
            } else {
                return Result.error("批量删除记事本失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("批量删除记事本失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据目录ID获取记事本列表
     * @param params 查询参数
     * @return 记事本列表
     */
    @GetMapping("/list")
    public Result<?> getNotebooksByDirectory(@RequestParam Map<String, Object> params) {
        try {
            String directoryId = (String) params.get("directoryId");
            String userId = (String) params.get("userId");
            
            if (!StringUtils.hasText(userId)) {
                return Result.error("用户ID不能为空");
            }
            
            List<TfNotebook> notebooks = tfNotebookService.getNotebooksByDirectory(directoryId, userId);
            return Result.success(notebooks, "获取记事本列表成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取记事本列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据记事本ID获取记事本详情
     * @param noteId 记事本ID
     * @return 记事本详情
     */
    @GetMapping("/detail/{noteId}")
    public Result<?> getNotebookById(@PathVariable String noteId) {
        try {
            if (!StringUtils.hasText(noteId)) {
                return Result.error("记事本ID不能为空");
            }
            
            TfNotebook notebook = tfNotebookService.getNotebookById(noteId);
            if (notebook == null) {
                return Result.error("记事本不存在");
            }
            
            return Result.success(notebook, "获取记事本详情成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取记事本详情失败：" + e.getMessage());
        }
    }
}
