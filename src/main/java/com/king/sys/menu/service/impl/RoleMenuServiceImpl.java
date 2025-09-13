package com.king.sys.menu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.sys.menu.entity.SysRoleMenu;
import com.king.sys.menu.mapper.SysRoleMenuMapper;
import com.king.sys.menu.service.IRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;

@Primary
@Service
public class RoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements IRoleMenuService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public List<Integer> getMenuIdsByRole(String roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return sysRoleMenuMapper.selectMenuIdsByRole(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRoleMenus(String roleId, List<Integer> menuIds) {
        sysRoleMenuMapper.deleteByRoleId(roleId);
        if (menuIds == null || menuIds.isEmpty()) {
            return true;
        }
        return sysRoleMenuMapper.insertRoleMenus(roleId, menuIds) > 0;
    }
}


