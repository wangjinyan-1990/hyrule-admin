package com.king.test.baseManage.testSystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.test.baseManage.testSystem.entity.TTestSystemUser;
import com.king.test.baseManage.testSystem.dto.UserSystemInfoDTO;

import java.util.List;

/**
 * 测试系统用户Service接口
 */
public interface ITestSystemUserService extends IService<TTestSystemUser> {
    
    /**
     * 保存或更新测试系统用户关系
     * @param systemId 系统ID
     * @param userIds 用户ID列表
     * @return 是否保存成功
     */
    boolean saveOrUpdateSystemUsers(String systemId, List<String> userIds);
    
    /**
     * 根据系统ID获取用户ID列表
     * @param systemId 系统ID
     * @return 用户ID列表
     */
    List<String> getUserIdsBySystemId(String systemId);
    
    /**
     * 根据用户ID获取系统ID列表
     * @param userId 用户ID
     * @return 系统ID列表
     */
    List<String> getSystemIdsByUserId(String userId);
    
    /**
     * 删除测试系统用户关系
     * @param systemId 系统ID
     * @param userIds 用户ID列表
     * @return 是否删除成功
     */
    boolean removeSystemUsers(String systemId, List<String> userIds);
    
    /**
     * 根据系统ID删除所有用户关系
     * @param systemId 系统ID
     * @return 是否删除成功
     */
    boolean removeAllUsersBySystemId(String systemId);
    
    /**
     * 根据角色ID获取用户列表（在职状态）
     * 每个人只出现一次，多个系统的systemId和systemName会拼接
     * @param roleId 角色ID
     * @return 用户系统信息列表
     */
    List<UserSystemInfoDTO> getUsersByRoleId(String roleId);
}
