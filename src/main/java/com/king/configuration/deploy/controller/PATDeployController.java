package com.king.configuration.deploy.controller;

import com.king.common.Result;
import com.king.configuration.deploy.entity.TfDeployRecord;
import com.king.configuration.deploy.service.IPATDeployService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

/**
 * PAT部署Controller
 */
@RestController
@RequestMapping("/configuration/deploy/pat")
public class PATDeployController {

    @Autowired
    @Qualifier("patDeployServiceImpl")
    private IPATDeployService patDeployService;

    /**
     * 创建发版登记（PAT）
     * @param deployRecord 发版登记信息
     * @return 创建结果，包含生成的版本号（versionCode）
     */
    @PostMapping("/record")
    public Result<String> createPATDeployRecord(@RequestBody TfDeployRecord deployRecord) {
        try {
            Result<String> serviceResult = patDeployService.createPATDeployRecord(deployRecord);
            // Service 已经返回了 Result，直接返回
            return serviceResult;
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("创建PAT发版登记失败: " + e.getMessage());
        }
    }

}
