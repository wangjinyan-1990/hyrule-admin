package com.king.configuration.merge.controller;

import com.king.common.Result;
import com.king.configuration.merge.entity.TfDeployRecord;
import com.king.configuration.merge.service.IMRMergeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * MergeRequest合并Controller
 */
@RestController
@RequestMapping("/configuration/merge/MRMerge")
public class MRMergeController {

    @Autowired
    @Qualifier("MRMergeServiceImpl")
    private IMRMergeService MRMergeService;

    /**
     * 解析Merge Request，获取代码清单和标题
     * @param mergeRequest Merge Request URL
     * @param systemId 系统ID
     * @return 包含codeList和sendTestInfo的结果
     */
    @PostMapping("/parseMR")
    public Result<Map<String, String>> parseMergeRequest(@RequestBody Map<String, String> params) {
        try {
            String mergeRequest = params.get("mergeRequest");
            String systemId = params.get("systemId");

            if (mergeRequest == null || mergeRequest.trim().isEmpty()) {
                return Result.error(0, "Merge Request URL不能为空");
            }

            if (systemId == null || systemId.trim().isEmpty()) {
                return Result.error(0, "系统ID不能为空");
            }

            Map<String, String> result = MRMergeService.parseMergeRequest(mergeRequest, systemId);
            return Result.success(result);

        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("解析Merge Request失败: " + e.getMessage());
        }
    }

    /**
     * 创建发版登记
     * @param deployRecord 发版登记信息
     * @return 创建结果
     */
    @PostMapping("/record")
    public Result<?> createMergeRecord(@RequestBody TfDeployRecord deployRecord) {
        try {
            MRMergeService.createMergeRecord(deployRecord);
            return Result.success("合并登记创建成功");
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("合并发版登记失败: " + e.getMessage());
        }
    }



}
