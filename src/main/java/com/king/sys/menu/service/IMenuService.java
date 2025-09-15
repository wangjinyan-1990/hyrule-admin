package com.king.sys.menu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.sys.menu.entity.TSysMenu;

import java.util.List;

public interface IMenuService extends IService<TSysMenu> {
    public List<TSysMenu> getMenusByUserId(String userId);
    
    /**
     * 更新父菜单的isLeaf状态
     * @param parentId 父菜单ID
     * @param isLeaf 是否为叶子菜单：0-否；1-是
     * @return 更新是否成功
     */
    public boolean updateParentMenuIsLeaf(Integer parentId, String isLeaf);
}
