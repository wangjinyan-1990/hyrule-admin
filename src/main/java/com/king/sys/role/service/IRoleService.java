package com.king.sys.role.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.sys.role.entity.TSysRole;

public interface IRoleService extends IService<TSysRole> {
    void createRole(TSysRole role);
    void updateRole(TSysRole role);
    void deleteRole(String roleId);
}
