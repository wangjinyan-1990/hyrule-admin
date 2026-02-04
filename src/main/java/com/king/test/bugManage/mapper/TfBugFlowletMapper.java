package com.king.test.bugManage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.test.bugManage.entity.TfBugFlowlet;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 缺陷流程Mapper接口
 */
public interface TfBugFlowletMapper extends BaseMapper<TfBugFlowlet> {

    /**
     * 根据当前状态码查询流程ID列表
     * @param currentStateCode 当前状态码
     * @return 流程ID列表
     */
    List<Integer> selectFlowletIdsByCurrentState(@Param("currentStateCode") String currentStateCode);
}

