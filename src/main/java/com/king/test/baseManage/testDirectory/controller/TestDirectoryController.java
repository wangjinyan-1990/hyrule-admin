package com.king.test.baseManage.testDirectory.controller;

import com.king.common.Result;
import com.king.test.baseManage.testDirectory.entity.TTestDirectory;
import com.king.test.baseManage.testDirectory.service.ITestDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 测试系统Controller
 */
@RestController
@RequestMapping("/test/directory")
public class TestDirectoryController {

    @Autowired
    @Qualifier("testDirectoryServiceImpl")
    private ITestDirectoryService testDirectoryService;

    /**
     * 根据用户ID获取该用户所参与的系统，作为测试目录树的根目录展示
     * 每个系统只出现一次
     * @param userId 用户ID
     * @return 系统信息列表
     */
    @GetMapping("/getRootDirectoryByUserId")
    public Result<Map<String, Object>> getRootDirectoryByUserId(@RequestParam("userId") String userId) {
        if (!StringUtils.hasText(userId)) {
            return Result.error("用户ID不能为空");
        }

        try {
            Map<String, Object> data = testDirectoryService.getRootDirectoryByUserId(userId);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("查询用户参与的系统列表失败：" + e.getMessage());
        }
    }

    /**
     * 创建测试目录
     * @param directory 目录信息
     * @return 创建结果
     */
    @PostMapping("/create")
    public Result<?> createDirectory(@RequestBody TTestDirectory directory) {
        if (directory == null) {
            return Result.error("目录信息不能为空");
        }
        
        if (!StringUtils.hasText(directory.getDirectoryName())) {
            return Result.error("目录名称不能为空");
        }
        
        if (!StringUtils.hasText(directory.getSystemId())) {
            return Result.error("系统ID不能为空");
        }
        
        try {
            boolean success = testDirectoryService.createDirectory(directory);
            if (success) {
                return Result.success("目录创建成功");
            } else {
                return Result.error("目录创建失败");
            }
        } catch (Exception e) {
            return Result.error("创建目录失败：" + e.getMessage());
        }
    }

    /**
     * 根据父目录ID和系统ID查询子目录
     * @param directoryParentId 父目录ID（可为空）
     * @param systemId 系统ID
     * @param module 模块类型（可选）：isUseTestset-只查找IS_USE_TESTSET=1的目录；isUseTestcase-只查找IS_USE_TESTCASE=1的目录
     * @return 子目录列表
     */
    @GetMapping("/getChildrenByParentId")
    public Result<Map<String, Object>> getChildrenByParentId(@RequestParam(value = "directoryParentId", required = false) String directoryParentId,
                                                           @RequestParam("systemId") String systemId,
                                                           @RequestParam(value = "module", required = false) String module) {
        if (!StringUtils.hasText(systemId)) {
            return Result.error("系统ID不能为空");
        }
        
        try {
            Map<String, Object> data = testDirectoryService.getChildrenByParentId(directoryParentId, systemId, module);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("查询子目录失败：" + e.getMessage());
        }
    }

    /**
     * 更新测试目录
     * @param directory 目录信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public Result<?> updateDirectory(@RequestBody TTestDirectory directory) {
        if (directory == null) {
            return Result.error("目录信息不能为空");
        }
        
        if (!StringUtils.hasText(directory.getDirectoryId())) {
            return Result.error("目录ID不能为空");
        }
        
        if (!StringUtils.hasText(directory.getDirectoryName())) {
            return Result.error("目录名称不能为空");
        }
        
        if (!StringUtils.hasText(directory.getSystemId())) {
            return Result.error("系统ID不能为空");
        }
        
        try {
            boolean success = testDirectoryService.updateDirectory(directory);
            if (success) {
                return Result.success("目录更新成功");
            } else {
                return Result.error("目录更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新目录失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除测试目录
     * @param directoryId 目录ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{directoryId}")
    public Result<?> deleteDirectory(@PathVariable("directoryId") String directoryId) {
        if (!StringUtils.hasText(directoryId)) {
            return Result.error("目录ID不能为空");
        }
        
        try {
            boolean success = testDirectoryService.deleteDirectory(directoryId);
            if (success) {
                return Result.success("目录删除成功");
            } else {
                return Result.error("目录删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除目录失败：" + e.getMessage());
        }
    }

    /**
     * 根据目录ID获取目录详情（包含完整路径）
     * @param directoryId 目录ID
     * @return 目录详情
     */
    @GetMapping("/{directoryId}")
    public Result<TTestDirectory> getDirectoryById(@PathVariable("directoryId") String directoryId) {
        if (!StringUtils.hasText(directoryId)) {
            return Result.error("目录ID不能为空");
        }
        
        try {
            TTestDirectory directory = testDirectoryService.getDirectoryById(directoryId);
            return Result.success(directory);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询目录详情失败：" + e.getMessage());
        }
    }

    /**
     * 导出目录数据
     * @param params 导出参数（包含systemId等）
     * @param response HTTP响应对象
     */
    @PostMapping("/export")
    public void exportDirectory(@RequestBody(required = false) Map<String, Object> params, 
                               HttpServletResponse response) {
        try {
            testDirectoryService.exportDirectory(params, response);
        } catch (Exception e) {
            try {
                response.reset();
                response.getWriter().write("导出失败：" + e.getMessage());
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 下载导入模板
     * @param response HTTP响应对象
     */
    @GetMapping("/template")
    public void downloadImportTemplate(HttpServletResponse response) {
        try {
            testDirectoryService.downloadImportTemplate(response);
        } catch (Exception e) {
            try {
                response.reset();
                response.getWriter().write("下载模板失败：" + e.getMessage());
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 导入目录数据
     * @param file 上传的Excel文件
     * @param systemId 系统ID（必填）
     * @return 导入结果
     */
    @PostMapping("/import")
    public Result<?> importDirectory(@RequestParam("file") MultipartFile file,
                                    @RequestParam("systemId") String systemId) {
        if (file == null || file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        if (!StringUtils.hasText(systemId)) {
            return Result.error("系统ID不能为空");
        }

        try {
            Map<String, Object> result = testDirectoryService.importDirectory(file, systemId);
            Boolean success = (Boolean) result.get("success");
            if (success != null && success) {
                return Result.success(result, (String) result.get("message"));
            } else {
                return Result.error((String) result.get("message"));
            }
        } catch (Exception e) {
            return Result.error("导入目录数据失败：" + e.getMessage());
        }
    }

}
