package com.king.configuration.deploy.service;

import com.king.configuration.deploy.dto.PATDeployRecordDTO;
import com.king.configuration.deploy.entity.TfDeployRecord;

/**
 * 创建PAT发版登记（外部API接口）
 */
public interface IPATDeployApiService {

    /**
     * 创建PAT发版登记（外部API接口）
     * @param dto PAT发版登记DTO
     */
    void createPATDeployRecordByApi(PATDeployRecordDTO dto);
}
