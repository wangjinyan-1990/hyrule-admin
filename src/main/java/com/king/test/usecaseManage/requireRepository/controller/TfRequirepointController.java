package com.king.test.usecaseManage.requireRepository.controller;

import com.king.common.Result;
import com.king.test.usecaseManage.requireRepository.entity.TfRequirepoint;
import com.king.test.usecaseManage.requireRepository.service.ITfRequirepointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * 需求点Controller
 */
@RestController
@RequestMapping("/test/requirepoint")
public class TfRequirepointController {

    @Autowired
    @Qualifier("tfRequirepointServiceImpl")
    private ITfRequirepointService tfRequirepointService;

    /**
     * 分页查询需求点列表
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param systemId 系统ID（可选）
     * @param directoryId 目录ID（可选）
     * @param requirePointType 需求点类型（可选）
     * @param reviewStatus 评审状态（可选）
     * @param requireStatus 需求状态（可选）
     * @param designer 设计人（可选）
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getRequirePointList(
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "systemId", required = false) String systemId,
            @RequestParam(value = "directoryId", required = false) String directoryId,
            @RequestParam(value = "requirePointType", required = false) String requirePointType,
            @RequestParam(value = "reviewStatus", required = false) String reviewStatus,
            @RequestParam(value = "requireStatus", required = false) String requireStatus,
            @RequestParam(value = "designer", required = false) String designer) {

        try {
            Map<String, Object> data = tfRequirepointService.getRequirepointsWithPagination(
                pageNo, pageSize, systemId, directoryId, requirePointType, 
                reviewStatus, requireStatus, designer);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("查询需求点列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID查询需求点详情
     * @param requirePointId 需求点ID
     * @return 需求点详情
     */
    @GetMapping("/detail/{requirePointId}")
    public Result<TfRequirepoint> getRequirePointById(@PathVariable("requirePointId") String requirePointId) {
        if (!StringUtils.hasText(requirePointId)) {
            return Result.error("需求点ID不能为空");
        }

        try {
            TfRequirepoint requirepoint = tfRequirepointService.getById(requirePointId);
            if (requirepoint != null) {
                return Result.success(requirepoint);
            } else {
                return Result.error("需求点不存在");
            }
        } catch (Exception e) {
            return Result.error("查询需求点详情失败：" + e.getMessage());
        }
    }

    /**
     * 创建需求点
     * @param requirepoint 需求点信息
     * @return 创建结果
     */
    @PostMapping("/create")
    public Result<?> createRequirePoint(@RequestBody TfRequirepoint requirepoint) {
        if (requirepoint == null) {
            return Result.error("需求点信息不能为空");
        }

        if (!StringUtils.hasText(requirepoint.getSystemId())) {
            return Result.error("系统ID不能为空");
        }

        if (!StringUtils.hasText(requirepoint.getRequirePointDesc())) {
            return Result.error("需求点概述不能为空");
        }

        try {
            boolean success = tfRequirepointService.createRequirepoint(requirepoint);
            if (success) {
                return Result.success("需求点创建成功");
            } else {
                return Result.error("需求点创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建需求点失败：" + e.getMessage());
        }
    }

    /**
     * 更新需求点
     * @param data 需求点信息
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result<?> updateRequirePoint(@RequestBody Map<String, Object> data) {
        if (data == null) {
            return Result.error("需求点信息不能为空");
        }

        String requirePointId = (String) data.get("requirePointId");
        if (!StringUtils.hasText(requirePointId)) {
            return Result.error("需求点ID不能为空");
        }

        try {
            // 将Map转换为TfRequirepoint对象
            TfRequirepoint requirepoint = new TfRequirepoint();
            requirepoint.setRequirePointId(requirePointId);
            requirepoint.setRequirePointDesc((String) data.get("requirePointDesc"));
            requirepoint.setSystemId((String) data.get("systemId"));
            requirepoint.setDirectoryId((String) data.get("directoryId"));
            requirepoint.setRequirePointType((String) data.get("requirePointType"));
            requirepoint.setReviewStatus((String) data.get("reviewStatus"));
            requirepoint.setAnalysisMethod((String) data.get("analysisMethod"));
            requirepoint.setRequireStatus((String) data.get("requireStatus"));
            requirepoint.setDesignerId((String) data.get("designerId"));
            requirepoint.setModifierId((String) data.get("modifierId"));
            requirepoint.setRemark((String) data.get("remark"));
            requirepoint.setSendTestId((String) data.get("sendTestId"));
            
            Object workPackageIdObj = data.get("workPackageId");
            if (workPackageIdObj != null) {
                if (workPackageIdObj instanceof Integer) {
                    requirepoint.setWorkPackageId((Integer) workPackageIdObj);
                } else if (workPackageIdObj instanceof String) {
                    try {
                        requirepoint.setWorkPackageId(Integer.parseInt((String) workPackageIdObj));
                    } catch (NumberFormatException e) {
                        // 忽略转换错误
                    }
                }
            }

            boolean success = tfRequirepointService.updateRequirepoint(requirepoint);
            if (success) {
                return Result.success("需求点更新成功");
            } else {
                return Result.error("需求点更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新需求点失败：" + e.getMessage());
        }
    }

    /**
     * 删除需求点
     * @param data 包含需求点ID的数据
     * @return 删除结果
     */
    @PostMapping("/delete")
    public Result<?> deleteRequirePoint(@RequestBody Map<String, Object> data) {
        if (data == null) {
            return Result.error("请求数据不能为空");
        }

        String requirePointId = (String) data.get("requirePointId");
        if (!StringUtils.hasText(requirePointId)) {
            return Result.error("需求点ID不能为空");
        }

        try {
            boolean success = tfRequirepointService.deleteRequirepoint(requirePointId);
            if (success) {
                return Result.success("需求点删除成功");
            } else {
                return Result.error("需求点删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除需求点失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除需求点
     * @param data 包含需求点ID列表的数据
     * @return 删除结果
     */
    @PostMapping("/batchDelete")
    public Result<?> batchDeleteRequirePoints(@RequestBody Map<String, Object> data) {
        if (data == null) {
            return Result.error("请求数据不能为空");
        }

        @SuppressWarnings("unchecked")
        List<String> requirePointIds = (List<String>) data.get("requirePointIds");
        
        if (requirePointIds == null || requirePointIds.isEmpty()) {
            return Result.error("需求点ID列表不能为空");
        }

        try {
            boolean success = tfRequirepointService.batchDeleteRequirepoints(requirePointIds);
            if (success) {
                return Result.success("批量删除需求点成功");
            } else {
                return Result.error("批量删除需求点失败");
            }
        } catch (Exception e) {
            return Result.error("批量删除需求点失败：" + e.getMessage());
        }
    }

    /**
     * 批量评审需求点
     * @param data 批量评审数据
     * @return 评审结果
     */
    @PostMapping("/batchReview")
    public Result<?> batchReviewRequirePoints(@RequestBody Map<String, Object> data) {
        if (data == null) {
            return Result.error("请求数据不能为空");
        }

        @SuppressWarnings("unchecked")
        List<String> requirePointIds = (List<String>) data.get("requirePointIds");
        String reviewStatus = (String) data.get("reviewStatus");
        String reviewComment = (String) data.get("reviewComment");
        
        if (requirePointIds == null || requirePointIds.isEmpty()) {
            return Result.error("需求点ID列表不能为空");
        }
        
        if (!StringUtils.hasText(reviewStatus)) {
            return Result.error("评审状态不能为空");
        }

        try {
            boolean success = tfRequirepointService.batchReviewRequirepoints(requirePointIds, reviewStatus, reviewComment);
            if (success) {
                return Result.success("批量评审需求点成功");
            } else {
                return Result.error("批量评审需求点失败");
            }
        } catch (Exception e) {
            return Result.error("批量评审需求点失败：" + e.getMessage());
        }
    }

    /**
     * 导出需求点数据
     * @param data 导出参数
     * @return 导出结果
     */
    @PostMapping("/export")
    public Result<List<TfRequirepoint>> exportRequirePoints(@RequestBody Map<String, Object> data) {
        if (data == null) {
            return Result.error("导出参数不能为空");
        }

        String systemId = (String) data.get("systemId");
        String directoryId = (String) data.get("directoryId");
        String requirePointType = (String) data.get("requirePointType");
        String reviewStatus = (String) data.get("reviewStatus");
        String requireStatus = (String) data.get("requireStatus");
        String designerId = (String) data.get("designerId");

        try {
            List<TfRequirepoint> requirepoints = tfRequirepointService.exportRequirepoints(
                systemId, directoryId, requirePointType, reviewStatus, requireStatus, designerId);
            return Result.success(requirepoints);
        } catch (Exception e) {
            return Result.error("导出需求点数据失败：" + e.getMessage());
        }
    }

    /**
     * 下载导入模板
     * @param response HTTP响应对象
     * @return 模板文件
     */
    @GetMapping("/template")
    public void downloadImportTemplate(HttpServletResponse response) {
        try {
            // 从classpath读取模板文件
            Resource resource = new ClassPathResource("templates/RequireTemplate.xlsx");
            
            if (!resource.exists()) {
                response.getWriter().write("模板文件不存在，请检查文件路径：templates/RequireTemplate.xlsx");
                return;
            }
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=RequireTemplate.xlsx");
            
            // 读取文件并写入响应流
            try (InputStream inputStream = resource.getInputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    response.getOutputStream().write(buffer, 0, bytesRead);
                }
                response.getOutputStream().flush();
            }
            
        } catch (IOException e) {
            try {
                response.getWriter().write("模板下载失败：" + e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
