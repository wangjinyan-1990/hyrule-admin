package com.king.test.bugManage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.test.bugManage.entity.TfBugRolePermission;
import com.king.test.bugManage.mapper.TfBugRolePermissionMapper;
import com.king.test.bugManage.service.ITfBugRolePermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 缺陷角色权限Service实现类
 */
@Service("tfBugRolePermissionServiceImpl")
public class TfBugRolePermissionServiceImpl extends ServiceImpl<TfBugRolePermissionMapper, TfBugRolePermission> 
        implements ITfBugRolePermissionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TfBugRolePermissionServiceImpl.class);
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserBugRolePermission(String userId, String testLeader, String devLeader) {
        Assert.hasText(userId, "用户ID不能为空");
        
        // 至少需要提供一个角色权限参数
        if (!StringUtils.hasText(testLeader) && !StringUtils.hasText(devLeader)) {
            throw new IllegalArgumentException("至少需要提供testLeader或devLeader参数");
        }
        
        // 查询是否已存在该用户的记录
        LambdaQueryWrapper<TfBugRolePermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TfBugRolePermission::getUserId, userId);
        TfBugRolePermission existing = this.getOne(queryWrapper);
        
        if (existing != null) {
            // 更新现有记录
            boolean updated = false;
            
            if (StringUtils.hasText(testLeader)) {
                existing.setTestLeader(testLeader);
                updated = true;
            }
            
            if (StringUtils.hasText(devLeader)) {
                existing.setDevLeader(devLeader);
                updated = true;
            }
            
            if (updated) {
                boolean result = this.updateById(existing);
                if (result) {
                    logger.info("更新用户缺陷角色权限成功: userId={}, testLeader={}, devLeader={}", 
                            userId, testLeader, devLeader);
                } else {
                    logger.error("更新用户缺陷角色权限失败: userId={}, testLeader={}, devLeader={}", 
                            userId, testLeader, devLeader);
                }
                return result;
            }
        } else {
            // 创建新记录
            TfBugRolePermission newRecord = new TfBugRolePermission();
            newRecord.setUserId(userId);
            
            // 设置testLeader，如果未提供则默认为"0"
            newRecord.setTestLeader(StringUtils.hasText(testLeader) ? testLeader : "0");
            
            // 设置devLeader，如果未提供则默认为"0"
            newRecord.setDevLeader(StringUtils.hasText(devLeader) ? devLeader : "0");
            
            boolean result = this.save(newRecord);
            if (result) {
                logger.info("创建用户缺陷角色权限成功: userId={}, testLeader={}, devLeader={}", 
                        userId, newRecord.getTestLeader(), newRecord.getDevLeader());
            } else {
                logger.error("创建用户缺陷角色权限失败: userId={}, testLeader={}, devLeader={}", 
                        userId, newRecord.getTestLeader(), newRecord.getDevLeader());
            }
            return result;
        }
        
        return true;
    }
}
