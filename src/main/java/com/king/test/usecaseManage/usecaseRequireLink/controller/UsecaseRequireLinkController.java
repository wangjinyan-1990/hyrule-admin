package com.king.test.usecaseManage.usecaseRequireLink.controller;

import com.king.common.Result;
import com.king.test.usecaseManage.usecaseRepository.entity.TfUsecase;
import com.king.test.usecaseManage.usecaseRequireLink.dto.UnlinkTestCasesDTO;
import com.king.test.usecaseManage.usecaseRequireLink.service.IUsecaseRequireLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用例和需求点关联Controller
 */
@RestController
@RequestMapping("/test/usecaseRequireLink")
public class UsecaseRequireLinkController {

    @Autowired
    @Qualifier("usecaseRequireLinkServiceImpl")
    private IUsecaseRequireLinkService usecaseRequireLinkService;

    /**
     * 获取需求点关联的测试用例
     * @param requirePointId 需求点ID
     * @return 关联的测试用例列表
     */
    @GetMapping("/testcases")
    public Result<List<TfUsecase>> getRequirePointTestCases(@RequestParam("requirePointId") String requirePointId) {
        if (!StringUtils.hasText(requirePointId)) {
            return Result.error("需求点ID不能为空");
        }
        
        try {
            List<TfUsecase> usecases = usecaseRequireLinkService.getUsecasesByRequirePointId(requirePointId);
            return Result.success(usecases);
        } catch (Exception e) {
            return Result.error("获取需求点关联的测试用例失败: " + e.getMessage());
        }
    }

    /**
     * 取消关联测试用例
     * @param dto 取消关联参数（包含需求点ID和测试用例ID数组）
     * @return 取消关联结果
     */
    @PostMapping("/unlinkTestCases")
    public Result<?> unlinkTestCasesFromRequirePoint(@RequestBody UnlinkTestCasesDTO dto) {
        if (dto == null) {
            return Result.error("参数不能为空");
        }
        
        if (!StringUtils.hasText(dto.getRequirePointId())) {
            return Result.error("需求点ID不能为空");
        }
        
        if (CollectionUtils.isEmpty(dto.getTestCaseIds())) {
            return Result.error("测试用例ID列表不能为空");
        }
        
        try {
            boolean success = usecaseRequireLinkService.unlinkTestCasesFromRequirePoint(
                    dto.getRequirePointId(), dto.getTestCaseIds());
            if (success) {
                return Result.success("取消关联测试用例成功");
            } else {
                return Result.error("取消关联测试用例失败");
            }
        } catch (Exception e) {
            return Result.error("取消关联测试用例失败: " + e.getMessage());
        }
    }

    /**
     * 关联测试用例到需求点
     * @param dto 关联参数（包含需求点ID和测试用例ID数组）
     * @return 关联结果
     */
    @PostMapping("/linkTestCases")
    public Result<?> linkTestCasesToRequirePoint(@RequestBody UnlinkTestCasesDTO dto) {
        if (dto == null) {
            return Result.error("参数不能为空");
        }
        
        if (!StringUtils.hasText(dto.getRequirePointId())) {
            return Result.error("需求点ID不能为空");
        }
        
        if (CollectionUtils.isEmpty(dto.getTestCaseIds())) {
            return Result.error("测试用例ID列表不能为空");
        }
        
        try {
            boolean success = usecaseRequireLinkService.linkTestCasesToRequirePoint(
                    dto.getRequirePointId(), dto.getTestCaseIds());
            if (success) {
                return Result.success("关联测试用例到需求点成功");
            } else {
                return Result.error("关联测试用例到需求点失败");
            }
        } catch (Exception e) {
            return Result.error("关联测试用例到需求点失败: " + e.getMessage());
        }
    }
}

