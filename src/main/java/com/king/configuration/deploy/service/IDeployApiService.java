package com.king.configuration.deploy.service;

import com.king.configuration.deploy.dto.DeployRecordApiDTO;

/**
 * 创建发版登记（外部API接口）
 */
public interface IDeployApiService {

    /**
     * 创建发版登记（外部API接口）
     * @param dto 发版登记DTO
     */
    void createDeployRecordByApi(DeployRecordApiDTO dto);
}
