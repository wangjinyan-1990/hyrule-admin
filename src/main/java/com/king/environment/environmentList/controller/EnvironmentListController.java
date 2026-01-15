package com.king.environment.environmentList.controller;

import com.king.common.Result;
import com.king.environment.environmentList.entity.TfEnvironment;
import com.king.environment.environmentList.entity.TfEnvironmentList;
import com.king.environment.environmentList.service.IEnvironmentListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 环境清单Controller
 */
@RestController
@RequestMapping("/env")
public class EnvironmentListController {

    @Autowired
    @Qualifier("environmentListServiceImpl")
    private IEnvironmentListService environmentListService;

    /**
     * 获取环境列表
     * @param testStage 测试阶段（可选，SIT、PAT）
     * @return 环境列表
     */
    @GetMapping("/environment/list")
    public Result<List<TfEnvironment>> getEnvironmentList(
            @RequestParam(value = "testStage", required = false) String testStage) {
        try {
            List<TfEnvironment> list = environmentListService.getEnvironmentList(testStage);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询环境列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取环境清单列表
     * @param envId 环境Id（可选）
     * @param systemName 系统名称（可选，模糊查询）
     * @param serverName 服务名称（可选，模糊查询）
     * @param ipAddress 主机地址（可选，模糊查询）
     * @return 环境清单列表
     */
    @GetMapping("/environmentList/list")
    public Result<List<TfEnvironmentList>> getEnvironmentListList(
            @RequestParam(value = "envId", required = false) Integer envId,
            @RequestParam(value = "systemName", required = false) String systemName,
            @RequestParam(value = "serverName", required = false) String serverName,
            @RequestParam(value = "ipAddress", required = false) String ipAddress) {
        try {
            List<TfEnvironmentList> list = environmentListService.getEnvironmentListList(envId, systemName, serverName, ipAddress);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询环境清单列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取环境清单详情
     * @param envListId 环境清单Id
     * @return 环境清单详情
     */
    @GetMapping("/environmentList/{envListId}")
    public Result<TfEnvironmentList> getEnvironmentListDetail(@PathVariable("envListId") Integer envListId) {
        try {
            if (envListId == null) {
                return Result.error("环境清单Id不能为空");
            }
            TfEnvironmentList environmentList = environmentListService.getEnvironmentListDetail(envListId);
            if (environmentList == null) {
                return Result.error("环境清单不存在");
            }
            return Result.success(environmentList);
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("获取环境清单详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建环境清单
     * @param environmentList 环境清单信息
     * @return 创建结果
     */
    @PostMapping("/environmentList")
    public Result<?> createEnvironmentList(@RequestBody TfEnvironmentList environmentList) {
        try {
            if (environmentList == null) {
                return Result.error("环境清单信息不能为空");
            }
            boolean success = environmentListService.createEnvironmentList(environmentList);
            return success ? Result.success("创建成功") : Result.error("创建失败");
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("创建环境清单失败: " + e.getMessage());
        }
    }

    /**
     * 更新环境清单
     * @param environmentList 环境清单信息
     * @return 更新结果
     */
    @PutMapping("/environmentList")
    public Result<?> updateEnvironmentList(@RequestBody TfEnvironmentList environmentList) {
        try {
            if (environmentList == null) {
                return Result.error("环境清单信息不能为空");
            }
            if (environmentList.getEnvListId() == null) {
                return Result.error("环境清单Id不能为空");
            }
            boolean success = environmentListService.updateEnvironmentList(environmentList);
            return success ? Result.success("更新成功") : Result.error("更新失败");
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("更新环境清单失败: " + e.getMessage());
        }
    }

    /**
     * 删除环境清单
     * @param envListId 环境清单Id
     * @return 删除结果
     */
    @DeleteMapping("/environmentList/{envListId}")
    public Result<?> deleteEnvironmentList(@PathVariable("envListId") Integer envListId) {
        try {
            if (envListId == null) {
                return Result.error("环境清单Id不能为空");
            }
            boolean success = environmentListService.deleteEnvironmentList(envListId);
            return success ? Result.success("删除成功") : Result.error("删除失败");
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("删除环境清单失败: " + e.getMessage());
        }
    }

    /**
     * 导出环境清单数据
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     */
    @GetMapping("/environmentList/export")
    public void exportEnvironmentList(
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            // 从请求参数中获取值，避免 Spring 参数绑定问题
            String envIdStr = request.getParameter("envId");
            String systemName = request.getParameter("systemName");
            String serverName = request.getParameter("serverName");
            String ipAddress = request.getParameter("ipAddress");

            // 安全地解析 envId
            Integer envId = null;
            if (envIdStr != null && !envIdStr.trim().isEmpty()) {
                try {
                    envId = Integer.valueOf(envIdStr.trim());
                } catch (NumberFormatException e) {
                    // envId 格式错误，忽略该参数
                }
            }

            if (systemName != null && systemName.trim().isEmpty()) {
                systemName = null;
            }
            if (serverName != null && serverName.trim().isEmpty()) {
                serverName = null;
            }
            if (ipAddress != null && ipAddress.trim().isEmpty()) {
                ipAddress = null;
            }

            // 验证：必须至少指定环境（envId）、系统名称（systemName）或服务名称（serverName）中的一项，不能全量导出
            boolean hasEnvId = envId != null;
            boolean hasSystemName = systemName != null && !systemName.isEmpty();
            boolean hasServerName = serverName != null && !serverName.isEmpty();

            if (!hasEnvId && !hasSystemName && !hasServerName) {
                if (!response.isCommitted()) {
                    response.reset();
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":400,\"message\":\"导出失败,至少指定环境、系统名称或服务名称中的一项，不允许全量导出\"}");
                }
                return;
            }

            List<TfEnvironmentList> list = environmentListService.getEnvironmentListList(envId, systemName, serverName, ipAddress);
            environmentListService.exportEnvironmentListToExcel(list, response);
        } catch (Exception e) {
            try {
                if (!response.isCommitted()) {
                    response.reset();
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":500,\"message\":\"导出失败：" + e.getMessage().replace("\"", "\\\"") + "\"}");
                }
            } catch (Exception ignored) {
                // 如果响应已提交或写入失败，忽略异常
            }
        }
    }

    /**
     * 导入环境清单数据
     * @param file Excel文件
     * @return 导入结果
     */
    @PostMapping("/environmentList/import")
    public Result<Map<String, Object>> importEnvironmentList(
            @RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> result = environmentListService.importEnvironmentList(file);
            return Result.success(result, "导入完成");
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("导入失败：" + e.getMessage());
        }
    }

    /**
     * 下载导入模板
     * @param response HTTP响应对象
     */
    @GetMapping("/environmentList/template")
    public void downloadTemplate(HttpServletResponse response) {
        try {
            environmentListService.downloadTemplate(response);
        } catch (Exception e) {
            try {
                response.reset();
                response.getWriter().write("下载模板失败：" + e.getMessage());
            } catch (Exception ignored) {
            }
        }
    }
}
