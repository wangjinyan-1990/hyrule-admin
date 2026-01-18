package com.king.statistics.configurationStat.dto;

import lombok.Data;

/**
 * 各系统发版数统计DTO
 */
@Data
public class DeployBySysStatDTO {
    
    /**
     * 系统名称
     */
    private String systemName;
    
    /**
     * SIT发版数
     */
    private Integer sitCount;
    
    /**
     * PAT发版数
     */
    private Integer patCount;
    
    public DeployBySysStatDTO() {
    }
    
    public DeployBySysStatDTO(String systemName, Integer sitCount, Integer patCount) {
        this.systemName = systemName;
        this.sitCount = sitCount;
        this.patCount = patCount;
    }
}
