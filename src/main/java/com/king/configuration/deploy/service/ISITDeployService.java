package com.king.configuration.deploy.service;

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
}
