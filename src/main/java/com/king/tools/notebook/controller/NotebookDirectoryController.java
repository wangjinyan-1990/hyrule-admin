package com.king.tools.notebook.controller;

import com.king.common.Result;
import com.king.tools.notebook.entity.TfNotebookDirectory;
import com.king.tools.notebook.service.INotebookDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 记事本目录控制器
 */
@RestController
@RequestMapping("/tools/notebook/directory")
public class NotebookDirectoryController {

    @Autowired
    @Qualifier("notebookDirectoryServiceImpl")
    private INotebookDirectoryService notebookDirectoryService;

    /**
     * 创建记事本目录
     * @param data 目录数据
     * @return 创建结果
     */
    @PostMapping("/create")
    public Result<?> createDirectory(@RequestBody Map<String, Object> data) {
        try {
            String directoryName = (String) data.get("directoryName");
            String directoryParentId = (String) data.get("directoryParentId");
            String userId = (String) data.get("userId");
            
            if (!StringUtils.hasText(directoryName)) {
                return Result.error("目录名称不能为空");
            }
            
            if (!StringUtils.hasText(userId)) {
                return Result.error("用户ID不能为空");
            }
            
            boolean success = notebookDirectoryService.createDirectory(directoryName, directoryParentId, userId);
            
            if (success) {
                return Result.success("目录创建成功");
            } else {
                return Result.error("目录创建失败，可能已存在同名目录");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建目录时发生错误: " + e.getMessage());
        }
    }

    /**
     * 获取记事本目录列表
     * @param userId 用户ID
     * @param parentId 父目录ID（可选，不传则查询根目录）
     * @return 目录列表
     */
    @GetMapping("/list")
    public Result<List<TfNotebookDirectory>> getDirectoryList(@RequestParam String userId, 
                                                              @RequestParam(required = false) String parentId) {
        try {
            if (!StringUtils.hasText(userId)) {
                return Result.error("用户ID不能为空");
            }
            
            List<TfNotebookDirectory> directoryList = notebookDirectoryService.getDirectoryList(userId, parentId);
            
            if (directoryList != null) {
                return Result.success(directoryList, "获取目录列表成功");
            } else {
                return Result.error("获取目录列表失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取目录列表时发生错误: " + e.getMessage());
        }
    }

    /**
     * 重命名记事本目录
     * @param data 重命名数据
     * @return 重命名结果
     */
    @PutMapping("/rename")
    public Result<?> renameDirectory(@RequestBody Map<String, Object> data) {
        try {
            String directoryId = (String) data.get("directoryId");
            String directoryName = (String) data.get("directoryName");
            
            if (!StringUtils.hasText(directoryId)) {
                return Result.error("目录ID不能为空");
            }
            
            if (!StringUtils.hasText(directoryName)) {
                return Result.error("目录名称不能为空");
            }
            
            boolean success = notebookDirectoryService.renameDirectory(directoryId, directoryName);
            
            if (success) {
                return Result.success("目录重命名成功");
            } else {
                return Result.error("目录重命名失败，可能已存在同名目录");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("重命名目录时发生错误: " + e.getMessage());
        }
    }

    /**
     * 删除记事本目录
     * @param directoryId 目录ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{directoryId}")
    public Result<?> deleteDirectory(@PathVariable String directoryId) {
        try {
            if (!StringUtils.hasText(directoryId)) {
                return Result.error("目录ID不能为空");
            }
            
            boolean success = notebookDirectoryService.deleteDirectory(directoryId);
            
            if (success) {
                return Result.success("目录删除成功");
            } else {
                return Result.error("目录删除失败，请确保目录下没有子目录");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除目录时发生错误: " + e.getMessage());
        }
    }

    /**
     * 批量删除记事本目录
     * @param data 删除数据
     * @return 删除结果
     */
    @DeleteMapping("/batchDelete")
    public Result<?> batchDeleteDirectory(@RequestBody Map<String, Object> data) {
        try {
            @SuppressWarnings("unchecked")
            List<String> directoryIds = (List<String>) data.get("directoryIds");
            
            if (directoryIds == null || directoryIds.isEmpty()) {
                return Result.error("目录ID列表不能为空");
            }
            
            boolean success = notebookDirectoryService.batchDeleteDirectory(directoryIds);
            
            if (success) {
                return Result.success("批量删除目录成功");
            } else {
                return Result.error("批量删除目录失败，请确保目录下没有子目录");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("批量删除目录时发生错误: " + e.getMessage());
        }
    }
}