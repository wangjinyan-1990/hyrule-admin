package com.king.sys.role.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.sys.role.entity.SysRole;

public interface IRoleService extends IService<SysRole> {
    void createRole(SysRole role);
    void updateRole(SysRole role);
    void deleteRole(String roleId);
}
