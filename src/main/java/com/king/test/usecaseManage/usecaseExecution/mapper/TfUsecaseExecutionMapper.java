package com.king.test.usecaseManage.usecaseExecution.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.king.test.usecaseManage.usecaseExecution.entity.TfUsecaseExecution;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用例执行Mapper接口
 */
@Mapper
public interface TfUsecaseExecutionMapper extends BaseMapper<TfUsecaseExecution> {

    /**
     * 根据用例ID和目录ID查询用例执行记录
     * @param usecaseId 用例ID
     * @param directoryId 目录ID
     * @return 用例执行记录，如果不存在返回null
     */
    TfUsecaseExecution selectByUsecaseIdAndDirectoryId(@Param("usecaseId") String usecaseId, 
                                                       @Param("directoryId") String directoryId);

    /**
     * 分页查询用例执行列表
     * @param page 分页对象
     * @param systemId 系统ID（可选）
     * @param directoryIds 目录ID列表（可选，当includeSubdirectories=false时使用）
     * @param directoryFullPath 目录完整路径（可选，当includeSubdirectories=true时使用，用于LIKE查询）
     * @param usecaseId 用例ID（可选）
     * @param usecaseName 用例名称（可选，模糊查询）
     * @param runStatus 执行状态（可选）
     * @param planExecutorId 计划执行人ID（可选）
     * @param actExecutorId 实际执行人ID（可选）
     * @return 分页结果
     */
    Page<TfUsecaseExecution> selectPageExecutionList(Page<TfUsecaseExecution> page,
                                                      @Param("systemId") String systemId,
                                                      @Param("directoryIds") List<String> directoryIds,
                                                      @Param("directoryFullPath") String directoryFullPath,
                                                      @Param("usecaseId") String usecaseId,
                                                      @Param("usecaseName") String usecaseName,
                                                      @Param("runStatus") String runStatus,
                                                      @Param("planExecutorId") String planExecutorId,
                                                      @Param("actExecutorId") String actExecutorId);

    /**
     * 查询执行统计信息
     * @param systemId 系统ID（可选）
     * @param directoryIds 目录ID列表（可选，当includeSubdirectories=false时使用）
     * @param directoryFullPath 目录完整路径（可选，当includeSubdirectories=true时使用，用于LIKE查询）
     * @param usecaseId 用例ID（可选）
     * @param usecaseName 用例名称（可选，模糊查询）
     * @param usecaseOverview 用例概述（可选，模糊查询）
     * @param actExecutionTimeStart 实际执行时间开始（可选）
     * @param actExecutionTimeEnd 实际执行时间结束（可选）
     * @param runStatus 执行状态（可选）
     * @return 统计信息列表
     */
    List<Map<String, Object>> selectExecutionStatistics(@Param("systemId") String systemId,
                                                         @Param("directoryIds") List<String> directoryIds,
                                                         @Param("directoryFullPath") String directoryFullPath,
                                                         @Param("usecaseId") String usecaseId,
                                                         @Param("usecaseName") String usecaseName,
                                                         @Param("usecaseOverview") String usecaseOverview,
                                                         @Param("actExecutionTimeStart") String actExecutionTimeStart,
                                                         @Param("actExecutionTimeEnd") String actExecutionTimeEnd,
                                                         @Param("runStatus") String runStatus);

    /**
     * 根据执行ID查询执行详情（关联用例表）
     * @param usecaseExecutionId 执行ID
     * @return 执行详情，如果不存在返回null
     */
    TfUsecaseExecution selectExecutionDetailById(@Param("usecaseExecutionId") Integer usecaseExecutionId);

    /**
     * 查询缺陷数量
     * @param systemId 系统ID（可选）
     * @param directoryIds 目录ID列表（可选，当includeSubdirectories=false时使用）
     * @param directoryFullPath 目录完整路径（可选，当includeSubdirectories=true时使用，用于LIKE查询）
     * @return 缺陷数量
     */
    Long selectBugCount(@Param("systemId") String systemId,
                       @Param("directoryIds") List<String> directoryIds,
                       @Param("directoryFullPath") String directoryFullPath);
}
