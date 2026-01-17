package com.king.configuration.deploy.service;

import com.king.common.Result;
import com.king.configuration.deploy.entity.TfDeployRecord;

/**
 * PAT部署Service接口
 */
public interface IPATDeployService {

    /**
     * 创建发版登记（PAT）
     * @param deployRecord 发版登记信息
     * @return Result 包含生成的版本号
     */
    Result<String> createPATDeployRecord(TfDeployRecord deployRecord);
}
