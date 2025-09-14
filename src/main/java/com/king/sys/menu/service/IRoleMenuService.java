package com.king.sys.menu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.sys.menu.entity.TSysRoleMenu;

import java.util.List;

public interface IRoleMenuService extends IService<TSysRoleMenu> {
    public List<Integer> getMenuIdsByRole(String roleId);
    public boolean saveRoleMenus(String roleId, List<Integer> menuIds);
}


