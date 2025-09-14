package com.king.sys.org.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.sys.org.entity.TSysOrg;
import com.king.sys.org.mapper.SysOrgMapper;
import com.king.sys.org.service.IOrgService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service("orgService")
public class OrgServiceImpl extends ServiceImpl<SysOrgMapper, TSysOrg> implements IOrgService {

    @Override
    public List<TSysOrg> getOrgTree() {
        List<TSysOrg> allOrgs = baseMapper.selectAllOrgs();
        return buildOrgTree(allOrgs);
    }

    @Override
    public IPage<TSysOrg> getOrgList(Page<TSysOrg> page, String orgName, String orgStatus) {
        return baseMapper.selectOrgPage(page, orgName, orgStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrg(TSysOrg org) {
        // 校验同级机构名称唯一性
        if (isDuplicateOrgName(org.getOrgName(), org.getParentOrgId(), null)) {
            throw new RuntimeException("同级机构名称已存在");
        }
        
        // 自动生成机构ID
        String newOrgId = generateOrgId(org.getParentOrgId());
        org.setOrgId(newOrgId);
        
        // 设置默认值
        if (org.getOrgStatus() == null) {
            org.setOrgStatus("A");
        }
        if (org.getSortNo() == null) {
            org.setSortNo(1);
        }
        
        return save(org);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrg(TSysOrg org) {
        // 校验同级机构名称唯一性（排除自身）
        if (isDuplicateOrgName(org.getOrgName(), org.getParentOrgId(), org.getOrgId())) {
            throw new RuntimeException("同级机构名称已存在");
        }
        
        return updateById(org);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOrg(String orgId) {
        // 检查是否存在子机构
        int childCount = baseMapper.countChildrenByParentId(orgId);
        if (childCount > 0) {
            throw new RuntimeException("该机构下存在子机构，不允许删除");
        }
        
        return removeById(orgId);
    }

    @Override
    public TSysOrg getOrgDetail(String orgId) {
        return baseMapper.selectOrgWithParentName(orgId);
    }

    /**
     * 构建机构树形结构
     */
    private List<TSysOrg> buildOrgTree(List<TSysOrg> allOrgs) {
        if (allOrgs == null || allOrgs.isEmpty()) {
            return new ArrayList<>();
        }

        // 按父机构ID分组
        Map<String, List<TSysOrg>> orgMap = allOrgs.stream()
                .collect(Collectors.groupingBy(org -> 
                    StringUtils.hasText(org.getParentOrgId()) ? org.getParentOrgId() : "ROOT"));

        // 递归构建树形结构
        return buildTreeNodes(orgMap.get("ROOT"), orgMap);
    }

    /**
     * 递归构建树节点
     */
    private List<TSysOrg> buildTreeNodes(List<TSysOrg> parentOrgs, Map<String, List<TSysOrg>> orgMap) {
        if (parentOrgs == null || parentOrgs.isEmpty()) {
            return new ArrayList<>();
        }

        List<TSysOrg> result = new ArrayList<>();
        for (TSysOrg parent : parentOrgs) {
            List<TSysOrg> children = buildTreeNodes(orgMap.get(parent.getOrgId()), orgMap);
            parent.setChildren(children);
            result.add(parent);
        }

        return result;
    }

    /**
     * 检查同级机构名称是否重复
     */
    private boolean isDuplicateOrgName(String orgName, String parentOrgId, String excludeOrgId) {
        int count = baseMapper.countSameLevelOrgName(orgName, parentOrgId, excludeOrgId);
        return count > 0;
    }

    /**
     * 生成机构ID
     * 一级机构：找到所有一级机构中最大的orgId，然后加上1000
     * 子机构：找到同一个父机构下最大的orgId，然后加上1
     */
    private String generateOrgId(String parentOrgId) {
        if (StringUtils.hasText(parentOrgId)) {
            // 子机构：父机构下最大ID + 1
            String maxChildId = baseMapper.getMaxChildOrgId(parentOrgId);
            if (StringUtils.hasText(maxChildId)) {
                try {
                    long maxId = Long.parseLong(maxChildId);
                    return String.valueOf(maxId + 1);
                } catch (NumberFormatException e) {
                    // 如果解析失败，使用父机构ID + "001"
                    return parentOrgId + "001";
                }
            } else {
                // 如果没有子机构，使用父机构ID + "001"
                return parentOrgId + "001";
            }
        } else {
            // 一级机构：所有一级机构中最大ID + 1000
            String maxRootId = baseMapper.getMaxRootOrgId();
            if (StringUtils.hasText(maxRootId)) {
                try {
                    long maxId = Long.parseLong(maxRootId);
                    return String.valueOf(maxId + 1000);
                } catch (NumberFormatException e) {
                    // 如果解析失败，从1000开始
                    return "1000";
                }
            } else {
                // 如果没有一级机构，从1000开始
                return "1000";
            }
        }
    }
}
