package com.king.statistics.configurationStat.service;

import com.king.statistics.configurationStat.dto.DeployBySysStatDTO;

import java.util.List;

/**
 * 配置统计Service接口
 */
public interface IConfigurationStatService {
    
    /**
     * 获取各系统发版数统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param systemId 系统ID（可选）
     * @return 统计结果列表（包含合计行）
     */
    List<DeployBySysStatDTO> getDeployBySysStat(String startDate, String endDate, String systemId);
}
