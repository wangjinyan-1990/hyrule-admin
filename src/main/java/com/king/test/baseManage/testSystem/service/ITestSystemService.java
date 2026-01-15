package com.king.test.baseManage.testSystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.king.test.baseManage.testSystem.entity.TTestSystem;

import java.util.List;

/**
 * 测试系统Service接口
 */
public interface ITestSystemService extends IService<TTestSystem> {
    
    /**
     * 分页查询测试系统列表
     * @param page 分页参数
     * @param systemName 系统名称（模糊查询）
     * @param orgId 机构ID
     * @param systemType 系统类型
     * @param systemStage 系统阶段
     * @param testManagerName 测试经理名称（模糊查询）
     * @param devManagerName 开发经理名称（模糊查询）
     * @return 分页结果
     */
    IPage<TTestSystem> getTestSystemList(Page<TTestSystem> page,
                                        String systemName,
                                        String orgId,
                                        String systemType,
                                        String systemStage,
                                        String testManagerName,
                                        String devManagerName);
    
    /**
     * 获取测试系统详情
     * @param systemId 系统ID
     * @return 测试系统详情
     */
    TTestSystem getTestSystemDetail(String systemId);
    
    /**
     * 创建测试系统
     * @param testSystem 测试系统信息
     * @return 是否创建成功
     */
    boolean createTestSystem(TTestSystem testSystem);
    
    /**
     * 更新测试系统
     * @param testSystem 测试系统信息
     * @return 是否更新成功
     */
    boolean updateTestSystem(TTestSystem testSystem);
    
    /**
     * 删除测试系统
     * @param systemId 系统ID
     * @return 是否删除成功
     */
    boolean deleteTestSystem(String systemId);
    
    /**
     * 批量删除测试系统
     * @param systemIds 系统ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteTestSystem(List<String> systemIds);

    /**
     * 根据系统名称获取系统ID
     * @param systemName 系统名称
     * @return 系统ID，如果不存在则返回null
     */
    String getSystemIdByName(String systemName);
}
