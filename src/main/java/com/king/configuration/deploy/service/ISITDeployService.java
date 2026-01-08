package com.king.configuration.deploy.service;

import com.king.configuration.deploy.entity.TfDeployRecord;

import java.util.Map;

/**
 * SIT部署Service接口
 */
public interface ISITDeployService {
    
    /**
     * 解析Merge Request，获取代码清单、标题和合并状态
     * @param mergeRequest Merge Request URL
     * @param systemId 系统ID
     * @return 包含codeList、sendTestCode和mergeState的Map
     */
    Map<String, String> parseMergeRequest(String mergeRequest, String systemId);
    
    /**
     * 创建发版登记（SIT）
     * @param deployRecord 发版登记信息
     */
    void createSITDeployRecord(TfDeployRecord deployRecord);
    
    /**
     * 更新发版登记（SIT）
     * @param deployRecord 发版登记信息
     */
    void updateSITDeployRecord(TfDeployRecord deployRecord);
    
    /**
     * 根据部署ID获取发版登记详情（SIT）
     * @param deployId 部署ID
     * @return 发版登记信息
     */
    TfDeployRecord getSITDeployRecordDetail(Integer deployId);
}
