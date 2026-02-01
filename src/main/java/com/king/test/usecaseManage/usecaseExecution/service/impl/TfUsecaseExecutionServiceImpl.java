package com.king.test.usecaseManage.usecaseExecution.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.utils.SecurityUtils;
import com.king.framework.dataDictionary.service.IDataDictionaryService;
import com.king.test.baseManage.testDirectory.service.ITestDirectoryService;
import com.king.test.usecaseManage.usecaseExecution.entity.TfUsecaseExecution;
import com.king.test.usecaseManage.usecaseExecution.entity.TfUsecaseExecutionHistory;
import com.king.test.usecaseManage.usecaseExecution.mapper.TfUsecaseExecutionMapper;
import com.king.test.usecaseManage.usecaseExecution.mapper.TfUsecaseExecutionHistoryMapper;
import com.king.test.usecaseManage.usecaseExecution.service.ITfUsecaseExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用例执行Service实现类
 */
@Service("tfUsecaseExecutionServiceImpl")
public class TfUsecaseExecutionServiceImpl extends ServiceImpl<TfUsecaseExecutionMapper, TfUsecaseExecution> 
        implements ITfUsecaseExecutionService {

    private static final Logger logger = LoggerFactory.getLogger(TfUsecaseExecutionServiceImpl.class);

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    @Qualifier("testDirectoryServiceImpl")
    private ITestDirectoryService testDirectoryService;

    @Autowired
    @Qualifier("dataDictionaryServiceImpl")
    private IDataDictionaryService dataDictionaryService;

    @Autowired
    private TfUsecaseExecutionHistoryMapper executionHistoryMapper;

    @Override
    public Map<String, Object> getExecutionPage(int pageNo, int pageSize,
                                               String systemId, String directoryId,
                                               String usecaseId, String usecaseName,
                                               String runStatus, String planExecutorId, String actExecutorId) {
        Page<TfUsecaseExecution> page = new Page<>(pageNo, pageSize);
        // 获取目录及其所有子目录的ID列表（包含当前目录）
        List<String> directoryIds = fetchDirectoryHierarchy(directoryId, systemId);
        Page<TfUsecaseExecution> result = this.baseMapper.selectPageExecutionList(page, systemId, directoryIds, 
                usecaseId, usecaseName, runStatus, planExecutorId, actExecutorId);

        // 填充执行状态名称（如果 SQL 关联失败，手动查询数据字典）
        for (TfUsecaseExecution execution : result.getRecords()) {
            if (StringUtils.hasText(execution.getRunStatus()) && !StringUtils.hasText(execution.getRunStatusName())) {
                String runStatusName = dataDictionaryService.getDataNameByTypeAndValue("runStatus", execution.getRunStatus());
                execution.setRunStatusName(runStatusName);
            }
        }

        Map<String, Object> data = new HashMap<>(8);
        data.put("rows", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pageNo", result.getCurrent());
        data.put("pageSize", result.getSize());
        data.put("totalPages", result.getPages());
        return data;
    }

    @Override
    public Map<String, Object> getExecutionStatistics(String systemId, String directoryId, Boolean includeSubdirectories,
                                                      String usecaseId, String usecaseName, String usecaseOverview,
                                                      String actExecutionTimeStart, String actExecutionTimeEnd, String runStatus) {
        // 处理目录ID列表
        List<String> directoryIds = null;
        if (StringUtils.hasText(directoryId)) {
            if (includeSubdirectories != null && includeSubdirectories) {
                // 包含子目录
                directoryIds = fetchDirectoryHierarchy(directoryId, systemId);
            } else {
                // 只查询当前目录
                directoryIds = new ArrayList<>();
                directoryIds.add(directoryId);
            }
        }

        // 查询统计信息
        List<Map<String, Object>> statistics = this.baseMapper.selectExecutionStatistics(
                systemId, directoryIds, usecaseId, usecaseName, usecaseOverview,
                actExecutionTimeStart, actExecutionTimeEnd, runStatus);

        // 计算总数
        long total = 0;
        if (statistics != null && !statistics.isEmpty()) {
            for (Map<String, Object> stat : statistics) {
                Object count = stat.get("count");
                if (count instanceof Number) {
                    total += ((Number) count).longValue();
                }
            }
        }

        Map<String, Object> result = new HashMap<>(8);
        result.put("total", total);
        result.put("items", statistics);
        return result;
    }

    @Override
    public TfUsecaseExecution getExecutionDetail(Integer usecaseExecutionId) {
        Assert.notNull(usecaseExecutionId, "执行ID不能为空");
        TfUsecaseExecution execution = this.baseMapper.selectExecutionDetailById(usecaseExecutionId);
        if (execution == null) {
            throw new IllegalArgumentException("执行记录不存在");
        }

        // 填充执行状态名称（如果 SQL 关联失败，手动查询数据字典）
        if (StringUtils.hasText(execution.getRunStatus()) && !StringUtils.hasText(execution.getRunStatusName())) {
            String runStatusName = dataDictionaryService.getDataNameByTypeAndValue("runStatus", execution.getRunStatus());
            execution.setRunStatusName(runStatusName);
        }

        return execution;
    }

    /**
     * 获取目录及其所有子目录的ID列表
     * @param directoryId 目录ID
     * @param systemId 系统ID
     * @return 目录ID列表（包含当前目录及其所有子目录），如果directoryId为空则返回null（不限制目录）
     */
    private List<String> fetchDirectoryHierarchy(String directoryId, String systemId) {
        if (!StringUtils.hasText(directoryId)) {
            return null;
        }
        // 调用目录服务获取当前目录及其所有子目录的ID列表
        List<String> ids = testDirectoryService.getAllChildrenDirectoryIds(directoryId, systemId);
        logger.debug("查询目录层级: directoryId={}, systemId={}, 包含的目录数量={}", directoryId, systemId, ids != null ? ids.size() : 0);
        return ids;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addToExecution(String usecaseId, String directoryId, String systemId) {
        Assert.hasText(usecaseId, "用例ID不能为空");
        Assert.hasText(directoryId, "目录ID不能为空");
        Assert.hasText(systemId, "系统ID不能为空");

        // 检查是否已存在（根据usecaseId和directoryId判断）
        LambdaQueryWrapper<TfUsecaseExecution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TfUsecaseExecution::getUsecaseId, usecaseId)
                   .eq(TfUsecaseExecution::getDirectoryId, directoryId);
        TfUsecaseExecution existing = this.getOne(queryWrapper);

        if (existing != null) {
            logger.info("用例执行记录已存在，不做任何操作: usecaseId={}, directoryId={}", usecaseId, directoryId);
            return true;
        }

        // 创建新的用例执行记录
        TfUsecaseExecution execution = new TfUsecaseExecution();
        execution.setUsecaseId(usecaseId);
        execution.setDirectoryId(directoryId);
        execution.setSystemId(systemId);
        
        // 设置创建时间和创建人
        execution.setExecutionCreateTime(LocalDateTime.now());
        String currentUser = securityUtils.getUserId();
        if (StringUtils.hasText(currentUser)) {
            execution.setExecutionCreatorId(currentUser);
        }

        // 保存到数据库
        boolean saved = this.save(execution);
        if (saved) {
            logger.info("添加用例到执行库成功: usecaseId={}, directoryId={}, systemId={}", 
                    usecaseId, directoryId, systemId);
        } else {
            logger.error("添加用例到执行库失败: usecaseId={}, directoryId={}, systemId={}", 
                    usecaseId, directoryId, systemId);
        }

        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateRunStatus(Integer usecaseExecutionId, String runStatus, String remark, String actExecutorId) {
        Assert.notNull(usecaseExecutionId, "执行ID不能为空");
        Assert.hasText(runStatus, "执行状态不能为空");

        // 查询执行记录
        TfUsecaseExecution execution = this.getById(usecaseExecutionId);
        if (execution == null) {
            throw new IllegalArgumentException("执行记录不存在");
        }

        // 获取当前用户ID（如果未提供执行人ID）
        String executorId = actExecutorId;
        if (!StringUtils.hasText(executorId)) {
            executorId = securityUtils.getUserId();
        }

        // 更新执行记录
        LocalDateTime now = LocalDateTime.now();
        execution.setRunStatus(runStatus);
        execution.setActExecutionTime(now);
        execution.setLastExecutionTime(now);
        if (StringUtils.hasText(executorId)) {
            execution.setActExecutorId(executorId);
        }
        if (StringUtils.hasText(remark)) {
            execution.setRemark(remark);
        }

        boolean updated = this.updateById(execution);
        if (!updated) {
            logger.error("更新执行状态失败: usecaseExecutionId={}, runStatus={}", usecaseExecutionId, runStatus);
            return null;
        }

        // 保存执行历史记录
        TfUsecaseExecutionHistory history = new TfUsecaseExecutionHistory();
        history.setDirectoryId(execution.getDirectoryId());
        history.setUsecaseId(execution.getUsecaseId());
        history.setExecutionTime(now);
        history.setRunStatus(runStatus);
        history.setExecutorId(executorId);
        history.setRemark(remark);

        int historyResult = executionHistoryMapper.insert(history);
        if (historyResult > 0) {
            logger.info("更新执行状态成功并保存历史记录: usecaseExecutionId={}, runStatus={}, executorId={}, historyId={}", 
                    usecaseExecutionId, runStatus, executorId, history.getUsecaseExecutionHistoryId());
            return history.getUsecaseExecutionHistoryId();
        } else {
            logger.error("保存执行历史记录失败: usecaseExecutionId={}, runStatus={}", usecaseExecutionId, runStatus);
            throw new RuntimeException("保存执行历史记录失败");
        }
    }

    @Override
    public List<TfUsecaseExecutionHistory> getExecutionHistory(String directoryId, String usecaseId) {
        List<TfUsecaseExecutionHistory> historyList = executionHistoryMapper.selectExecutionHistoryList(directoryId, usecaseId);

        // 填充执行状态名称（如果 SQL 关联失败，手动查询数据字典）
        for (TfUsecaseExecutionHistory history : historyList) {
            if (StringUtils.hasText(history.getRunStatus()) && !StringUtils.hasText(history.getRunStatusName())) {
                String runStatusName = dataDictionaryService.getDataNameByTypeAndValue("runStatus", history.getRunStatus());
                history.setRunStatusName(runStatusName);
            }
        }

        return historyList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExecution(Integer usecaseExecutionId) {
        Assert.notNull(usecaseExecutionId, "执行ID不能为空");

        // 查询执行记录
        TfUsecaseExecution execution = this.getById(usecaseExecutionId);
        if (execution == null) {
            throw new IllegalArgumentException("执行记录不存在");
        }

        // 删除执行记录
        boolean deleted = this.removeById(usecaseExecutionId);
        if (deleted) {
            logger.info("删除执行用例成功: usecaseExecutionId={}, usecaseId={}, directoryId={}", 
                    usecaseExecutionId, execution.getUsecaseId(), execution.getDirectoryId());
        } else {
            logger.error("删除执行用例失败: usecaseExecutionId={}", usecaseExecutionId);
        }

        return deleted;
    }
}
