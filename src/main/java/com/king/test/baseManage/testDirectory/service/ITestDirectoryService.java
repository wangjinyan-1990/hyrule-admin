package com.king.test.baseManage.testDirectory.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.king.test.baseManage.testDirectory.entity.TTestDirectory;
import java.util.List;
import java.util.Map;

/**
 * 测试目录Service接口
 */
public interface ITestDirectoryService extends IService<TTestDirectory> {

    /**
     * 查询用户参与的测试系统,目录树展示系统根目录
     * @param userId 用户ID
     * @return
     */
    Map<String, Object> getRootDirectoryByUserId(String userId);

    /**
     * 创建测试目录
     * @param directory 目录信息
     * @return 是否创建成功
     */
    boolean createDirectory(TTestDirectory directory);

    /**
     * 根据父目录ID和系统ID查询子目录
     * @param directoryParentId 父目录ID（可为空）
     * @param systemId 系统ID
     * @param module 模块类型（可选）：isUseTestset-只查找IS_USE_TESTSET=1的目录；isUseTestcase-只查找IS_USE_TESTCASE=1的目录
     * @return 子目录列表
     */
    Map<String, Object> getChildrenByParentId(String directoryParentId, String systemId, String module);

    /**
     * 更新测试目录
     * @param directory 目录信息
     * @return 是否更新成功
     */
    boolean updateDirectory(TTestDirectory directory);

    /**
     * 删除测试目录
     * @param directoryId 目录ID
     * @return 是否删除成功
     */
    boolean deleteDirectory(String directoryId);

    /**
     * 根据完整路径查找目录ID
     * @param fullPath 完整路径
     * @return 目录ID，如果未找到返回null
     */
    String getDirectoryIdByFullPath(String fullPath);

    /**
     * 根据目录ID获取完整路径
     * @param directoryId 目录ID
     * @return 完整路径
     */
    String getDirectoryFullPath(String directoryId);

    /**
     * 递归获取所有子目录ID（包含当前目录）
     * @param directoryId 目录ID
     * @param systemId 系统ID
     * @return 包含当前目录及其所有子目录的ID列表
     */
    List<String> getAllChildrenDirectoryIds(String directoryId, String systemId);

    /**
     * 根据目录ID获取目录详情（包含完整路径）
     * @param directoryId 目录ID
     * @return 目录详情
     */
    TTestDirectory getDirectoryById(String directoryId);

    /**
     * 导出目录数据到Excel
     * @param params 导出参数（包含systemId等）
     * @param response HTTP响应对象
     */
    void exportDirectory(Map<String, Object> params, javax.servlet.http.HttpServletResponse response);

    /**
     * 下载导入模板
     * @param response HTTP响应对象
     */
    void downloadImportTemplate(javax.servlet.http.HttpServletResponse response);

    /**
     * 导入目录数据
     * @param file Excel文件
     * @param systemId 系统ID（必填）
     * @return 导入结果
     */
    Map<String, Object> importDirectory(org.springframework.web.multipart.MultipartFile file, String systemId);

}
