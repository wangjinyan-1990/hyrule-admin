package com.king.test.baseManage.testSystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.king.test.baseManage.testSystem.entity.TTestSystem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 测试系统Mapper接口
 */
@Mapper
public interface TestSystemMapper extends BaseMapper<TTestSystem> {
    
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
    IPage<TTestSystem> selectTestSystemPage(Page<TTestSystem> page,
                                           @Param("systemName") String systemName,
                                           @Param("orgId") String orgId,
                                           @Param("systemType") String systemType,
                                           @Param("systemStage") String systemStage,
                                           @Param("testManagerName") String testManagerName,
                                           @Param("devManagerName") String devManagerName);
}
