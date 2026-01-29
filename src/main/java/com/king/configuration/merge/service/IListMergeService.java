package com.king.configuration.merge.service;

import com.king.common.Result;
import com.king.configuration.merge.entity.TfDeployRecord;

/**
 * 清单合并Service接口
 */
public interface IListMergeService {

    /**
     * 清单合并
     * @param deployRecord 合并后登记信息
     * @return Result 包含生成的版本号
     */
    Result<String> createListMergeRecord(TfDeployRecord deployRecord);
}
