package com.king.test.bugManage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.test.bugManage.entity.TfBugRolePermission;

/**
 * 缺陷角色权限Service接口
 */
public interface ITfBugRolePermissionService extends IService<TfBugRolePermission> {
    
    /**
     * 更新用户缺陷角色权限（测试组长/开发组长）
     * @param userId 用户ID
     * @param testLeader 是否为测试组长:0为否、1为是（可选）
     * @param devLeader 是否为开发组长:0为否、1为是（可选）
     * @return 是否更新成功
     */
    boolean updateUserBugRolePermission(String userId, String testLeader, String devLeader);
}
