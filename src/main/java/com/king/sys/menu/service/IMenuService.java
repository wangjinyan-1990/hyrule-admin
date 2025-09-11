package com.king.sys.menu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.sys.menu.entity.SysMenu;

import java.util.List;

public interface IMenuService extends IService<SysMenu> {
    public List<SysMenu> getMenusByUserId(String userId);
}
