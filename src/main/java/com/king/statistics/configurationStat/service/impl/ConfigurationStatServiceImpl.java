package com.king.statistics.configurationStat.service.impl;

import com.king.statistics.configurationStat.dto.DeployBySysStatDTO;
import com.king.statistics.configurationStat.mapper.ConfigurationStatMapper;
import com.king.statistics.configurationStat.service.IConfigurationStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 配置统计Service实现类
 */
@Service("configurationStatServiceImpl")
public class ConfigurationStatServiceImpl implements IConfigurationStatService {
    
    @Autowired
    private ConfigurationStatMapper configurationStatMapper;
    
    @Override
    public List<DeployBySysStatDTO> getDeployBySysStat(String startDate, String endDate, String systemId) {
        // 查询各系统统计数据
        List<DeployBySysStatDTO> result = configurationStatMapper.selectDeployBySysStat(startDate, endDate, systemId);
        
        // 计算合计
        int totalSitCount = 0;
        int totalPatCount = 0;
        
        for (DeployBySysStatDTO stat : result) {
            totalSitCount += (stat.getSitCount() != null ? stat.getSitCount() : 0);
            totalPatCount += (stat.getPatCount() != null ? stat.getPatCount() : 0);
        }
        
        // 添加合计行
        DeployBySysStatDTO totalRow = new DeployBySysStatDTO("合计", totalSitCount, totalPatCount);
        result.add(totalRow);
        
        return result;
    }
}
