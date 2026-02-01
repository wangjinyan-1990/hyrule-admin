package com.king.test.usecaseManage.usecaseExecution.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.test.usecaseManage.usecaseExecution.entity.TfUsecaseExecution;
import com.king.test.usecaseManage.usecaseExecution.entity.TfUsecaseExecutionHistory;

import java.util.List;
import java.util.Map;

/**
 * 用例执行Service接口
 */
public interface ITfUsecaseExecutionService extends IService<TfUsecaseExecution> {

    /**
     * 添加用例到执行库
     * @param usecaseId 用例ID
     * @param directoryId 目录ID
     * @param systemId 系统ID
     * @return 是否添加成功（如果已存在则返回true，不做任何操作）
     */
    boolean addToExecution(String usecaseId, String directoryId, String systemId);

    /**
     * 分页查询执行用例列表
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param systemId 系统ID（可选）
     * @param directoryId 目录ID（可选）
     * @param usecaseId 用例ID（可选）
     * @param usecaseName 用例名称（可选）
     * @param runStatus 执行状态（可选）
     * @param planExecutorId 计划执行人ID（可选）
     * @param actExecutorId 实际执行人ID（可选）
     * @return 分页结果
     */
    Map<String, Object> getExecutionPage(int pageNo, int pageSize,
                                        String systemId, String directoryId,
                                        String usecaseId, String usecaseName,
                                        String runStatus, String planExecutorId, String actExecutorId);

    /**
     * 获取执行统计信息
     * @param systemId 系统ID（可选）
     * @param directoryId 目录ID（可选）
     * @param includeSubdirectories 是否包含子目录（可选）
     * @param usecaseId 用例ID（可选）
     * @param usecaseName 用例名称（可选）
     * @param usecaseOverview 用例概述（可选）
     * @param actExecutionTimeStart 实际执行时间开始（可选）
     * @param actExecutionTimeEnd 实际执行时间结束（可选）
     * @param runStatus 执行状态（可选）
     * @return 统计信息
     */
    Map<String, Object> getExecutionStatistics(String systemId, String directoryId, Boolean includeSubdirectories,
                                               String usecaseId, String usecaseName, String usecaseOverview,
                                               String actExecutionTimeStart, String actExecutionTimeEnd, String runStatus);

    /**
     * 获取执行详情
     * @param usecaseExecutionId 执行ID
     * @return 执行详情
     */
    TfUsecaseExecution getExecutionDetail(Integer usecaseExecutionId);

    /**
     * 更新执行状态
     * @param usecaseExecutionId 执行ID
     * @param runStatus 执行状态
     * @param remark 执行备注（可选）
     * @param actExecutorId 实际执行人ID（可选）
     * @return 用例执行历史记录ID，如果更新失败返回null
     */
    Integer updateRunStatus(Integer usecaseExecutionId, String runStatus, String remark, String actExecutorId);

    /**
     * 获取历史执行记录列表
     * @param directoryId 目录ID（可选）
     * @param usecaseId 用例ID（可选）
     * @return 历史执行记录列表
     */
    List<TfUsecaseExecutionHistory> getExecutionHistory(String directoryId, String usecaseId);

    /**
     * 删除执行用例
     * @param usecaseExecutionId 执行ID
     * @return 是否删除成功
     */
    boolean deleteExecution(Integer usecaseExecutionId);
}
