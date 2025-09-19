package com.king.test.baseManage.testDirectory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.king.test.baseManage.testDirectory.entity.TTestDirectory;
import com.king.test.baseManage.testSystem.entity.TTestSystem;

import java.util.List;
import java.util.Map;

/**
 * 测试目录Service接口
 */
public interface ITestDirectoryService extends IService<TTestDirectory> {

    /**
     * 查询用户参与的测试系统
     * @param userId 用户ID
     * @return
     */
    Map<String, Object> getSystemsByUserId(String userId);

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

}
