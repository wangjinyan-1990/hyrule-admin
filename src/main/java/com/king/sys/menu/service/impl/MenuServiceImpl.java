package com.king.sys.menu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.sys.menu.entity.TSysMenu;
import com.king.sys.menu.mapper.SysMenuMapper;
import com.king.sys.menu.service.IMenuService;
import com.king.sys.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.util.List;

@Primary
@Service
public class MenuServiceImpl extends ServiceImpl<SysMenuMapper, TSysMenu> implements IMenuService {

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户ID获取菜单列表
     */
    @Override
    public List<TSysMenu> getMenusByUserId(String userId){
        List<String> roleNames = userMapper.getRoleNameByUserId(userId);
        boolean isAdmin = roleNames != null && roleNames.stream().anyMatch(r -> "admin".equalsIgnoreCase(r));
        if (isAdmin) {
            return menuMapper.getAllVisibleMenus();
        }
        return menuMapper.getMenusByUserId(userId);
    }

}
