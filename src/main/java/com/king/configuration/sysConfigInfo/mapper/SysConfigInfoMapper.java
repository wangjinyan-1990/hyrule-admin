package com.king.configuration.sysConfigInfo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.king.configuration.sysConfigInfo.entity.TfSystemConfiguration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统配置信息Mapper接口
 * 对应数据库表：tf_system_configuration
 */
@Mapper
public interface SysConfigInfoMapper extends BaseMapper<TfSystemConfiguration> {
    
    /**
     * 根据配置ID查询系统配置信息详情（带关联信息）
     * @param configurationId 配置ID
     * @return 系统配置信息
     */
    TfSystemConfiguration selectSysConfigInfoDetailById(@Param("configurationId") Integer configurationId);
    
    /**
     * 分页查询系统配置信息列表（带系统名称和配置人员名称）
     * @param page 分页对象
     * @param systemName 系统名称（可选，模糊查询）
     * @param configurationPeopleNames 配置人员名称（可选，模糊查询）
     * @return 分页结果
     */
    IPage<TfSystemConfiguration> selectSysConfigInfoListWithSystemName(Page<TfSystemConfiguration> page, 
                                                                      @Param("systemName") String systemName,
                                                                      @Param("configurationPeopleNames") String configurationPeopleNames);
    
    /**
     * 根据系统ID查询系统配置信息列表（带关联信息）
     * @param systemId 系统ID
     * @return 系统配置信息列表
     */
    List<TfSystemConfiguration> selectSysConfigInfoBySystemId(@Param("systemId") String systemId);
    
    /**
     * 查询所有系统配置信息（带关联信息）
     * @return 系统配置信息列表
     */
    List<TfSystemConfiguration> selectAllSysConfigInfo();
    
    /**
     * 根据系统简称查询系统配置信息
     * @param sysAbbreviation 系统简称
     * @return 系统配置信息列表
     */
    List<TfSystemConfiguration> selectSysConfigInfoBySysAbbreviation(@Param("sysAbbreviation") String sysAbbreviation);
}

