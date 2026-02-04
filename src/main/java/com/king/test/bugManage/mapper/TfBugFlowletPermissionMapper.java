package com.king.test.bugManage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.test.bugManage.entity.TfBugFlowletPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 缺陷流程权限Mapper接口
 */
public interface TfBugFlowletPermissionMapper extends BaseMapper<TfBugFlowletPermission> {

    /**
     * 根据流程ID和角色代码查询下一状态码列表
     * @param bugFlowletIds 流程ID列表
     * @param bugRoleCodes 角色代码列表
     * @return 下一状态码列表（去重）
     */
    List<String> selectNextStateCodesByFlowletIdsAndRoleCodes(
            @Param("bugFlowletIds") List<Integer> bugFlowletIds,
            @Param("bugRoleCodes") List<String> bugRoleCodes);
}

