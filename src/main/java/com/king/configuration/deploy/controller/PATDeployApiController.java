package com.king.configuration.deploy.controller;

import com.king.common.Result;
import com.king.configuration.deploy.dto.PATDeployRecordDTO;
import com.king.configuration.deploy.service.IPATDeployApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

/**
 * PAT部署外部API Controller
 */
@RestController
@RequestMapping("/api/deploy")
public class PATDeployApiController {
    
    @Autowired
    @Qualifier("patDeployApiServiceImpl")
    private IPATDeployApiService patDeployApiService;
    
    /**
     * 创建PAT发版登记（外部API接口）
     * @param dto PAT发版登记DTO
     * @return 创建结果
     */
    @PostMapping("/patRecord")
    public Result<?> createPATDeployRecordByApi(@RequestBody PATDeployRecordDTO dto) {
        try {
            patDeployApiService.createPATDeployRecordByApi(dto);
            return Result.success("PAT发版登记创建成功");
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("创建PAT发版登记失败: " + e.getMessage());
        }
    }
}

