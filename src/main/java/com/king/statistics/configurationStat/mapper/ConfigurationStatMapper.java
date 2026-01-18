package com.king.statistics.configurationStat.mapper;

import com.king.statistics.configurationStat.dto.DeployBySysStatDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 配置统计Mapper接口
 */
@Mapper
public interface ConfigurationStatMapper {
    
    /**
     * 查询各系统发版数统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param systemId 系统ID（可选）
     * @return 统计结果列表
     */
    List<DeployBySysStatDTO> selectDeployBySysStat(@Param("startDate") String startDate,
                                                    @Param("endDate") String endDate,
                                                    @Param("systemId") String systemId);
}
