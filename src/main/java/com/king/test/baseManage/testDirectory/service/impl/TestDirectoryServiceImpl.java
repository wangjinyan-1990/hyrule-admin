package com.king.test.baseManage.testDirectory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.utils.SecurityUtils;
import com.king.test.baseManage.testDirectory.entity.TTestDirectory;
import com.king.test.baseManage.testDirectory.mapper.TestDirectoryMapper;
import com.king.test.baseManage.testDirectory.service.ITestDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 测试目录Service实现类
 */
@Service("testDirectoryServiceImpl")
public class TestDirectoryServiceImpl extends ServiceImpl<TestDirectoryMapper, TTestDirectory> implements ITestDirectoryService {

    @Autowired
    private SecurityUtils securityUtils;

    @Override
    public Map<String, Object> getSystemsByUserId(String userId) {
        Assert.hasText(userId, "用户ID不能为空");
        List<TTestDirectory> systems;
        // 使用自定义查询方法
        if("admin".equals(userId)){
            systems = baseMapper.getRootDirectory();
        }else{
            systems = baseMapper.getSystemsByUserId(userId);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("rows", systems);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDirectory(TTestDirectory directory) {
        Assert.notNull(directory, "目录信息不能为空");
        Assert.hasText(directory.getDirectoryName(), "目录名称不能为空");
        Assert.hasText(directory.getSystemId(), "系统ID不能为空");
        
        // 检查目录名称是否重复
        validateDirectoryNameUnique(directory.getDirectoryName(), directory.getDirectoryParentId(), 
                                  directory.getSystemId(), null);
        
        // 生成目录ID
        directory.setDirectoryId(UUID.randomUUID().toString().replace("-", ""));

        // 设置创建时间
        directory.setCreateTime(LocalDateTime.now());
        
        // 构建完整路径
        buildFullPath(directory);
        
        // 设置路径层级字段
        setPathLevels(directory);
        
        // 设置默认值
        if (directory.getIsUseTestcase() == null) {
            directory.setIsUseTestcase("1");
        }
        if (directory.getIsUseTestset() == null) {
            directory.setIsUseTestset("1");
        }
        
        // 保存到数据库
        return baseMapper.insert(directory) > 0;
    }
    
    /**
     * 构建完整路径
     */
    private void buildFullPath(TTestDirectory directory) {
        StringBuilder fullPath = new StringBuilder();
        
        // 如果有父目录，先获取父目录的完整路径
        if (directory.getDirectoryParentId() != null && !directory.getDirectoryParentId().trim().isEmpty()) {
            TTestDirectory parent = baseMapper.selectById(directory.getDirectoryParentId());
            if (parent != null && parent.getFullPath() != null) {
                fullPath.append(parent.getFullPath());
            }
        }
        
        // 添加当前目录名称
        if (fullPath.length() > 0) {
            fullPath.append("/");
        }
        fullPath.append(directory.getDirectoryName());
        
        directory.setFullPath(fullPath.toString());
    }
    
    /**
     * 设置路径层级字段
     */
    private void setPathLevels(TTestDirectory directory) {
        String fullPath = directory.getFullPath();
        if (fullPath != null && !fullPath.isEmpty()) {
            String[] pathParts = fullPath.split("/");
            
            // 设置层级
            directory.setLevel(pathParts.length);
            
            // 设置各级路径
            if (pathParts.length >= 1) {
                directory.setFirstPath(pathParts[0]);
            }
            if (pathParts.length >= 2) {
                directory.setSecondPath(pathParts[1]);
            }
            if (pathParts.length >= 3) {
                directory.setThirdPath(pathParts[2]);
            }
        }
    }

    @Override
    public Map<String, Object> getChildrenByParentId(String directoryParentId, String systemId) {
        Assert.hasText(systemId, "系统ID不能为空");
        
        List<TTestDirectory> children = baseMapper.getChildrenByParentId(directoryParentId, systemId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("rows", children);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDirectory(TTestDirectory directory) {
        Assert.notNull(directory, "目录信息不能为空");
        Assert.hasText(directory.getDirectoryId(), "目录ID不能为空");
        Assert.hasText(directory.getDirectoryName(), "目录名称不能为空");
        Assert.hasText(directory.getSystemId(), "系统ID不能为空");
        
        // 检查目录是否存在
        TTestDirectory existingDirectory = baseMapper.selectById(directory.getDirectoryId());
        if (existingDirectory == null) {
            throw new IllegalArgumentException("目录不存在");
        }
        
        // 如果目录名称发生变化，检查是否重复
        if (!directory.getDirectoryName().equals(existingDirectory.getDirectoryName())) {
            validateDirectoryNameUnique(directory.getDirectoryName(), directory.getDirectoryParentId(), 
                                      directory.getSystemId(), directory.getDirectoryId());
            buildFullPath(directory);
            setPathLevels(directory);
        }
        
        // 设置默认值
        if (directory.getIsUseTestcase() == null) {
            directory.setIsUseTestcase("1");
        }
        if (directory.getIsUseTestset() == null) {
            directory.setIsUseTestset("1");
        }
        
        // 更新到数据库
        return baseMapper.updateById(directory) > 0;
    }
    
    /**
     * 验证目录名称唯一性
     * @param directoryName 目录名称
     * @param directoryParentId 父目录ID
     * @param systemId 系统ID
     * @param excludeDirectoryId 排除的目录ID（更新时使用）
     */
    private void validateDirectoryNameUnique(String directoryName, String directoryParentId, 
                                           String systemId, String excludeDirectoryId) {
        List<TTestDirectory> existingDirectories = baseMapper.checkDirectoryNameExists(
            directoryName, directoryParentId, systemId, excludeDirectoryId);
        
        if (!existingDirectories.isEmpty()) {
            throw new IllegalArgumentException("目录名称已存在");
        }
    }

}
