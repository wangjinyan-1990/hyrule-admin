package com.king.environment.environmentList.controller;

import com.king.common.Result;
import com.king.environment.environmentList.entity.TfEnvironment;
import com.king.environment.environmentList.entity.TfEnvironmentList;
import com.king.environment.environmentList.service.IEnvironmentListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * @param systemId 系统ID（可选）
     * @param serverName 服务名称（可选，模糊查询）
     * @return 环境清单列表
     */
    @GetMapping("/environmentList/list")
    public Result<List<TfEnvironmentList>> getEnvironmentListList(
            @RequestParam(value = "envId", required = false) Integer envId,
            @RequestParam(value = "systemId", required = false) String systemId,
            @RequestParam(value = "serverName", required = false) String serverName) {
        try {
            List<TfEnvironmentList> list = environmentListService.getEnvironmentListList(envId, systemId, serverName);
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
}
