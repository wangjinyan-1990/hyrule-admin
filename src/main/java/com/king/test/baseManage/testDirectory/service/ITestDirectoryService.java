package com.king.test.baseManage.testDirectory.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.king.test.baseManage.testDirectory.entity.TTestDirectory;
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
     * @return 子目录列表
     */
    Map<String, Object> getChildrenByParentId(String directoryParentId, String systemId);

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

}
