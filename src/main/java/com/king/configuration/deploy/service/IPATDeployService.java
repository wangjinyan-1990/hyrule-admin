package com.king.configuration.deploy.service;

import com.king.configuration.deploy.dto.PATDeployRecordDTO;
import com.king.configuration.deploy.entity.TfDeployRecord;

/**
 * PAT部署Service接口
 */
public interface IPATDeployService {

    /**
     * 创建发版登记（PAT）
     * @param deployRecord 发版登记信息
     */
    void createPATDeployRecord(TfDeployRecord deployRecord);
}
