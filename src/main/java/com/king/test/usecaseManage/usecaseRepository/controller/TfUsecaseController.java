package com.king.test.usecaseManage.usecaseRepository.controller;

import com.king.common.Result;
import com.king.test.usecaseManage.requireRepository.entity.TfRequirepoint;
import com.king.test.usecaseManage.usecaseRepository.entity.TfUsecase;
import com.king.test.usecaseManage.usecaseRepository.service.ITfUsecaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@RestController
@RequestMapping("/test/usecase")
public class TfUsecaseController {

    @Autowired
    @Qualifier("tfUsecaseServiceImpl")
    private ITfUsecaseService usecaseService;

    @GetMapping("/list")
    public Result<Map<String, Object>> listUsecases(@RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                   @RequestParam(value = "systemId", required = false) String systemId,
                                                   @RequestParam(value = "directoryId", required = false) String directoryId,
                                                   @RequestParam(value = "usecaseName", required = false) String usecaseName,
                                                   @RequestParam(value = "usecaseType", required = false) String usecaseType,
                                                   @RequestParam(value = "usecaseNature", required = false) String usecaseNature,
                                                   @RequestParam(value = "prority", required = false) String prority,
                                                   @RequestParam(value = "isSmokeTest", required = false) String isSmokeTest,
                                                   @RequestParam(value = "creatorId", required = false) String creatorId) {
        try {
            Map<String, Object> data = usecaseService.getUsecasePage(pageNo, pageSize, systemId, directoryId, usecaseName, usecaseType, usecaseNature, prority, isSmokeTest, creatorId);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("查询用例列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/detail/{usecaseId}")
    public Result<TfUsecase> getUsecaseDetail(@PathVariable("usecaseId") String usecaseId) {
        if (!StringUtils.hasText(usecaseId)) {
            return Result.error("用例ID不能为空");
        }
        try {
            TfUsecase usecase = usecaseService.getUsecaseDetail(usecaseId);
            return Result.success(usecase);
        } catch (Exception e) {
            return Result.error("查询用例详情失败：" + e.getMessage());
        }
    }

    @PostMapping("/create")
    public Result<?> createUsecase(@RequestBody TfUsecase usecase) {
        if (usecase == null) {
            return Result.error("用例数据不能为空");
        }
        try {
            boolean success = usecaseService.createUsecase(usecase);
            return success ? Result.success("新增用例成功") : Result.error("新增用例失败");
        } catch (Exception e) {
            return Result.error("新增用例失败：" + e.getMessage());
        }
    }

    @PostMapping("/update")
    public Result<?> updateUsecase(@RequestBody Map<String, Object> body) {
        if (body == null || !body.containsKey("usecaseId")) {
            return Result.error("用例ID不能为空");
        }
        try {
            TfUsecase usecase = mapToUsecase(body);
            boolean success = usecaseService.updateUsecase(usecase);
            return success ? Result.success("更新用例成功") : Result.error("更新用例失败");
        } catch (Exception e) {
            return Result.error("更新用例失败：" + e.getMessage());
        }
    }

    @PostMapping("/delete")
    public Result<?> deleteUsecase(@RequestBody Map<String, Object> body) {
        if (body == null) {
            return Result.error("请求数据不能为空");
        }
        String usecaseId = (String) body.get("usecaseId");
        if (!StringUtils.hasText(usecaseId)) {
            return Result.error("用例ID不能为空");
        }
        try {
            boolean success = usecaseService.deleteUsecase(usecaseId);
            return success ? Result.success("删除用例成功") : Result.error("删除用例失败");
        } catch (Exception e) {
            return Result.error("删除用例失败：" + e.getMessage());
        }
    }

    @PostMapping("/batchDelete")
    public Result<?> batchDeleteUsecases(@RequestBody Map<String, Object> body) {
        if (body == null) {
            return Result.error("请求数据不能为空");
        }
        @SuppressWarnings("unchecked")
        List<String> usecaseIds = (List<String>) body.get("usecaseIds");
        if (CollectionUtils.isEmpty(usecaseIds)) {
            return Result.error("用例ID列表不能为空");
        }
        try {
            boolean success = usecaseService.batchDeleteUsecases(usecaseIds);
            return success ? Result.success("批量删除成功") : Result.error("批量删除失败");
        } catch (Exception e) {
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }

    @PostMapping("/export")
    public void exportUsecases(@RequestBody Map<String, Object> body, HttpServletResponse response) {
        String systemId = getString(body, "systemId");
        String directoryId = getString(body, "directoryId");
        String usecaseName = getString(body, "usecaseName");
        String usecaseType = getString(body, "usecaseType");
        String usecaseNature = getString(body, "usecaseNature");
        String prority = getString(body, "prority");
        String isSmokeTest = getString(body, "isSmokeTest");
        String creatorId = getString(body, "creatorId");
        try {
            List<TfUsecase> usecases = usecaseService.listUsecasesForExport(systemId, directoryId, usecaseName,
                    usecaseType, usecaseNature, prority, isSmokeTest, creatorId);
            usecaseService.exportUsecasesToExcel(usecases, response);
        } catch (Exception e) {
            try {
                response.reset();
                response.getWriter().write("导出失败：" + e.getMessage());
            } catch (Exception ignored) {
            }
        }
    }

    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) {
        try {
            usecaseService.downloadTemplate(response);
        } catch (Exception e) {
            try {
                response.reset();
                response.getWriter().write("下载模板失败：" + e.getMessage());
            } catch (Exception ignored) {
            }
        }
    }

    @PostMapping("/import")
    public Result<Map<String, Object>> importUsecases(@RequestParam("file") MultipartFile file,
                                                      @RequestParam(value = "systemId", required = false) String systemId,
                                                      @RequestParam(value = "directoryId", required = false) String directoryId) {
        try {
            Map<String, Object> result = usecaseService.importUsecases(file, systemId, directoryId);
            return Result.success(result, "导入完成");
        } catch (Exception e) {
            return Result.error("导入失败：" + e.getMessage());
        }
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(@RequestParam(value = "systemId", required = false) String systemId,
                                                     @RequestParam(value = "directoryId", required = false) String directoryId) {
        try {
            Map<String, Object> stats = usecaseService.getUsecaseStatistics(systemId, directoryId);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计信息失败：" + e.getMessage());
        }
    }

    @GetMapping("/typeStatistics")
    public Result<List<Map<String, Object>>> getTypeStatistics(@RequestParam(value = "systemId", required = false) String systemId,
                                                               @RequestParam(value = "directoryId", required = false) String directoryId) {
        try {
            List<Map<String, Object>> data = usecaseService.getUsecaseTypeStatistics(systemId, directoryId);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取类型统计失败：" + e.getMessage());
        }
    }

    @GetMapping("/statusStatistics")
    public Result<List<Map<String, Object>>> getStatusStatistics(@RequestParam(value = "systemId", required = false) String systemId,
                                                                 @RequestParam(value = "directoryId", required = false) String directoryId) {
        try {
            List<Map<String, Object>> data = usecaseService.getUsecaseStatusStatistics(systemId, directoryId);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取状态统计失败：" + e.getMessage());
        }
    }

    @PostMapping("/copy")
    public Result<TfUsecase> copyUsecase(@RequestBody Map<String, Object> body) {
        String usecaseId = getString(body, "usecaseId");
        if (!StringUtils.hasText(usecaseId)) {
            return Result.error("用例ID不能为空");
        }
        try {
            TfUsecase copy = usecaseService.copyUsecase(usecaseId, body);
            return Result.success(copy, "复制用例成功");
        } catch (Exception e) {
            return Result.error("复制用例失败：" + e.getMessage());
        }
    }

    @PostMapping("/move")
    public Result<?> moveUsecases(@RequestBody Map<String, Object> body) {
        if (body == null) {
            return Result.error("请求数据不能为空");
        }
        @SuppressWarnings("unchecked")
        List<String> usecaseIds = (List<String>) body.get("usecaseIds");
        String targetDirectoryId = getString(body, "targetDirectoryId");
        if (CollectionUtils.isEmpty(usecaseIds) || !StringUtils.hasText(targetDirectoryId)) {
            return Result.error("用例ID列表或目标目录不能为空");
        }
        try {
            boolean success = usecaseService.moveUsecases(usecaseIds, targetDirectoryId);
            return success ? Result.success("移动成功") : Result.error("移动失败");
        } catch (Exception e) {
            return Result.error("移动失败：" + e.getMessage());
        }
    }

    @GetMapping("/history")
    public Result<Map<String, Object>> getUsecaseHistory(@RequestParam("usecaseId") String usecaseId,
                                                         @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        try {
            Map<String, Object> data = usecaseService.getUsecaseHistory(usecaseId, pageNo, pageSize);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取历史记录失败：" + e.getMessage());
        }
    }

    @GetMapping("/requirepoints")
    public Result<List<TfRequirepoint>> getUsecaseRequirePoints(@RequestParam("usecaseId") String usecaseId) {
        try {
            List<TfRequirepoint> data = usecaseService.getLinkedRequirePoints(usecaseId);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取关联需求点失败：" + e.getMessage());
        }
    }

    @PostMapping("/linkRequirePoints")
    public Result<?> linkRequirePoints(@RequestBody Map<String, Object> body) {
        String usecaseId = getString(body, "usecaseId");
        @SuppressWarnings("unchecked")
        List<String> requirePointIds = (List<String>) body.get("requirePointIds");
        if (!StringUtils.hasText(usecaseId) || CollectionUtils.isEmpty(requirePointIds)) {
            return Result.error("用例ID和需求点ID列表不能为空");
        }
        try {
            boolean success = usecaseService.linkRequirePoints(usecaseId, requirePointIds);
            return success ? Result.success("关联需求点成功") : Result.error("关联需求点失败");
        } catch (Exception e) {
            return Result.error("关联需求点失败：" + e.getMessage());
        }
    }

    @PostMapping("/unlinkRequirePoints")
    public Result<?> unlinkRequirePoints(@RequestBody Map<String, Object> body) {
        String usecaseId = getString(body, "usecaseId");
        @SuppressWarnings("unchecked")
        List<String> requirePointIds = (List<String>) body.get("requirePointIds");
        if (!StringUtils.hasText(usecaseId) || CollectionUtils.isEmpty(requirePointIds)) {
            return Result.error("用例ID和需求点ID列表不能为空");
        }
        try {
            boolean success = usecaseService.unlinkRequirePoints(usecaseId, requirePointIds);
            return success ? Result.success("取消关联成功") : Result.error("取消关联失败");
        } catch (Exception e) {
            return Result.error("取消关联失败：" + e.getMessage());
        }
    }

    private TfUsecase mapToUsecase(Map<String, Object> body) {
        TfUsecase usecase = new TfUsecase();
        usecase.setUsecaseId(getString(body, "usecaseId"));
        usecase.setUsecaseName(getString(body, "usecaseName"));
        usecase.setDirectoryId(getString(body, "directoryId"));
        usecase.setSystemId(getString(body, "systemId"));
        usecase.setIsSmokeTest(getString(body, "isSmokeTest"));
        usecase.setUsecaseType(getString(body, "usecaseType"));
        usecase.setTestPoint(getString(body, "testPoint"));
        usecase.setUsecaseNature(getString(body, "usecaseNature"));
        usecase.setPrority(getString(body, "prority"));
        usecase.setLatestExeStatus(getString(body, "latestExeStatus"));
        usecase.setPrecondition(getString(body, "precondition"));
        usecase.setTestData(getString(body, "testData"));
        usecase.setTestStep(getString(body, "testStep"));
        usecase.setExpectedResult(getString(body, "expectedResult"));
        Object workPackageId = body.get("workPackageId");
        if (workPackageId != null) {
            if (workPackageId instanceof Number) {
                usecase.setWorkPackageId(((Number) workPackageId).intValue());
            } else if (workPackageId instanceof String && StringUtils.hasText((String) workPackageId)) {
                usecase.setWorkPackageId(Integer.parseInt((String) workPackageId));
            }
        }
        Object createTime = body.get("createTime");
        if (createTime instanceof String && StringUtils.hasText((String) createTime)) {
            usecase.setCreateTime(parseDateTime((String) createTime));
        }
        Object modifyTime = body.get("modifyTime");
        if (modifyTime instanceof String && StringUtils.hasText((String) modifyTime)) {
            usecase.setModifyTime(parseDateTime((String) modifyTime));
        }
        usecase.setCreatorId(getString(body, "creatorId"));
        usecase.setModifierId(getString(body, "modifierId"));
        return usecase;
    }

    private String getString(Map<String, Object> body, String key) {
        Object value = body.get(key);
        return value == null ? null : String.valueOf(value);
    }

    private LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value.replace(" ", "T"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
