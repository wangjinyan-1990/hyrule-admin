package com.king.configuration.merge.service;

import com.king.configuration.merge.dto.DeployRecordApiDTO;

/**
 * 创建发版登记（外部API接口）
 */
public interface IDeployApiService {

    /**
     * 创建发版登记（外部API接口）
     * @param dto 发版登记DTO
     * @return 生成的版本号（versionCode）
     */
    String createDeployRecordByApi(DeployRecordApiDTO dto);
}
