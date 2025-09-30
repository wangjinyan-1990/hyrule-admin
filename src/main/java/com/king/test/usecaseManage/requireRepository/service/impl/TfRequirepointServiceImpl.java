package com.king.test.usecaseManage.requireRepository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.utils.CounterUtil;
import com.king.common.utils.SecurityUtils;
import com.king.test.baseManage.testDirectory.entity.TTestDirectory;
import com.king.test.baseManage.testDirectory.service.ITestDirectoryService;
import com.king.test.usecaseManage.requireRepository.entity.TfRequirepoint;
import com.king.test.usecaseManage.requireRepository.mapper.TfRequirepointMapper;
import com.king.test.usecaseManage.requireRepository.service.ITfRequirepointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 需求点Service实现类
 */
@Service("tfRequirepointServiceImpl")
public class TfRequirepointServiceImpl extends ServiceImpl<TfRequirepointMapper, TfRequirepoint> implements ITfRequirepointService {

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    @Qualifier("testDirectoryServiceImpl")
    private ITestDirectoryService testDirectoryService;
    
    @Autowired
    private CounterUtil counterUtil;

    @Override
    public Map<String, Object> getRequirepointsWithPagination(int pageNo, int pageSize, 
                                                            String systemId, String directoryId, 
                                                            String requirePointType, String reviewStatus, 
                                                            String requireStatus, String designer) {
        // 创建分页对象
        Page<TfRequirepoint> page = new Page<>(pageNo, pageSize);
        
        // 获取目录及其子目录ID列表
        List<String> directoryIds = null;
        if (StringUtils.hasText(directoryId)) {
            directoryIds = getDirectoryAndChildrenIds(directoryId);
        }
        
        // 执行分页查询
        Page<TfRequirepoint> result = baseMapper.selectPageRequirepoints(page, systemId, directoryIds,
                                                                                  requirePointType, reviewStatus, 
                                                                                  requireStatus, designer);
        
        // 构建返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("rows", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pageNo", result.getCurrent());
        data.put("pageSize", result.getSize());
        data.put("totalPages", result.getPages());
        
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRequirepoint(TfRequirepoint requirepoint) {
        Assert.notNull(requirepoint, "需求点信息不能为空");
        Assert.hasText(requirepoint.getSystemId(), "系统ID不能为空");
        Assert.hasText(requirepoint.getRequirePointDesc(), "需求点概述不能为空");

        // 如果需求点ID为空，则使用计数器生成
        if (!StringUtils.hasText(requirepoint.getRequirePointId())) {
            String requirePointId = generateRequirePointId(requirepoint.getSystemId());
            requirepoint.setRequirePointId(requirePointId);
        }
        
        // 设置创建时间
        if (requirepoint.getCreateTime() == null) {
            requirepoint.setCreateTime(LocalDateTime.now());
        }
        
        // 设置设计人为当前用户ID
        if (!StringUtils.hasText(requirepoint.getDesignerId())) {
            String currentUserId = securityUtils.getUserId();
            if (StringUtils.hasText(currentUserId)) {
                requirepoint.setDesignerId(currentUserId);
            }
        }
        
        // 设置默认评审状态: 未评审
        if (!StringUtils.hasText(requirepoint.getReviewStatus())) {
            requirepoint.setReviewStatus("0");
        }
        // 设置默认需求点状态: 未覆盖
        if (!StringUtils.hasText(requirepoint.getRequireStatus())) {
            requirepoint.setRequireStatus("1");
        }
        return baseMapper.insert(requirepoint) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRequirepoint(TfRequirepoint requirepoint) {
        Assert.notNull(requirepoint, "需求点信息不能为空");
        Assert.hasText(requirepoint.getRequirePointId(), "需求点ID不能为空");
        Assert.hasText(requirepoint.getSystemId(), "系统ID不能为空");
        Assert.hasText(requirepoint.getRequirePointDesc(), "需求点概述不能为空");

        // 检查需求点是否存在
        TfRequirepoint existingRequirepoint = baseMapper.selectById(requirepoint.getRequirePointId());
        if (existingRequirepoint == null) {
            throw new IllegalArgumentException("需求点不存在");
        }

        // 设置修改时间
        requirepoint.setModifyTime(LocalDateTime.now());

        return baseMapper.updateById(requirepoint) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRequirepoint(String requirePointId) {
        Assert.hasText(requirePointId, "需求点ID不能为空");
        
        // 检查需求点是否存在
        TfRequirepoint requirepoint = baseMapper.selectById(requirePointId);
        if (requirepoint == null) {
            throw new IllegalArgumentException("需求点不存在");
        }

        return baseMapper.deleteById(requirePointId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteRequirepoints(List<String> requirePointIds) {
        Assert.notEmpty(requirePointIds, "需求点ID列表不能为空");
        
        // 检查所有需求点是否存在
        for (String requirePointId : requirePointIds) {
            TfRequirepoint requirepoint = baseMapper.selectById(requirePointId);
            if (requirepoint == null) {
                throw new IllegalArgumentException("需求点不存在：" + requirePointId);
            }
        }
        
        // 批量删除
        return baseMapper.deleteBatchIds(requirePointIds) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchReviewRequirepoints(List<String> requirePointIds, String reviewStatus, String reviewComment) {
        Assert.notEmpty(requirePointIds, "需求点ID列表不能为空");
        Assert.hasText(reviewStatus, "评审状态不能为空");
        
        // 检查所有需求点是否存在
        for (String requirePointId : requirePointIds) {
            TfRequirepoint requirepoint = baseMapper.selectById(requirePointId);
            if (requirepoint == null) {
                throw new IllegalArgumentException("需求点不存在：" + requirePointId);
            }
        }
        
        // 批量更新评审状态
        int successCount = 0;
        for (String requirePointId : requirePointIds) {
            TfRequirepoint requirepoint = new TfRequirepoint();
            requirepoint.setRequirePointId(requirePointId);
            requirepoint.setReviewStatus(reviewStatus);
            requirepoint.setRemark(reviewComment); // 将评审意见存储到备注字段
            requirepoint.setModifyTime(LocalDateTime.now());
            
            if (baseMapper.updateById(requirepoint) > 0) {
                successCount++;
            }
        }
        
        return successCount == requirePointIds.size();
    }

    @Override
    public TfRequirepoint getRequirepointDetailById(String requirePointId) {
        Assert.hasText(requirePointId, "需求点ID不能为空");
        
        // 根据ID查询需求点详情
        TfRequirepoint requirepoint = baseMapper.selectRequirepointDetailById(requirePointId);
        
        if (requirepoint == null) {
            throw new IllegalArgumentException("需求点不存在，ID: " + requirePointId);
        }
        
        return requirepoint;
    }

    @Override
    public List<TfRequirepoint> exportRequirepoints(String systemId, String directoryId, 
                                                   String requirePointType, String reviewStatus, 
                                                   String requireStatus, String designer) {
        // 获取目录及其子目录ID列表
        List<String> directoryIds = null;
        if (StringUtils.hasText(directoryId)) {
            directoryIds = getDirectoryAndChildrenIds(directoryId);
        }
        
        // 查询所有符合条件的数据
        return baseMapper.selectRequirepoints(systemId, directoryIds, requirePointType,
                                                          reviewStatus, requireStatus, designer);
    }

    @Override
    public List<String> getDirectoryAndChildrenIds(String directoryId) {
        List<String> allDirectoryIds = new java.util.ArrayList<>();
        
        if (!StringUtils.hasText(directoryId)) {
            return allDirectoryIds;
        }
        
        // 添加当前目录ID
        allDirectoryIds.add(directoryId);
        
        // 递归获取所有子目录ID
        getAllChildrenDirectoryIds(directoryId, allDirectoryIds);
        
        return allDirectoryIds;
    }
    
    /**
     * 递归获取所有子目录ID
     * @param parentDirectoryId 父目录ID
     * @param allDirectoryIds 所有目录ID列表
     */
    private void getAllChildrenDirectoryIds(String parentDirectoryId, List<String> allDirectoryIds) {
        try {
            // 查询直接子目录
            LambdaQueryWrapper<TTestDirectory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TTestDirectory::getDirectoryParentId, parentDirectoryId);
            List<TTestDirectory> children = testDirectoryService.list(wrapper);
            
            for (TTestDirectory child : children) {
                String childId = child.getDirectoryId();
                allDirectoryIds.add(childId);
                // 递归查询子目录的子目录
                getAllChildrenDirectoryIds(childId, allDirectoryIds);
            }
        } catch (Exception e) {
            // 查询失败时记录日志，但不影响主流程
            System.err.println("查询子目录失败: " + e.getMessage());
        }
    }

    /**
     * 生成需求点ID
     * @param systemId 系统ID
     * @return 需求点ID
     */
    private String generateRequirePointId(String systemId) {
        // 使用计数器生成需求点ID
        String requirePointId = systemId + "-" + counterUtil.generateNextCode("requireCode");
        return requirePointId;
    }
}
