package com.king.configuration.deploy.controller;

import com.king.common.Result;
import com.king.configuration.deploy.entity.TfDeployRecord;
import com.king.configuration.deploy.service.ISITDeployService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * SIT部署Controller
 */
@RestController
@RequestMapping("/configuration/deploy/sit")
public class SITDeployController {
    
    @Autowired
    @Qualifier("sitDeployServiceImpl")
    private ISITDeployService sitDeployService;
    
    /**
     * 解析Merge Request，获取代码清单和标题
     * @param mergeRequest Merge Request URL
     * @param systemId 系统ID
     * @return 包含codeList和sendTestCode的结果
     */
    @PostMapping("/parseMR")
    public Result<Map<String, String>> parseMergeRequest(@RequestBody Map<String, String> params) {
        try {
            String mergeRequest = params.get("mergeRequest");
            String systemId = params.get("systemId");
            
            if (mergeRequest == null || mergeRequest.trim().isEmpty()) {
                return Result.error(0, "Merge Request URL不能为空");
            }
            
            if (systemId == null || systemId.trim().isEmpty()) {
                return Result.error(0, "系统ID不能为空");
            }
            
            Map<String, String> result = sitDeployService.parseMergeRequest(mergeRequest, systemId);
            return Result.success(result);
            
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("解析Merge Request失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建发版登记（SIT）
     * @param deployRecord 发版登记信息
     * @return 创建结果
     */
    @PostMapping("/record")
    public Result<?> createSITDeployRecord(@RequestBody TfDeployRecord deployRecord) {
        try {
            sitDeployService.createSITDeployRecord(deployRecord);
            return Result.success("SIT发版登记创建成功");
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("创建SIT发版登记失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新发版登记（SIT）
     * @param deployRecord 发版登记信息
     * @return 更新结果
     */
    @PutMapping("/record")
    public Result<?> updateSITDeployRecord(@RequestBody TfDeployRecord deployRecord) {
        try {
            sitDeployService.updateSITDeployRecord(deployRecord);
            return Result.success("SIT发版登记更新成功");
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("更新SIT发版登记失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取发版登记详情（SIT）
     * @param deployId 部署ID
     * @return 发版登记详情
     */
    @GetMapping("/record/{deployId}")
    public Result<TfDeployRecord> getSITDeployRecordDetail(@PathVariable("deployId") Integer deployId) {
        try {
            TfDeployRecord record = sitDeployService.getSITDeployRecordDetail(deployId);
            return Result.success(record);
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("获取SIT发版登记详情失败: " + e.getMessage());
        }
    }
}
