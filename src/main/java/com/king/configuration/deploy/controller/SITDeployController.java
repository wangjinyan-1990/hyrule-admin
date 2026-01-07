package com.king.configuration.deploy.controller;

import com.king.common.Result;
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
}
