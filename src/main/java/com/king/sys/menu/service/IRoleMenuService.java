package com.king.sys.menu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.sys.menu.entity.SysRoleMenu;

import java.util.List;

public interface IRoleMenuService extends IService<SysRoleMenu> {
    public List<Integer> getMenuIdsByRole(String roleId);
    public boolean saveRoleMenus(String roleId, List<Integer> menuIds);
}


