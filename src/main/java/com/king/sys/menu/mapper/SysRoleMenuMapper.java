package com.king.sys.menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.sys.menu.entity.TSysRoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysRoleMenuMapper extends BaseMapper<TSysRoleMenu> {
    List<Integer> selectMenuIdsByRole(String roleId);
    int deleteByRoleId(String roleId);
    int insertRoleMenus(String roleId,List<Integer> menuIds);
}


