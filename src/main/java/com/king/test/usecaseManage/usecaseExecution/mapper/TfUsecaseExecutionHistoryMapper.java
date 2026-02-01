package com.king.test.usecaseManage.usecaseExecution.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.test.usecaseManage.usecaseExecution.entity.TfUsecaseExecutionHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用例执行历史记录Mapper接口
 */
@Mapper
public interface TfUsecaseExecutionHistoryMapper extends BaseMapper<TfUsecaseExecutionHistory> {

    /**
     * 查询历史执行记录列表
     * @param directoryId 目录ID（可选）
     * @param usecaseId 用例ID（可选）
     * @return 历史执行记录列表
     */
    List<TfUsecaseExecutionHistory> selectExecutionHistoryList(@Param("directoryId") String directoryId,
                                                                 @Param("usecaseId") String usecaseId);
}
