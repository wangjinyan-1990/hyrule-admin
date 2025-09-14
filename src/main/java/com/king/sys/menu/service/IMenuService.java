package com.king.sys.menu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.sys.menu.entity.TSysMenu;

import java.util.List;

public interface IMenuService extends IService<TSysMenu> {
    public List<TSysMenu> getMenusByUserId(String userId);
}
