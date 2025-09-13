package com.king.sys.menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.sys.menu.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {
    List<Integer> selectMenuIdsByRole(String roleId);
    int deleteByRoleId(String roleId);
    int insertRoleMenus(String roleId,List<Integer> menuIds);
}


