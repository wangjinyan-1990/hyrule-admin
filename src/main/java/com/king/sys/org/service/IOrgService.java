package com.king.sys.org.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.king.sys.org.entity.TSysOrg;

import java.util.List;

public interface IOrgService extends IService<TSysOrg> {
    
    /**
     * 获取机构树形列表
     */
    List<TSysOrg> getOrgTree();
    
    /**
     * 分页查询机构列表
     */
    IPage<TSysOrg> getOrgList(Page<TSysOrg> page, String orgName, String orgStatus);
    
    /**
     * 创建机构
     */
    boolean createOrg(TSysOrg org);
    
    /**
     * 更新机构
     */
    boolean updateOrg(TSysOrg org);
    
    /**
     * 删除机构
     */
    boolean deleteOrg(String orgId);
    
    /**
     * 获取机构详情
     */
    TSysOrg getOrgDetail(String orgId);
}
