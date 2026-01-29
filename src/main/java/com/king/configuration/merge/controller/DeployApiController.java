package com.king.configuration.merge.controller;

import com.king.common.Result;
import com.king.configuration.merge.dto.DeployRecordApiDTO;
import com.king.configuration.merge.service.IDeployApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

/**
 * 登记部署外部API Controller
 */
@RestController
@RequestMapping("/api/deploy")
public class DeployApiController {

    @Autowired
    @Qualifier("deployApiServiceImpl")
    private IDeployApiService deployApiService;

    /**
     * 创建发版登记（外部API接口）
     * @param dto 发版登记DTO
     * @return 创建结果，包含生成的版本号（versionCode）
     */
    @PostMapping("/record")
    public Result<String> createPATDeployRecordByApi(@RequestBody DeployRecordApiDTO dto) {
        try {
            String versionCode = deployApiService.createDeployRecordByApi(dto);
            return Result.success(versionCode, "发版登记创建成功");
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("API接口创建发版登记失败: " + e.getMessage());
        }
    }
}

