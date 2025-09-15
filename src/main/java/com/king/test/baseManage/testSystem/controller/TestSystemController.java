package com.king.test.baseManage.testSystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.king.common.Result;
import com.king.test.baseManage.testSystem.entity.TTestSystem;
import com.king.test.baseManage.testSystem.service.ITestSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 测试系统Controller
 */
@RestController
@RequestMapping("/test/system")
public class TestSystemController {
    
    @Autowired
    @Qualifier("testSystemServiceImpl")
    private ITestSystemService testSystemService;
    
    /**
     * 获取测试系统列表
     * @param systemName 系统名称（模糊查询）
     * @param orgId 机构ID
     * @param systemType 系统类型
     * @param systemStage 系统阶段
     * @param testManagerName 测试经理名称（模糊查询）
     * @param devManagerName 开发经理名称（模糊查询）
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 测试系统列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getTestSystemList(@RequestParam(value = "systemName", required = false) String systemName,
                                                         @RequestParam(value = "orgId", required = false) String orgId,
                                                         @RequestParam(value = "systemType", required = false) String systemType,
                                                         @RequestParam(value = "systemStage", required = false) String systemStage,
                                                         @RequestParam(value = "testManagerName", required = false) String testManagerName,
                                                         @RequestParam(value = "devManagerName", required = false) String devManagerName,
                                                         @RequestParam("pageNo") Long pageNo,
                                                         @RequestParam("pageSize") Long pageSize) {
        try {
            Page<TTestSystem> page = new Page<>(pageNo, pageSize);
            IPage<TTestSystem> result = testSystemService.getTestSystemList(page, systemName, orgId, systemType, systemStage, testManagerName, devManagerName);
            
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("total", result.getTotal());
            data.put("rows", result.getRecords());
            
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取测试系统列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取测试系统详情
     * @param systemId 系统ID
     * @return 测试系统详情
     */
    @GetMapping("/{systemId}")
    public Result<TTestSystem> getTestSystemDetail(@PathVariable("systemId") String systemId) {
        try {
            TTestSystem testSystem = testSystemService.getTestSystemDetail(systemId);
            if (testSystem == null) {
                return Result.error("测试系统不存在");
            }
            return Result.success(testSystem);
        } catch (Exception e) {
            return Result.error("获取测试系统详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建测试系统
     * @param testSystem 测试系统信息
     * @return 创建结果
     */
    @PostMapping
    public Result<?> createTestSystem(@RequestBody TTestSystem testSystem) {
        try {
            boolean success = testSystemService.createTestSystem(testSystem);
            return success ? Result.success("创建成功") : Result.error("创建失败");
        } catch (Exception e) {
            return Result.error("创建测试系统失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新测试系统
     * @param testSystem 测试系统信息
     * @return 更新结果
     */
    @PutMapping
    public Result<?> updateTestSystem(@RequestBody TTestSystem testSystem) {
        try {
            boolean success = testSystemService.updateTestSystem(testSystem);
            return success ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("更新测试系统失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除测试系统
     * @param systemId 系统ID
     * @return 删除结果
     */
    @DeleteMapping("/{systemId}")
    public Result<?> deleteTestSystem(@PathVariable("systemId") String systemId) {
        try {
            boolean success = testSystemService.deleteTestSystem(systemId);
            return success ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error("删除测试系统失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量删除测试系统
     * @param requestData 请求数据，包含systemIds列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public Result<?> batchDeleteTestSystem(@RequestBody Map<String, Object> requestData) {
        try {
            @SuppressWarnings("unchecked")
            List<String> systemIds = (List<String>) requestData.get("systemIds");
            if (systemIds == null || systemIds.isEmpty()) {
                return Result.error("系统ID列表不能为空");
            }
            
            boolean success = testSystemService.batchDeleteTestSystem(systemIds);
            return success ? Result.success("批量删除成功") : Result.error("批量删除失败");
        } catch (Exception e) {
            return Result.error("批量删除测试系统失败: " + e.getMessage());
        }
    }
}
