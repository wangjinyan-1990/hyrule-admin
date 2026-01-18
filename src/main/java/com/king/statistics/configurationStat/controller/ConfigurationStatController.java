package com.king.statistics.configurationStat.controller;

import com.king.common.Result;
import com.king.statistics.configurationStat.dto.DeployBySysStatDTO;
import com.king.statistics.configurationStat.service.IConfigurationStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 配置统计Controller
 */
@RestController
@RequestMapping("/statistics/configurationStat")
public class ConfigurationStatController {
    
    @Autowired
    @Qualifier("configurationStatServiceImpl")
    private IConfigurationStatService configurationStatService;
    
    /**
     * 获取各系统发版数统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param systemId 系统ID（可选）
     * @return 统计结果
     */
    @GetMapping("/deployBySysStat")
    public Result<List<DeployBySysStatDTO>> getDeployBySysStat(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "systemId", required = false) String systemId) {
        try {
            List<DeployBySysStatDTO> data = configurationStatService.getDeployBySysStat(startDate, endDate, systemId);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取各系统发版数统计失败: " + e.getMessage());
        }
    }
}
