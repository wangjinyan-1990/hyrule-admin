package com.king.test.baseManage.testDirectory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.test.baseManage.testDirectory.entity.TTestDirectory;
import com.king.test.baseManage.testDirectory.mapper.TestDirectoryMapper;
import com.king.test.baseManage.testDirectory.service.ITestDirectoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 测试目录Service实现类
 */
@Service("testDirectoryServiceImpl")
public class TestDirectoryServiceImpl extends ServiceImpl<TestDirectoryMapper, TTestDirectory> implements ITestDirectoryService {

    /**
     * 查询用户参与的测试系统,目录树展示系统根目录
     */
    @Override
    public Map<String, Object> getRootDirectoryByUserId(String userId) {
        Assert.hasText(userId, "用户ID不能为空");
        List<TTestDirectory> rootDirectories;
        // 使用自定义查询方法
        if("admin".equals(userId)){
            rootDirectories = baseMapper.getRootDirectory();
        }else{
            rootDirectories = baseMapper.getRootDirectoryByUserId(userId);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("rows", rootDirectories);
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

        // 设置新目录为叶子目录（默认值为1）
        directory.setIsLeafDirectory("1");

        // 保存到数据库
        boolean result = baseMapper.insert(directory) > 0;

        // 如果有父目录，将父目录设置为非叶子目录
        if (result && directory.getDirectoryParentId() != null && !directory.getDirectoryParentId().trim().isEmpty()) {
            updateParentDirectoryLeafStatus(directory.getDirectoryParentId(), "0");
        }

        return result;
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
    public Map<String, Object> getChildrenByParentId(String directoryParentId, String systemId, String module) {
        Assert.hasText(systemId, "系统ID不能为空");

        List<TTestDirectory> children = baseMapper.getChildrenByParentId(directoryParentId, systemId, module);

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

    /**
     * 更新父目录的叶子状态
     * @param directoryParentId 父目录ID
     * @param isLeafDirectory 是否为叶子目录："0"-不是；"1"-是
     */
    private void updateParentDirectoryLeafStatus(String directoryParentId, String isLeafDirectory) {
        TTestDirectory parentDirectory = baseMapper.selectById(directoryParentId);
        if (parentDirectory != null) {
            parentDirectory.setIsLeafDirectory(isLeafDirectory);
            baseMapper.updateById(parentDirectory);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDirectory(String directoryId) {
        Assert.hasText(directoryId, "目录ID不能为空");

        // 检查目录是否存在
        TTestDirectory directory = baseMapper.selectById(directoryId);
        if (directory == null) {
            throw new IllegalArgumentException("目录不存在");
        }

        // 检查是否有子目录
        List<TTestDirectory> children = baseMapper.getChildrenByParentId(directoryId, directory.getSystemId(), null);
        if (!children.isEmpty()) {
            throw new IllegalArgumentException("目录下存在子目录，无法删除");
        }

        // 保存父目录ID，用于后续更新父目录的叶子状态
        String parentId = directory.getDirectoryParentId();

        // 删除目录
        boolean result = baseMapper.deleteById(directoryId) > 0;

        // 如果有父目录，检查父目录是否还有其他子目录
        if (result && parentId != null && !parentId.trim().isEmpty()) {
            checkAndUpdateParentLeafStatus(parentId);
        }

        return result;
    }

    /**
     * 检查并更新父目录的叶子状态
     * @param parentId 父目录ID
     */
    private void checkAndUpdateParentLeafStatus(String parentId) {
        TTestDirectory parentDirectory = baseMapper.selectById(parentId);
        if (parentDirectory != null) {
            // 查询父目录下是否还有其他子目录
            List<TTestDirectory> siblings = baseMapper.getChildrenByParentId(parentId, parentDirectory.getSystemId(), null);

            // 如果没有子目录了，设置为叶子目录
            if (siblings.isEmpty()) {
                updateParentDirectoryLeafStatus(parentId, "1");
            }
        }
    }

    @Override
    public String getDirectoryIdByFullPath(String fullPath) {
        if (!StringUtils.hasText(fullPath)) {
            return null;
        }

        TTestDirectory directory = baseMapper.getDirectoryByFullPath(fullPath);
        return directory != null ? directory.getDirectoryId() : null;
    }

    @Override
    public String getDirectoryFullPath(String directoryId) {
        if (!StringUtils.hasText(directoryId)) {
            return "";
        }

        try {
            TTestDirectory directory = this.getById(directoryId);
            if (directory != null && directory.getFullPath() != null) {
                return directory.getFullPath();
            }

            return null; // 如果获取不到完整路径，返回null
        } catch (Exception e) {
            return null; // 异常时返回null
        }
    }

    @Override
    public List<String> getAllChildrenDirectoryIds(String directoryId, String systemId) {
        if (!StringUtils.hasText(directoryId)) {
            return new ArrayList<>();
        }
        
        // 验证目录是否存在且属于指定系统
        TTestDirectory directory = this.getById(directoryId);
        if (directory == null) {
            return new ArrayList<>();
        }
        
        // 如果提供了 systemId，验证目录是否属于该系统
        if (StringUtils.hasText(systemId) && !systemId.equals(directory.getSystemId())) {
            return new ArrayList<>();
        }
        
        // 使用目录的实际 systemId（如果未提供 systemId 参数）
        String actualSystemId = StringUtils.hasText(systemId) ? systemId : directory.getSystemId();
        
        // 获取父目录的完整路径
        String parentFullPath = directory.getFullPath();
        if (!StringUtils.hasText(parentFullPath)) {
            // 如果 fullPath 为空，回退到递归查询方式
            List<String> allDirectoryIds = new ArrayList<>();
            allDirectoryIds.add(directoryId);
            getAllChildrenDirectoryIdsRecursive(directoryId, actualSystemId, allDirectoryIds);
            return allDirectoryIds;
        }
        
        // 使用 fullPath 的 LIKE 查询获取所有子目录（包含当前目录）
        // 例如：父目录 fullPath 为 "ABC/DEF"，则查询：
        //   - FULLPATH = 'ABC/DEF' （当前目录）
        //   - FULLPATH LIKE 'ABC/DEF/%' （所有子目录，包括子目录的子目录）
        // 这样可以一次性获取所有子目录，避免递归查询，提高性能
        LambdaQueryWrapper<TTestDirectory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TTestDirectory::getSystemId, actualSystemId)
               .and(w -> w.eq(TTestDirectory::getFullPath, parentFullPath)
                         .or()
                         .apply("FULLPATH LIKE {0}", parentFullPath + "/%"));
        
        List<TTestDirectory> directories = this.list(wrapper);
        
        // 提取目录ID列表
        List<String> allDirectoryIds = new ArrayList<>();
        for (TTestDirectory dir : directories) {
            if (dir != null && StringUtils.hasText(dir.getDirectoryId())) {
                allDirectoryIds.add(dir.getDirectoryId());
            }
        }
        
        return allDirectoryIds;
    }

    /**
     * 递归获取所有子目录ID（备用方法，当 fullPath 为空时使用）
     * @param parentDirectoryId 父目录ID
     * @param systemId 系统ID（用于过滤，确保只查询同一系统下的子目录）
     * @param allDirectoryIds 所有目录ID列表
     */
    private void getAllChildrenDirectoryIdsRecursive(String parentDirectoryId, String systemId, List<String> allDirectoryIds) {
        if (!StringUtils.hasText(parentDirectoryId) || !StringUtils.hasText(systemId)) {
            return;
        }
        
        try {
            // 查询直接子目录，同时过滤系统ID
            LambdaQueryWrapper<TTestDirectory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TTestDirectory::getDirectoryParentId, parentDirectoryId)
                   .eq(TTestDirectory::getSystemId, systemId);
            List<TTestDirectory> children = this.list(wrapper);
            
            for (TTestDirectory child : children) {
                if (child != null && StringUtils.hasText(child.getDirectoryId())) {
                    String childId = child.getDirectoryId();
                    // 避免重复添加
                    if (!allDirectoryIds.contains(childId)) {
                        allDirectoryIds.add(childId);
                        // 递归查询子目录的子目录
                        getAllChildrenDirectoryIdsRecursive(childId, systemId, allDirectoryIds);
                    }
                }
            }
        } catch (Exception e) {
            // 查询失败时记录日志，但不影响主流程
            System.err.println("查询子目录失败: parentDirectoryId=" + parentDirectoryId + ", systemId=" + systemId + ", error=" + e.getMessage());
        }
    }

    @Override
    public TTestDirectory getDirectoryById(String directoryId) {
        Assert.hasText(directoryId, "目录ID不能为空");
        
        TTestDirectory directory = this.getById(directoryId);
        if (directory == null) {
            throw new IllegalArgumentException("目录不存在");
        }
        
        // 确保fullPath字段被填充
        // 如果数据库中的fullPath为空，尝试构建完整路径
        if (!StringUtils.hasText(directory.getFullPath())) {
            // 尝试构建完整路径
            buildFullPath(directory);
        }
        
        return directory;
    }

}
