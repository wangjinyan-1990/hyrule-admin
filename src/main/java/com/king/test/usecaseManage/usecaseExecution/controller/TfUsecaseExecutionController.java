package com.king.test.usecaseManage.usecaseExecution.controller;

import com.king.common.Result;
import com.king.test.usecaseManage.usecaseExecution.entity.TfUsecaseExecution;
import com.king.test.usecaseManage.usecaseExecution.entity.TfUsecaseExecutionHistory;
import com.king.test.usecaseManage.usecaseExecution.service.ITfUsecaseExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用例执行Controller
 */
@RestController
@RequestMapping("/test/usecase/execution")
public class TfUsecaseExecutionController {

    @Autowired
    @Qualifier("tfUsecaseExecutionServiceImpl")
    private ITfUsecaseExecutionService executionService;

    /**
     * 添加用例到执行库
     * @param data 用例数据，包含usecaseId、directoryId、systemId
     * @return 添加结果
     */
    @PostMapping("/add")
    public Result<?> addToExecution(@RequestBody Map<String, Object> data) {
        if (data == null) {
            return Result.error("请求数据不能为空");
        }

        String usecaseId = getString(data, "usecaseId");
        String directoryId = getString(data, "directoryId");
        String systemId = getString(data, "systemId");

        if (!StringUtils.hasText(usecaseId)) {
            return Result.error("用例ID不能为空");
        }
        if (!StringUtils.hasText(directoryId)) {
            return Result.error("目录ID不能为空");
        }
        if (!StringUtils.hasText(systemId)) {
            return Result.error("系统ID不能为空");
        }

        try {
            boolean success = executionService.addToExecution(usecaseId, directoryId, systemId);
            if (success) {
                return Result.success("添加用例到执行库成功");
            } else {
                return Result.error("添加用例到执行库失败");
            }
        } catch (Exception e) {
            return Result.error("添加用例到执行库失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询执行用例列表
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param systemId 系统ID（可选）
     * @param directoryId 目录ID（可选）
     * @param includeSubdirectories 是否包含子目录（可选，默认false）
     * @param usecaseId 用例ID（可选）
     * @param usecaseName 用例名称（可选）
     * @param runStatus 执行状态（可选）
     * @param planExecutorId 计划执行人ID（可选）
     * @param actExecutorId 实际执行人ID（可选）
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getExecutionList(
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "systemId", required = false) String systemId,
            @RequestParam(value = "directoryId", required = false) String directoryId,
            @RequestParam(value = "includeSubdirectories", required = false, defaultValue = "false") Boolean includeSubdirectories,
            @RequestParam(value = "usecaseId", required = false) String usecaseId,
            @RequestParam(value = "usecaseName", required = false) String usecaseName,
            @RequestParam(value = "runStatus", required = false) String runStatus,
            @RequestParam(value = "planExecutorId", required = false) String planExecutorId,
            @RequestParam(value = "actExecutorId", required = false) String actExecutorId) {
        try {
            Map<String, Object> data = executionService.getExecutionPage(pageNo, pageSize, systemId, directoryId,
                    includeSubdirectories, usecaseId, usecaseName, runStatus, planExecutorId, actExecutorId);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("查询执行用例列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取执行统计信息
     * @param usecaseId 用例ID（可选）
     * @param usecaseName 用例名称（可选）
     * @param usecaseOverview 用例概述（可选）
     * @param actExecutionTimeStart 实际执行时间开始（可选）
     * @param actExecutionTimeEnd 实际执行时间结束（可选）
     * @param runStatus 执行状态（可选）
     * @param includeSubdirectories 是否包含子目录（可选，默认false）
     * @param directoryId 目录ID（可选）
     * @param systemId 系统ID（可选）
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getExecutionStatistics(
            @RequestParam(value = "usecaseId", required = false) String usecaseId,
            @RequestParam(value = "usecaseName", required = false) String usecaseName,
            @RequestParam(value = "usecaseOverview", required = false) String usecaseOverview,
            @RequestParam(value = "actExecutionTimeStart", required = false) String actExecutionTimeStart,
            @RequestParam(value = "actExecutionTimeEnd", required = false) String actExecutionTimeEnd,
            @RequestParam(value = "runStatus", required = false) String runStatus,
            @RequestParam(value = "includeSubdirectories", required = false, defaultValue = "false") Boolean includeSubdirectories,
            @RequestParam(value = "directoryId", required = false) String directoryId,
            @RequestParam(value = "systemId", required = false) String systemId) {
        try {
            Map<String, Object> statistics = executionService.getExecutionStatistics(
                    systemId, directoryId, includeSubdirectories,
                    usecaseId, usecaseName, usecaseOverview,
                    actExecutionTimeStart, actExecutionTimeEnd, runStatus);
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error("获取执行统计信息失败：" + e.getMessage());
        }
    }

    /**
     * 获取执行详情
     * @param usecaseExecutionId 执行ID
     * @return 执行详情
     */
    @GetMapping("/detail")
    public Result<TfUsecaseExecution> getExecutionDetail(@RequestParam("usecaseExecutionId") Integer usecaseExecutionId) {
        if (usecaseExecutionId == null) {
            return Result.error("执行ID不能为空");
        }
        try {
            TfUsecaseExecution execution = executionService.getExecutionDetail(usecaseExecutionId);
            return Result.success(execution);
        } catch (Exception e) {
            return Result.error("查询执行详情失败：" + e.getMessage());
        }
    }

    /**
     * 更新执行状态
     * @param data 更新数据，包含usecaseExecutionId、runStatus、remark
     * @return 更新结果，包含用例执行历史记录ID
     */
    @PostMapping("/updateStatus")
    public Result<Map<String, Object>> updateRunStatus(@RequestBody Map<String, Object> data) {
        if (data == null) {
            return Result.error("请求数据不能为空");
        }

        Object executionIdObj = data.get("usecaseExecutionId");
        if (executionIdObj == null) {
            return Result.error("执行ID不能为空");
        }
        Integer usecaseExecutionId;
        try {
            if (executionIdObj instanceof Integer) {
                usecaseExecutionId = (Integer) executionIdObj;
            } else {
                usecaseExecutionId = Integer.valueOf(executionIdObj.toString());
            }
        } catch (Exception e) {
            return Result.error("执行ID格式错误");
        }

        String runStatus = getString(data, "runStatus");
        if (!StringUtils.hasText(runStatus)) {
            return Result.error("执行状态不能为空");
        }

        String remark = getString(data, "remark");
        String actExecutorId = getString(data, "actExecutorId");

        try {
            Integer usecaseExecutionHistoryId = executionService.updateRunStatus(usecaseExecutionId, runStatus, remark, actExecutorId);
            if (usecaseExecutionHistoryId != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("usecaseExecutionHistoryId", usecaseExecutionHistoryId);
                return Result.success(result, "更新执行状态成功");
            } else {
                return Result.error("更新执行状态失败");
            }
        } catch (Exception e) {
            return Result.error("更新执行状态失败：" + e.getMessage());
        }
    }

    /**
     * 获取历史执行记录列表
     * @param directoryId 目录ID（可选）
     * @param usecaseId 用例ID（可选）
     * @return 历史执行记录列表
     */
    @GetMapping("/history")
    public Result<List<TfUsecaseExecutionHistory>> getExecutionHistory(
            @RequestParam(value = "directoryId", required = false) String directoryId,
            @RequestParam(value = "usecaseId", required = false) String usecaseId) {
        try {
            List<TfUsecaseExecutionHistory> historyList = executionService.getExecutionHistory(directoryId, usecaseId);
            return Result.success(historyList);
        } catch (Exception e) {
            return Result.error("查询历史执行记录失败：" + e.getMessage());
        }
    }

    /**
     * 删除执行用例
     * @param usecaseExecutionId 执行ID
     * @return 删除结果
     */
    @DeleteMapping("/{usecaseExecutionId}")
    public Result<?> deleteExecution(@PathVariable("usecaseExecutionId") Integer usecaseExecutionId) {
        if (usecaseExecutionId == null) {
            return Result.error("执行ID不能为空");
        }
        try {
            boolean success = executionService.deleteExecution(usecaseExecutionId);
            if (success) {
                return Result.success("删除执行用例成功");
            } else {
                return Result.error("删除执行用例失败");
            }
        } catch (Exception e) {
            return Result.error("删除执行用例失败：" + e.getMessage());
        }
    }

    /**
     * 批量更新执行状态
     * @param data 更新数据，包含usecaseExecutionIds（用例执行ID数组）、runStatus（执行状态）、remark（执行备注）
     * @return 更新结果
     */
    @PostMapping("/batchUpdateStatus")
    public Result<?> batchUpdateRunStatus(@RequestBody Map<String, Object> data) {
        if (data == null) {
            return Result.error("请求数据不能为空");
        }

        Object executionIdsObj = data.get("usecaseExecutionIds");
        if (executionIdsObj == null) {
            return Result.error("用例执行ID列表不能为空");
        }

        List<Integer> usecaseExecutionIds;
        try {
            if (executionIdsObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> idList = (List<Object>) executionIdsObj;
                usecaseExecutionIds = new ArrayList<>();
                for (Object idObj : idList) {
                    if (idObj instanceof Integer) {
                        usecaseExecutionIds.add((Integer) idObj);
                    } else {
                        usecaseExecutionIds.add(Integer.valueOf(idObj.toString()));
                    }
                }
            } else {
                return Result.error("用例执行ID列表格式错误");
            }
        } catch (Exception e) {
            return Result.error("用例执行ID列表格式错误：" + e.getMessage());
        }

        if (usecaseExecutionIds.isEmpty()) {
            return Result.error("用例执行ID列表不能为空");
        }

        String runStatus = getString(data, "runStatus");
        if (!StringUtils.hasText(runStatus)) {
            return Result.error("执行状态不能为空");
        }

        String remark = getString(data, "remark");
        String actExecutorId = getString(data, "actExecutorId");

        try {
            boolean success = executionService.batchUpdateRunStatus(usecaseExecutionIds, runStatus, remark, actExecutorId);
            if (success) {
                return Result.success("批量更新执行状态成功");
            } else {
                return Result.error("批量更新执行状态失败");
            }
        } catch (Exception e) {
            return Result.error("批量更新执行状态失败：" + e.getMessage());
        }
    }

    /**
     * 移动执行用例到其他目录
     * @param data 移动数据，包含usecaseExecutionIds（用例执行ID数组）和targetDirectoryId（目标目录ID）
     * @return 移动结果
     */
    @PostMapping("/move")
    public Result<?> moveExecutions(@RequestBody Map<String, Object> data) {
        if (data == null) {
            return Result.error("请求数据不能为空");
        }

        Object executionIdsObj = data.get("usecaseExecutionIds");
        if (executionIdsObj == null) {
            return Result.error("用例执行ID列表不能为空");
        }

        List<Integer> usecaseExecutionIds;
        try {
            if (executionIdsObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> idList = (List<Object>) executionIdsObj;
                usecaseExecutionIds = new ArrayList<>();
                for (Object idObj : idList) {
                    if (idObj instanceof Integer) {
                        usecaseExecutionIds.add((Integer) idObj);
                    } else {
                        usecaseExecutionIds.add(Integer.valueOf(idObj.toString()));
                    }
                }
            } else {
                return Result.error("用例执行ID列表格式错误");
            }
        } catch (Exception e) {
            return Result.error("用例执行ID列表格式错误：" + e.getMessage());
        }

        if (usecaseExecutionIds.isEmpty()) {
            return Result.error("用例执行ID列表不能为空");
        }

        String targetDirectoryId = getString(data, "targetDirectoryId");
        if (!StringUtils.hasText(targetDirectoryId)) {
            return Result.error("目标目录ID不能为空");
        }

        try {
            boolean success = executionService.moveExecutions(usecaseExecutionIds, targetDirectoryId);
            if (success) {
                return Result.success("移动执行用例到目标目录成功");
            } else {
                return Result.error("移动执行用例到目标目录失败");
            }
        } catch (Exception e) {
            return Result.error("移动执行用例到目标目录失败：" + e.getMessage());
        }
    }

    /**
     * 批量更新计划执行信息
     * @param data 更新数据，包含usecaseExecutionIds（用例执行ID数组）、planExecutorId（计划执行人ID）、planExecutionDate（计划执行日期）
     * @return 更新结果
     */
    @PostMapping("/batchUpdatePlan")
    public Result<?> batchUpdatePlanExecution(@RequestBody Map<String, Object> data) {
        if (data == null) {
            return Result.error("请求数据不能为空");
        }

        Object executionIdsObj = data.get("usecaseExecutionIds");
        if (executionIdsObj == null) {
            return Result.error("用例执行ID列表不能为空");
        }

        List<Integer> usecaseExecutionIds;
        try {
            if (executionIdsObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> idList = (List<Object>) executionIdsObj;
                usecaseExecutionIds = new ArrayList<>();
                for (Object idObj : idList) {
                    if (idObj instanceof Integer) {
                        usecaseExecutionIds.add((Integer) idObj);
                    } else {
                        usecaseExecutionIds.add(Integer.valueOf(idObj.toString()));
                    }
                }
            } else {
                return Result.error("用例执行ID列表格式错误");
            }
        } catch (Exception e) {
            return Result.error("用例执行ID列表格式错误：" + e.getMessage());
        }

        if (usecaseExecutionIds.isEmpty()) {
            return Result.error("用例执行ID列表不能为空");
        }

        String planExecutorId = getString(data, "planExecutorId");
        Object planExecutionDateObj = data.get("planExecutionDate");
        java.util.Date planExecutionDate = null;

        // 解析计划执行日期
        if (planExecutionDateObj != null) {
            try {
                if (planExecutionDateObj instanceof java.util.Date) {
                    planExecutionDate = (java.util.Date) planExecutionDateObj;
                } else if (planExecutionDateObj instanceof Long) {
                    // 时间戳格式
                    planExecutionDate = new java.util.Date((Long) planExecutionDateObj);
                } else {
                    // 字符串格式，尝试解析
                    String dateStr = planExecutionDateObj.toString();
                    if (StringUtils.hasText(dateStr)) {
                        // 尝试多种日期格式
                        java.text.SimpleDateFormat sdf1 = new java.text.SimpleDateFormat("yyyy-MM-dd");
                        try {
                            planExecutionDate = sdf1.parse(dateStr);
                        } catch (Exception e1) {
                            java.text.SimpleDateFormat sdf2 = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                planExecutionDate = sdf2.parse(dateStr);
                            } catch (Exception e2) {
                                return Result.error("计划执行日期格式错误，支持格式：yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                return Result.error("计划执行日期解析失败：" + e.getMessage());
            }
        }

        // 至少需要提供一个参数
        if (!StringUtils.hasText(planExecutorId) && planExecutionDate == null) {
            return Result.error("至少需要提供planExecutorId或planExecutionDate参数");
        }

        try {
            boolean success = executionService.batchUpdatePlanExecution(usecaseExecutionIds, planExecutorId, planExecutionDate);
            if (success) {
                return Result.success("批量更新计划执行信息成功");
            } else {
                return Result.error("批量更新计划执行信息失败");
            }
        } catch (Exception e) {
            return Result.error("批量更新计划执行信息失败：" + e.getMessage());
        }
    }

    /**
     * 从Map中安全获取String值
     */
    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}
