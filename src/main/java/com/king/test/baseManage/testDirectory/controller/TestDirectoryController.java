package com.king.test.baseManage.testDirectory.controller;

import com.king.common.Result;
import com.king.test.baseManage.testDirectory.entity.TTestDirectory;
import com.king.test.baseManage.testDirectory.service.ITestDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 测试系统Controller
 */
@RestController
@RequestMapping("/test/directory")
public class TestDirectoryController {

    @Autowired
    @Qualifier("testDirectoryServiceImpl")
    private ITestDirectoryService testDirectoryService;

    /**
     * 根据用户ID获取该用户所参与的系统，作为测试目录树的根目录展示
     * 每个系统只出现一次
     * @param userId 用户ID
     * @return 系统信息列表
     */
    @GetMapping("/getSystemsByUserId")
    public Result<Map<String, Object>> getSystemsByUserId(@RequestParam("userId") String userId) {
        if (!StringUtils.hasText(userId)) {
            return Result.error("用户ID不能为空");
        }

        try {
            Map<String, Object> data = testDirectoryService.getSystemsByUserId(userId);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("查询用户参与的系统列表失败：" + e.getMessage());
        }
    }

    /**
     * 创建测试目录
     * @param directory 目录信息
     * @return 创建结果
     */
    @PostMapping("/create")
    public Result<?> createDirectory(@RequestBody TTestDirectory directory) {
        if (directory == null) {
            return Result.error("目录信息不能为空");
        }
        
        if (!StringUtils.hasText(directory.getDirectoryName())) {
            return Result.error("目录名称不能为空");
        }
        
        if (!StringUtils.hasText(directory.getSystemId())) {
            return Result.error("系统ID不能为空");
        }
        
        try {
            boolean success = testDirectoryService.createDirectory(directory);
            if (success) {
                return Result.success("目录创建成功");
            } else {
                return Result.error("目录创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建目录失败：" + e.getMessage());
        }
    }

    /**
     * 根据父目录ID和系统ID查询子目录
     * @param directoryParentId 父目录ID（可为空）
     * @param systemId 系统ID
     * @return 子目录列表
     */
    @GetMapping("/getChildrenByParentId")
    public Result<Map<String, Object>> getChildrenByParentId(@RequestParam(value = "directoryParentId", required = false) String directoryParentId,
                                                           @RequestParam("systemId") String systemId) {
        if (!StringUtils.hasText(systemId)) {
            return Result.error("系统ID不能为空");
        }
        
        try {
            Map<String, Object> data = testDirectoryService.getChildrenByParentId(directoryParentId, systemId);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("查询子目录失败：" + e.getMessage());
        }
    }

    /**
     * 更新测试目录
     * @param directory 目录信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public Result<?> updateDirectory(@RequestBody TTestDirectory directory) {
        if (directory == null) {
            return Result.error("目录信息不能为空");
        }
        
        if (!StringUtils.hasText(directory.getDirectoryId())) {
            return Result.error("目录ID不能为空");
        }
        
        if (!StringUtils.hasText(directory.getDirectoryName())) {
            return Result.error("目录名称不能为空");
        }
        
        if (!StringUtils.hasText(directory.getSystemId())) {
            return Result.error("系统ID不能为空");
        }
        
        try {
            boolean success = testDirectoryService.updateDirectory(directory);
            if (success) {
                return Result.success("目录更新成功");
            } else {
                return Result.error("目录更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新目录失败：" + e.getMessage());
        }
    }

}
