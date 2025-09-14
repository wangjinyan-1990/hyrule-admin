package com.king.sys.org.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.king.sys.org.entity.TSysOrg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysOrgMapper extends BaseMapper<TSysOrg> {
    
    /**
     * 查询所有机构（树形结构用）
     */
    List<TSysOrg> selectAllOrgs();
    
    /**
     * 分页查询机构列表
     */
    IPage<TSysOrg> selectOrgPage(Page<TSysOrg> page,
                                 @Param("orgName") String orgName,
                                 @Param("orgStatus") String orgStatus);
    
    /**
     * 根据父机构ID查询子机构数量
     */
    int countChildrenByParentId(@Param("parentOrgId") String parentOrgId);
    
    /**
     * 检查同级机构名称是否重复
     */
    int countSameLevelOrgName(@Param("orgName") String orgName, 
                              @Param("parentOrgId") String parentOrgId, 
                              @Param("excludeOrgId") String excludeOrgId);
    
    /**
     * 获取所有一级机构中最大的orgId
     */
    String getMaxRootOrgId();
    
    /**
     * 获取指定父机构下最大的orgId
     */
    String getMaxChildOrgId(@Param("parentOrgId") String parentOrgId);
    
    /**
     * 根据机构ID查询机构详情（包含上级机构名称）
     */
    TSysOrg selectOrgWithParentName(@Param("orgId") String orgId);
}
