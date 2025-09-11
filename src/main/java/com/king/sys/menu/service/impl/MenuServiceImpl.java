package com.king.sys.menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.sys.menu.entity.SysMenu;
import com.king.sys.menu.mapper.SysMenuMapper;
import com.king.sys.menu.service.IMenuService;
import com.king.sys.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Primary
@Service
public class MenuServiceImpl extends ServiceImpl<SysMenuMapper,SysMenu> implements IMenuService {

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户ID获取菜单列表
     */
    @Override
    public List<SysMenu> getMenusByUserId(String userId){
        List<String> roleNames = userMapper.getRoleNameByUserId(userId);
        boolean isAdmin = roleNames != null && roleNames.stream().anyMatch(r -> "admin".equalsIgnoreCase(r));
        if (isAdmin) {
            return menuMapper.getAllVisibleMenus();
        }
        return menuMapper.getMenusByUserId(userId);
    }

}
