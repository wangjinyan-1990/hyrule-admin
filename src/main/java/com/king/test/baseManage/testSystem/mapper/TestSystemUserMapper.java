package com.king.test.baseManage.testSystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.test.baseManage.testSystem.entity.TTestSystemUser;
import com.king.test.baseManage.testSystem.dto.UserSystemInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 测试系统用户Mapper接口
 */
@Mapper
public interface TestSystemUserMapper extends BaseMapper<TTestSystemUser> {
    
    /**
     * 根据角色ID获取用户列表（在职状态）
     * 每个人只出现一次，多个系统的systemId和systemName会拼接
     * @param roleId 角色ID
     * @param userName 用户姓名（可选）
     * @param loginName 登录名（可选）
     * @param phone 电话（可选）
     * @return 用户系统信息列表
     */
    List<UserSystemInfoDTO> getUsersByRoleId(@Param("roleId") String roleId, 
                                            @Param("userName") String userName,
                                            @Param("loginName") String loginName,
                                            @Param("phone") String phone);
}
