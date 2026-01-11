package com.king.configuration.sysConfigInfo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.configuration.sysConfigInfo.entity.TfSystemConfiguration;
import com.king.configuration.sysConfigInfo.mapper.SysConfigInfoMapper;
import com.king.configuration.sysConfigInfo.service.ISysConfigInfoService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统配置信息Service实现类
 */
@Service("sysConfigInfoServiceImpl")
public class SysConfigInfoServiceImpl extends ServiceImpl<SysConfigInfoMapper, TfSystemConfiguration> implements ISysConfigInfoService {

    @Override
    public Map<String, Object> getSysConfigInfoList(Integer pageNo, Integer pageSize, String systemName, String configurationPeopleNames) {
        // 设置默认分页参数
        if (pageNo == null || pageNo < 1) {
            pageNo = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        
        // 使用自定义查询方法，包含系统名称和配置人员名称
        Page<TfSystemConfiguration> page = new Page<>(pageNo, pageSize);
        IPage<TfSystemConfiguration> result = this.baseMapper.selectSysConfigInfoListWithSystemName(page, systemName, configurationPeopleNames);
        
        // 构建返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("rows", result.getRecords());
        
        return data;
    }

    @Override
    public TfSystemConfiguration getSysConfigInfoDetail(Integer configurationId) {
        Assert.notNull(configurationId, "配置ID不能为空");
        
        // 使用自定义查询方法，包含系统名称和配置人员名称
        TfSystemConfiguration config = this.baseMapper.selectSysConfigInfoDetailById(configurationId);
        Assert.notNull(config, "系统配置信息不存在");
        
        return config;
    }

    @Override
    public void createSysConfigInfo(TfSystemConfiguration sysConfigInfo) {
        Assert.notNull(sysConfigInfo, "系统配置信息不能为空");
        
        // 校验必填字段
        Assert.isTrue(StringUtils.hasText(sysConfigInfo.getSystemId()), "系统ID不能为空");
        
        // 如果系统简称不为空，检查是否重复
        if (StringUtils.hasText(sysConfigInfo.getSysAbbreviation())) {
            List<TfSystemConfiguration> existingConfigs = this.baseMapper.selectSysConfigInfoBySysAbbreviation(
                    sysConfigInfo.getSysAbbreviation());
            Assert.isTrue(existingConfigs == null || existingConfigs.isEmpty(), 
                    "系统简称 " + sysConfigInfo.getSysAbbreviation() + " 已存在，不能重复");
        }
        
        // 保存配置信息
        this.save(sysConfigInfo);
    }

    @Override
    public void updateSysConfigInfo(TfSystemConfiguration sysConfigInfo) {
        Assert.notNull(sysConfigInfo, "系统配置信息不能为空");
        Assert.notNull(sysConfigInfo.getConfigurationId(), "配置ID不能为空");
        
        // 检查配置是否存在
        TfSystemConfiguration existingConfig = this.getById(sysConfigInfo.getConfigurationId());
        Assert.notNull(existingConfig, "系统配置信息不存在");
        
        // 校验必填字段
        Assert.isTrue(StringUtils.hasText(sysConfigInfo.getSystemId()), "系统ID不能为空");
        
        // 如果系统简称不为空且与原有值不同，检查是否被其他记录使用
        if (StringUtils.hasText(sysConfigInfo.getSysAbbreviation())) {
            // 如果系统简称发生了变化，需要检查是否重复
            if (!sysConfigInfo.getSysAbbreviation().equals(existingConfig.getSysAbbreviation())) {
                List<TfSystemConfiguration> existingConfigs = this.baseMapper.selectSysConfigInfoBySysAbbreviation(
                        sysConfigInfo.getSysAbbreviation());
                // 检查是否有其他记录（排除当前记录）使用了相同的系统简称
                if (existingConfigs != null && !existingConfigs.isEmpty()) {
                    boolean isDuplicate = existingConfigs.stream()
                            .anyMatch(config -> !config.getConfigurationId().equals(sysConfigInfo.getConfigurationId()));
                    Assert.isTrue(!isDuplicate, 
                            "系统简称 " + sysConfigInfo.getSysAbbreviation() + " 已被其他记录使用，不能重复");
                }
            }
        }
        
        // 更新配置信息
        this.updateById(sysConfigInfo);
    }

    @Override
    public void deleteSysConfigInfo(Integer configurationId) {
        Assert.notNull(configurationId, "配置ID不能为空");
        
        // 检查配置是否存在
        TfSystemConfiguration existingConfig = this.getById(configurationId);
        Assert.notNull(existingConfig, "系统配置信息不存在");
        
        // 删除配置信息
        this.removeById(configurationId);
    }
}

