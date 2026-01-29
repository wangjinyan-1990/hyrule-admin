package com.king.configuration.merge.service;

import com.king.configuration.merge.entity.TfDeployRecord;

import java.util.Map;

/**
 * Merge Request合并Service接口
 */
public interface IMRMergeService {

    /**
     * 解析Merge Request，获取代码清单、标题和合并状态
     * @param mergeRequest Merge Request URL
     * @param systemId 系统ID
     * @return 包含codeList、sendTestInfo和mergeState的Map
     */
    Map<String, String> parseMergeRequest(String mergeRequest, String systemId);

    /**
     * 创建发版登记（SIT）
     * @param deployRecord 发版登记信息
     */
    void createMergeRecord(TfDeployRecord deployRecord);

}
