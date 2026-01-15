package com.king.test.baseManage.testSystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.utils.CounterUtil;
import com.king.common.utils.SecurityUtils;
import com.king.test.baseManage.testSystem.entity.TTestSystem;
import com.king.test.baseManage.testSystem.mapper.TestSystemMapper;
import com.king.test.baseManage.testSystem.service.ITestSystemService;
import com.king.test.baseManage.testSystem.service.ITestSystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 测试系统Service实现类
 */
@Service("testSystemServiceImpl")
public class TestSystemServiceImpl extends ServiceImpl<TestSystemMapper, TTestSystem> implements ITestSystemService {
    
    @Autowired
    @Qualifier("testSystemUserServiceImpl")
    private ITestSystemUserService testSystemUserService;
    
    @Autowired
    private CounterUtil counterUtil;
    
    @Autowired
    private SecurityUtils securityUtils;
    
    @Override
    public IPage<TTestSystem> getTestSystemList(Page<TTestSystem> page,
                                               String systemName,
                                               String orgId,
                                               String systemType,
                                               String systemStage,
                                               String testManagerName,
                                               String devManagerName) {
        return baseMapper.selectTestSystemPage(page, systemName, orgId, systemType, systemStage, testManagerName, devManagerName);
    }
    
    @Override
    public TTestSystem getTestSystemDetail(String systemId) {
        Assert.hasText(systemId, "系统ID不能为空");
        return baseMapper.selectById(systemId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createTestSystem(TTestSystem testSystem) {
        Assert.notNull(testSystem, "测试系统信息不能为空");
        Assert.hasText(testSystem.getSystemName(), "系统名称不能为空");
        
        // 生成系统ID（可以根据实际业务需求调整生成规则）
        if (!StringUtils.hasText(testSystem.getSystemId())) {
            String systemId = generateSystemId();
            testSystem.setSystemId(systemId);
        }
        
        // 设置创建时间和修改时间
        Date now = new Date();
        // 设置创建人
        testSystem.setCreatorId(securityUtils.getUserId());
        testSystem.setCreateTime(now);
        testSystem.setModifyTime(now);
        testSystem.setIsUse("1"); // 默认在使用
        
        // 保存测试系统基本信息
        boolean result = baseMapper.insert(testSystem) > 0;
        
        // 保存测试系统用户关系（测试经理和开发经理）
        if (result) {
            saveSystemUserRelations(testSystem);
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTestSystem(TTestSystem testSystem) {
        Assert.notNull(testSystem, "测试系统信息不能为空");
        Assert.hasText(testSystem.getSystemId(), "系统ID不能为空");
        Assert.hasText(testSystem.getSystemName(), "系统名称不能为空");
        
        // 检查系统是否存在
        TTestSystem existingSystem = baseMapper.selectById(testSystem.getSystemId());
        Assert.notNull(existingSystem, "测试系统不存在");
        
        // 设置修改时间
        testSystem.setModifyTime(new Date());
        
        // 更新测试系统基本信息
        boolean result = baseMapper.updateById(testSystem) > 0;
        
        // 更新测试系统用户关系（测试经理和开发经理）
        if (result) {
            saveSystemUserRelations(testSystem);
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTestSystem(String systemId) {
        Assert.hasText(systemId, "系统ID不能为空");
        
        // 检查系统是否存在
        TTestSystem existingSystem = baseMapper.selectById(systemId);
        Assert.notNull(existingSystem, "测试系统不存在");
        
        // 先删除测试系统用户关系
        testSystemUserService.removeAllUsersBySystemId(systemId);
        
        // 再删除测试系统
        return baseMapper.deleteById(systemId) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteTestSystem(List<String> systemIds) {
        Assert.notEmpty(systemIds, "系统ID列表不能为空");
        
        // 检查所有系统是否存在
        for (String systemId : systemIds) {
            TTestSystem existingSystem = baseMapper.selectById(systemId);
            Assert.notNull(existingSystem, "测试系统不存在: " + systemId);
        }
        
        return baseMapper.deleteBatchIds(systemIds) > 0;
    }
    
    /**
     * 生成系统ID
     * @return 系统ID
     */
    private String generateSystemId() {
        // 使用计数器生成系统ID
        return counterUtil.generateNextCode("sysCode");
    }
    
    /**
     * 保存测试系统用户关系
     * @param testSystem 测试系统信息
     */
    private void saveSystemUserRelations(TTestSystem testSystem) {
        List<String> userIds = new ArrayList<>();

        // 添加测试经理
        if (StringUtils.hasText(testSystem.getSystemTestManagerId())) {
            userIds.add(testSystem.getSystemTestManagerId());
        }

        // 添加开发经理
        if (StringUtils.hasText(testSystem.getSystemDevManagerId())) {
            userIds.add(testSystem.getSystemDevManagerId());
        }

        // 如果有用户ID，则保存用户关系
        if (!userIds.isEmpty()) {
            testSystemUserService.saveOrUpdateSystemUsers(testSystem.getSystemId(), userIds);
        }
    }

    /**
     * 根据系统名称获取系统ID
     * @param systemName 系统名称
     * @return 系统ID，如果不存在则返回null
     */
    @Override
    public String getSystemIdByName(String systemName) {
        if (!StringUtils.hasText(systemName)) {
            return null;
        }
        LambdaQueryWrapper<TTestSystem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TTestSystem::getSystemName, systemName);
        wrapper.eq(TTestSystem::getIsUse, "1");
        TTestSystem system = this.getOne(wrapper);
        return system != null ? system.getSystemId() : null;
    }
}
