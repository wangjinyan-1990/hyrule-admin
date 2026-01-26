package com.king.configuration.deploy.controller;

import com.king.common.Result;
import com.king.configuration.deploy.entity.TfDeployRecord;
import com.king.configuration.deploy.service.IDeployRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 发版登记Controller
 */
@RestController
@RequestMapping("/configuration/deploy/record")
public class DeployRecordController {
    
    @Autowired
    @Qualifier("deployRecordServiceImpl")
    private IDeployRecordService deployRecordService;
    
    /**
     * 获取发版登记列表
     * @param pageNo 页码（可选，默认1）
     * @param pageSize 每页大小（可选，默认10）
     * @param sendTestInfo 送测单信息（可选，模糊查询）
     * @param systemName 系统名称（可选，模糊查询）
     * @param testStage 测试阶段（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 发版登记列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getDeployRecordList(
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sendTestInfo", required = false) String sendTestInfo,
            @RequestParam(value = "systemName", required = false) String systemName,
            @RequestParam(value = "testStage", required = false) String testStage,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        try {
            Map<String, Object> data = deployRecordService.getDeployRecordList(pageNo, pageSize, sendTestInfo, systemName, testStage, startDate, endDate);
            return Result.success(data);
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("获取发版登记列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取发版登记详情
     * @param deployId 部署ID
     * @return 发版登记详情
     */
    @GetMapping("/{deployId}")
    public Result<TfDeployRecord> getDeployRecordDetail(@PathVariable("deployId") Integer deployId) {
        try {
            TfDeployRecord record = deployRecordService.getDeployRecordDetail(deployId);
            return Result.success(record);
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("获取发版登记详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新发版登记
     * @param deployRecord 发版登记信息
     * @return 更新结果
     */
    @PutMapping
    public Result<?> updateDeployRecord(@RequestBody TfDeployRecord deployRecord) {
        try {
            deployRecordService.updateDeployRecord(deployRecord);
            return Result.success("发版登记更新成功");
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("更新发版登记失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除发版登记
     * @param deployId 部署ID
     * @return 删除结果
     */
    @DeleteMapping("/{deployId}")
    public Result<?> deleteDeployRecord(@PathVariable("deployId") Integer deployId) {
        try {
            deployRecordService.deleteDeployRecord(deployId);
            return Result.success("发版登记删除成功");
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("删除发版登记失败: " + e.getMessage());
        }
    }
}

