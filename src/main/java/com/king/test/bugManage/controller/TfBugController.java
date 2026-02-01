package com.king.test.bugManage.controller;

import com.king.common.Result;
import com.king.test.bugManage.entity.TfBug;
import com.king.test.bugManage.service.ITfBugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 缺陷Controller
 */
@RestController
@RequestMapping("/test/bug")
public class TfBugController {

    @Autowired
    @Qualifier("tfBugServiceImpl")
    private ITfBugService bugService;

    /**
     * 分页查询缺陷列表
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param systemId 系统ID（可选）
     * @param directoryId 目录ID（可选）
     * @param bugId 缺陷ID（可选）
     * @param bugName 缺陷名称（可选）
     * @param bugState 缺陷状态（可选）
     * @param bugType 缺陷类型（可选）
     * @param bugSeverityLevel 缺陷严重级别（可选）
     * @param bugSource 缺陷来源（可选）
     * @param submitterId 提交人ID（可选）
     * @param checkerId 验证人ID（可选）
     * @param developerId 开发人员ID（可选）
     * @param commitTimeStart 提交时间开始（可选）
     * @param commitTimeEnd 提交时间结束（可选）
     * @param closeTimeStart 关闭时间开始（可选）
     * @param closeTimeEnd 关闭时间结束（可选）
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getBugList(
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "systemId", required = false) String systemId,
            @RequestParam(value = "directoryId", required = false) Integer directoryId,
            @RequestParam(value = "bugId", required = false) String bugId,
            @RequestParam(value = "bugName", required = false) String bugName,
            @RequestParam(value = "bugState", required = false) String bugState,
            @RequestParam(value = "bugType", required = false) String bugType,
            @RequestParam(value = "bugSeverityLevel", required = false) Integer bugSeverityLevel,
            @RequestParam(value = "bugSource", required = false) String bugSource,
            @RequestParam(value = "submitterId", required = false) String submitterId,
            @RequestParam(value = "checkerId", required = false) String checkerId,
            @RequestParam(value = "developerId", required = false) String developerId,
            @RequestParam(value = "commitTimeStart", required = false) String commitTimeStart,
            @RequestParam(value = "commitTimeEnd", required = false) String commitTimeEnd,
            @RequestParam(value = "closeTimeStart", required = false) String closeTimeStart,
            @RequestParam(value = "closeTimeEnd", required = false) String closeTimeEnd) {
        try {
            Map<String, Object> data = bugService.getBugPage(pageNo, pageSize, systemId, directoryId,
                    bugId, bugName, bugState, bugType, bugSeverityLevel, bugSource,
                    submitterId, checkerId, developerId,
                    commitTimeStart, commitTimeEnd, closeTimeStart, closeTimeEnd);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("查询缺陷列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取关联的缺陷列表
     * @param usecaseId 用例ID（可选）
     * @param directoryId 目录ID（可选）
     * @param currentPage 当前页码
     * @param pageSize 每页大小
     * @return 关联缺陷列表
     */
    @GetMapping("/relatedBugs")
    public Result<Map<String, Object>> getRelatedBugs(
            @RequestParam(value = "usecaseId", required = false) String usecaseId,
            @RequestParam(value = "directoryId", required = false) String directoryId,
            @RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        try {
            Map<String, Object> data = bugService.getRelatedBugs(currentPage, pageSize, usecaseId, directoryId);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("查询关联缺陷列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取缺陷详情
     * @param bugId 缺陷ID
     * @return 缺陷详情
     */
    @GetMapping("/{bugId}")
    public Result<TfBug> getBugDetail(@PathVariable("bugId") Integer bugId) {
        if (bugId == null) {
            return Result.error("缺陷ID不能为空");
        }
        try {
            TfBug bug = bugService.getBugDetail(bugId);
            return Result.success(bug);
        } catch (Exception e) {
            return Result.error("查询缺陷详情失败：" + e.getMessage());
        }
    }

    /**
     * 创建缺陷
     * @param bug 缺陷对象
     * @return 创建结果
     */
    @PostMapping
    public Result<?> createBug(@RequestBody TfBug bug) {
        if (bug == null) {
            return Result.error("缺陷数据不能为空");
        }
        try {
            boolean success = bugService.createBug(bug);
            if (success) {
                return Result.success("创建缺陷成功");
            } else {
                return Result.error("创建缺陷失败");
            }
        } catch (Exception e) {
            return Result.error("创建缺陷失败：" + e.getMessage());
        }
    }

    /**
     * 更新缺陷
     * @param bugId 缺陷ID
     * @param bug 缺陷对象
     * @return 更新结果
     */
    @PutMapping("/{bugId}")
    public Result<?> updateBug(@PathVariable("bugId") Integer bugId, @RequestBody TfBug bug) {
        if (bugId == null) {
            return Result.error("缺陷ID不能为空");
        }
        if (bug == null) {
            return Result.error("缺陷数据不能为空");
        }
        bug.setBugId(bugId);
        try {
            boolean success = bugService.updateBug(bug);
            if (success) {
                return Result.success("更新缺陷成功");
            } else {
                return Result.error("更新缺陷失败");
            }
        } catch (Exception e) {
            return Result.error("更新缺陷失败：" + e.getMessage());
        }
    }

    /**
     * 删除缺陷
     * @param bugId 缺陷ID
     * @return 删除结果
     */
    @DeleteMapping("/{bugId}")
    public Result<?> deleteBug(@PathVariable("bugId") Integer bugId) {
        if (bugId == null) {
            return Result.error("缺陷ID不能为空");
        }
        try {
            boolean success = bugService.deleteBug(bugId);
            if (success) {
                return Result.success("删除缺陷成功");
            } else {
                return Result.error("删除缺陷失败");
            }
        } catch (Exception e) {
            return Result.error("删除缺陷失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除缺陷
     * @param data 请求数据，包含bugIds列表
     * @return 删除结果
     */
    @DeleteMapping("/batch/delete")
    public Result<?> batchDeleteBugs(@RequestBody Map<String, Object> data) {
        if (data == null) {
            return Result.error("请求数据不能为空");
        }
        @SuppressWarnings("unchecked")
        List<Integer> bugIds = (List<Integer>) data.get("bugIds");
        if (CollectionUtils.isEmpty(bugIds)) {
            return Result.error("缺陷ID列表不能为空");
        }
        try {
            boolean success = bugService.batchDeleteBugs(bugIds);
            if (success) {
                return Result.success("批量删除成功");
            } else {
                return Result.error("批量删除失败");
            }
        } catch (Exception e) {
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }

    /**
     * 导出缺陷列表
     * @param systemId 系统ID（可选）
     * @param directoryId 目录ID（可选）
     * @param bugId 缺陷ID（可选）
     * @param bugName 缺陷名称（可选）
     * @param bugState 缺陷状态（可选）
     * @param bugType 缺陷类型（可选）
     * @param bugSeverityLevel 缺陷严重级别（可选）
     * @param bugSource 缺陷来源（可选）
     * @param submitterId 提交人ID（可选）
     * @param checkerId 验证人ID（可选）
     * @param developerId 开发人员ID（可选）
     * @param commitTimeStart 提交时间开始（可选）
     * @param commitTimeEnd 提交时间结束（可选）
     * @param closeTimeStart 关闭时间开始（可选）
     * @param closeTimeEnd 关闭时间结束（可选）
     * @param response HTTP响应对象
     */
    @GetMapping("/export")
    public void exportBugList(
            @RequestParam(value = "systemId", required = false) String systemId,
            @RequestParam(value = "directoryId", required = false) Integer directoryId,
            @RequestParam(value = "bugId", required = false) String bugId,
            @RequestParam(value = "bugName", required = false) String bugName,
            @RequestParam(value = "bugState", required = false) String bugState,
            @RequestParam(value = "bugType", required = false) String bugType,
            @RequestParam(value = "bugSeverityLevel", required = false) Integer bugSeverityLevel,
            @RequestParam(value = "bugSource", required = false) String bugSource,
            @RequestParam(value = "submitterId", required = false) String submitterId,
            @RequestParam(value = "checkerId", required = false) String checkerId,
            @RequestParam(value = "developerId", required = false) String developerId,
            @RequestParam(value = "commitTimeStart", required = false) String commitTimeStart,
            @RequestParam(value = "commitTimeEnd", required = false) String commitTimeEnd,
            @RequestParam(value = "closeTimeStart", required = false) String closeTimeStart,
            @RequestParam(value = "closeTimeEnd", required = false) String closeTimeEnd,
            HttpServletResponse response) {
        try {
            List<TfBug> bugs = bugService.listBugsForExport(systemId, directoryId,
                    bugId, bugName, bugState, bugType, bugSeverityLevel, bugSource,
                    submitterId, checkerId, developerId,
                    commitTimeStart, commitTimeEnd, closeTimeStart, closeTimeEnd);
            bugService.exportBugsToExcel(bugs, response);
        } catch (Exception e) {
            try {
                response.reset();
                response.getWriter().write("导出失败：" + e.getMessage());
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 获取缺陷历史记录
     * @param bugId 缺陷ID
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 历史记录列表
     */
    @GetMapping("/{bugId}/history")
    public Result<Map<String, Object>> getBugHistory(
            @PathVariable("bugId") Integer bugId,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        if (bugId == null) {
            return Result.error("缺陷ID不能为空");
        }
        try {
            Map<String, Object> data = bugService.getBugHistory(bugId, pageNo, pageSize);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("查询缺陷历史记录失败：" + e.getMessage());
        }
    }
}
