package com.king.configuration.merge.controller;

import com.king.common.Result;
import com.king.configuration.merge.entity.TfDeployRecord;
import com.king.configuration.merge.service.IListMergeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

/**
 * 清单合并Controller
 */
@RestController
@RequestMapping("/configuration/merge/listMerge")
public class ListMergeController {

    @Autowired
    @Qualifier("listMergeServiceImpl")
    private IListMergeService listMergeService;

    /**
     * 创建合并登记
     * @param deployRecord 发版登记信息
     * @return 创建结果，包含生成的版本号（versionCode）
     */
    @PostMapping("/record")
    public Result<String> createListMergeRecord(@RequestBody TfDeployRecord deployRecord) {
        try {
            Result<String> serviceResult = listMergeService.createListMergeRecord(deployRecord);
            // Service 已经返回了 Result，直接返回
            return serviceResult;
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("创建合并登记失败: " + e.getMessage());
        }
    }

}
