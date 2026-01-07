package com.king.configuration.sysConfigInfo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.configuration.sysConfigInfo.entity.TfSystemConfiguration;

import java.util.Map;

/**
 * 系统配置信息Service接口
 */
public interface ISysConfigInfoService extends IService<TfSystemConfiguration> {
    
    /**
     * 分页查询系统配置信息列表
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param systemName 系统名称（可选，模糊查询）
     * @param configurationPeopleNames 配置人员名称（可选，模糊查询）
     * @return 分页结果
     */
    Map<String, Object> getSysConfigInfoList(Integer pageNo, Integer pageSize, String systemName, String configurationPeopleNames);
    
    /**
     * 根据配置ID获取系统配置信息详情
     * @param configurationId 配置ID
     * @return 系统配置信息
     */
    TfSystemConfiguration getSysConfigInfoDetail(Integer configurationId);
    
    /**
     * 创建系统配置信息
     * @param sysConfigInfo 系统配置信息
     */
    void createSysConfigInfo(TfSystemConfiguration sysConfigInfo);
    
    /**
     * 更新系统配置信息
     * @param sysConfigInfo 系统配置信息
     */
    void updateSysConfigInfo(TfSystemConfiguration sysConfigInfo);
    
    /**
     * 删除系统配置信息
     * @param configurationId 配置ID
     */
    void deleteSysConfigInfo(Integer configurationId);
}

