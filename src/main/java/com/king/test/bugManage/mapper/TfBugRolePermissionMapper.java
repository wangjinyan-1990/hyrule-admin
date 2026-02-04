package com.king.test.bugManage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.test.bugManage.entity.TfBugRolePermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 缺陷角色权限Mapper接口
 */
public interface TfBugRolePermissionMapper extends BaseMapper<TfBugRolePermission> {

    /**
     * 根据系统ID和用户ID查询测试组长角色权限
     * @param systemId 系统ID
     * @param userId 用户ID
     * @return 角色权限列表
     */
    List<TfBugRolePermission> selectTestLeaderBySystemIdAndUserId(
            @Param("systemId") String systemId,
            @Param("userId") String userId);
}
