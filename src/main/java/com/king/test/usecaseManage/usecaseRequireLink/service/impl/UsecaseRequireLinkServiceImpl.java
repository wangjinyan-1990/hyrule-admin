package com.king.test.usecaseManage.usecaseRequireLink.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.test.usecaseManage.requireRepository.entity.TfRequirepoint;
import com.king.test.usecaseManage.requireRepository.service.ITfRequirepointService;
import com.king.test.usecaseManage.usecaseRepository.entity.TfUsecase;
import com.king.test.usecaseManage.usecaseRequireLink.entity.TfUsecaseRequire;
import com.king.test.usecaseManage.usecaseRequireLink.mapper.TfUsecaseRequireLinkMapper;
import com.king.test.usecaseManage.usecaseRequireLink.service.IUsecaseRequireLinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用例和需求点关联Service实现类
 */
@Service("usecaseRequireLinkServiceImpl")
public class UsecaseRequireLinkServiceImpl extends ServiceImpl<TfUsecaseRequireLinkMapper, TfUsecaseRequire> implements IUsecaseRequireLinkService {

    private static final Logger logger = LoggerFactory.getLogger(UsecaseRequireLinkServiceImpl.class);

    @Autowired
    @Qualifier("tfRequirepointServiceImpl")
    private ITfRequirepointService requirePointService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean linkUsecaseToRequirePoint(String usecaseId, String requirePointId) {
        if (!StringUtils.hasText(usecaseId) || !StringUtils.hasText(requirePointId)) {
            logger.warn("关联用例和需求点失败: 参数为空, usecaseId={}, requirePointId={}", usecaseId, requirePointId);
            return false;
        }

        // 验证需求点是否存在
        TfRequirepoint requirePoint = requirePointService.getById(requirePointId);
        if (requirePoint == null) {
            logger.warn("关联用例和需求点失败: 需求点不存在, requirePointId={}", requirePointId);
            return false;
        }

        // 检查是否已经关联
        List<String> existing = this.baseMapper.selectRequirePointIdsByUsecaseId(usecaseId);
        if (existing.contains(requirePointId)) {
            logger.debug("用例和需求点已关联, usecaseId={}, requirePointId={}", usecaseId, requirePointId);
            return true;
        }

        // 创建关联关系
        TfUsecaseRequire link = new TfUsecaseRequire(usecaseId, requirePointId);
        int result = this.baseMapper.insert(link);
        
        if (result > 0) {
            logger.info("关联用例和需求点成功: usecaseId={}, requirePointId={}", usecaseId, requirePointId);
            return true;
        } else {
            logger.error("关联用例和需求点失败: 插入数据库失败, usecaseId={}, requirePointId={}", usecaseId, requirePointId);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean linkUsecaseToRequirePoints(String usecaseId, List<String> requirePointIds) {
        if (!StringUtils.hasText(usecaseId) || CollectionUtils.isEmpty(requirePointIds)) {
            logger.warn("批量关联用例和需求点失败: 参数为空, usecaseId={}, requirePointIds={}", usecaseId, requirePointIds);
            return false;
        }

        // 过滤掉空值
        List<String> validRequirePointIds = requirePointIds.stream()
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());

        if (validRequirePointIds.isEmpty()) {
            logger.warn("批量关联用例和需求点失败: 有效的需求点ID列表为空, usecaseId={}", usecaseId);
            return false;
        }

        // 获取已存在的关联
        List<String> existing = this.baseMapper.selectRequirePointIdsByUsecaseId(usecaseId);
        
        // 过滤出需要新增的关联
        List<String> toInsert = validRequirePointIds.stream()
                .filter(id -> !existing.contains(id))
                .collect(Collectors.toList());

        if (toInsert.isEmpty()) {
            logger.debug("所有需求点已关联, usecaseId={}", usecaseId);
            return true;
        }

        // 验证需求点是否存在
        List<String> notExistIds = new ArrayList<>();
        for (String requirePointId : toInsert) {
            TfRequirepoint requirePoint = requirePointService.getById(requirePointId);
            if (requirePoint == null) {
                notExistIds.add(requirePointId);
            }
        }

        if (!notExistIds.isEmpty()) {
            logger.warn("批量关联用例和需求点失败: 部分需求点不存在, usecaseId={}, notExistIds={}", usecaseId, notExistIds);
            // 移除不存在的需求点ID
            toInsert.removeAll(notExistIds);
        }

        if (toInsert.isEmpty()) {
            logger.warn("批量关联用例和需求点失败: 所有需求点都不存在, usecaseId={}", usecaseId);
            return false;
        }

        // 批量插入关联关系
        int successCount = 0;
        for (String requirePointId : toInsert) {
            TfUsecaseRequire link = new TfUsecaseRequire(usecaseId, requirePointId);
            int result = this.baseMapper.insert(link);
            if (result > 0) {
                successCount++;
            }
        }

        logger.info("批量关联用例和需求点完成: usecaseId={}, 成功={}, 总数={}", usecaseId, successCount, toInsert.size());
        return successCount == toInsert.size();
    }

    @Override
    public List<TfUsecase> getUsecasesByRequirePointId(String requirePointId) {
        if (!StringUtils.hasText(requirePointId)) {
            logger.warn("根据需求点ID查询用例失败: 需求点ID为空");
            return new ArrayList<>();
        }
        
        List<TfUsecase> usecases = this.baseMapper.selectUsecasesByRequirePointId(requirePointId);
        logger.info("根据需求点ID查询用例成功: requirePointId={}, 用例数量={}", requirePointId, usecases != null ? usecases.size() : 0);
        return usecases != null ? usecases : new ArrayList<>();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlinkTestCasesFromRequirePoint(String requirePointId, List<String> usecaseIds) {
        if (!StringUtils.hasText(requirePointId)) {
            logger.warn("取消关联测试用例失败: 需求点ID为空");
            return false;
        }
        
        if (CollectionUtils.isEmpty(usecaseIds)) {
            logger.warn("取消关联测试用例失败: 用例ID列表为空, requirePointId={}", requirePointId);
            return false;
        }

        // 过滤掉空值
        List<String> validUsecaseIds = usecaseIds.stream()
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());

        if (validUsecaseIds.isEmpty()) {
            logger.warn("取消关联测试用例失败: 有效的用例ID列表为空, requirePointId={}", requirePointId);
            return false;
        }

        // 删除关联关系
        int deletedCount = this.baseMapper.deleteByRequirePointIdAndUsecaseIds(requirePointId, validUsecaseIds);
        
        if (deletedCount > 0) {
            logger.info("取消关联测试用例成功: requirePointId={}, 用例数量={}, 删除记录数={}", 
                    requirePointId, validUsecaseIds.size(), deletedCount);
            return true;
        } else {
            logger.warn("取消关联测试用例失败: 未找到需要删除的关联记录, requirePointId={}, usecaseIds={}", 
                    requirePointId, validUsecaseIds);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean linkTestCasesToRequirePoint(String requirePointId, List<String> usecaseIds) {
        if (!StringUtils.hasText(requirePointId)) {
            logger.warn("关联测试用例到需求点失败: 需求点ID为空");
            return false;
        }
        
        if (CollectionUtils.isEmpty(usecaseIds)) {
            logger.warn("关联测试用例到需求点失败: 用例ID列表为空, requirePointId={}", requirePointId);
            return false;
        }

        // 验证需求点是否存在
        TfRequirepoint requirePoint = requirePointService.getById(requirePointId);
        if (requirePoint == null) {
            logger.warn("关联测试用例到需求点失败: 需求点不存在, requirePointId={}", requirePointId);
            return false;
        }

        // 过滤掉空值
        List<String> validUsecaseIds = usecaseIds.stream()
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());

        if (validUsecaseIds.isEmpty()) {
            logger.warn("关联测试用例到需求点失败: 有效的用例ID列表为空, requirePointId={}", requirePointId);
            return false;
        }

        // 获取已存在的关联
        List<String> existing = this.baseMapper.selectUsecaseIdsByRequirePointId(requirePointId);
        
        // 过滤出需要新增的关联
        List<String> toInsert = validUsecaseIds.stream()
                .filter(id -> !existing.contains(id))
                .collect(Collectors.toList());

        if (toInsert.isEmpty()) {
            logger.debug("所有测试用例已关联到需求点, requirePointId={}", requirePointId);
            return true;
        }

        // 验证用例是否存在（可选，如果需要严格校验）
        // 这里可以添加用例存在性验证，但为了性能考虑，可以跳过，让数据库约束来处理

        // 批量插入关联关系
        int successCount = 0;
        for (String usecaseId : toInsert) {
            TfUsecaseRequire link = new TfUsecaseRequire(usecaseId, requirePointId);
            int result = this.baseMapper.insert(link);
            if (result > 0) {
                successCount++;
            }
        }

        logger.info("关联测试用例到需求点完成: requirePointId={}, 成功={}, 总数={}", 
                requirePointId, successCount, toInsert.size());
        return successCount == toInsert.size();
    }
}
