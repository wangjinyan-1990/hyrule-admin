package com.king.sys.menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.sys.menu.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    /**
     * 根据用户ID查询菜单列表
     */
    List<SysMenu> getMenusByUserId(String userId);

    /**
     * 查询所有未隐藏的菜单（admin 使用）
     */
    List<SysMenu> getAllVisibleMenus();

}
