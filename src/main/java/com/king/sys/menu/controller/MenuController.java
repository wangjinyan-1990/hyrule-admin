package com.king.sys.menu.controller;

import com.king.common.Result;
import com.king.sys.menu.entity.TSysMenu;
import com.king.sys.menu.service.IMenuService;
import com.king.sys.menu.service.IRoleMenuService;
import com.king.sys.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import java.util.Collections;

@RestController
@RequestMapping("/sys/menu")
public class MenuController {

    @Autowired
    private IMenuService menuService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleMenuService roleMenuService;

    /**
     * 根据用户角色获取菜单列表
     */
    @GetMapping("/getUserMenus")
    public Result getUserMenus(HttpServletRequest request) {
        // 从token或session中获取用户信息
        String userId = userService.getCurrentUserId(request);
        if (userId == null) {
            return Result.error("用户未登录或token无效");
        }
        List<TSysMenu> menus = menuService.getMenusByUserId(userId);
        return Result.success(menus);
    }

    /**
     * 管理端：获取全部菜单（扁平列表）
     */
    @GetMapping("/listAll")
    public Result listAll() {
        List<TSysMenu> menus = menuService.list();
        return Result.success(menus);
    }

    /**
     * 管理端：新增菜单
     */
    @PostMapping("/create")
    public Result create(@RequestBody TSysMenu menu) {
        if (menu == null) {
            return Result.error("参数不能为空");
        }
        // 基础必填校验
        if (!StringUtils.hasText(menu.getTitle())) {
            return Result.error("菜单名称(title)不能为空");
        }
        if (!StringUtils.hasText(menu.getPath())) {
            return Result.error("路由路径(path)不能为空");
        }

        // 统一校验（新增：不需要排除自身ID）
        Result check = validateMenuUniqueness(menu, false);
        if (check != null) return check;

        boolean ok = menuService.save(menu);
        return ok ? Result.success(menu) : Result.error("新增失败");
    }

    /**
     * 管理端：更新菜单
     */
    @PutMapping("/update")
    public Result update(@RequestBody TSysMenu menu) {
        if (menu == null || menu.getMenuId() == null) {
            return Result.error("菜单ID不能为空");
        }
        // 基础必填校验
        if (!StringUtils.hasText(menu.getTitle())) {
            return Result.error("菜单名称(title)不能为空");
        }
        if (!StringUtils.hasText(menu.getPath())) {
            return Result.error("路由路径(path)不能为空");
        }

        // 统一校验（更新：需要排除自身ID）
        Result check = validateMenuUniqueness(menu, true);
        if (check != null) return check;

        boolean ok = menuService.updateById(menu);
        return ok ? Result.success(menu) : Result.error("更新失败");
    }

    /**
     * 管理端：删除菜单
     */
    @DeleteMapping("/delete")
    public Result delete(@RequestParam("menuId") Integer menuId) {
        if (menuId == null) {
            return Result.error("菜单ID不能为空");
        }
        // 存在子菜单则不允许删除
        boolean hasChildren = menuService.lambdaQuery()
                .eq(TSysMenu::getParentId, menuId)
                .count() > 0;
        if (hasChildren) {
            return Result.error("存在子菜单，无法删除");
        }

        boolean ok = menuService.removeById(menuId);
        return ok ? Result.success() : Result.error("删除失败");
    }

    /**
     * 校验菜单在新增/更新时的唯一性规则
     * - 一级菜单：同为 parentId=null 下，title 唯一
     * - 子菜单：同 parentId 下，title 唯一
     * - 所有菜单：path 全局唯一
     * - 子菜单：component 全局唯一（按当前需求）
     *
     * @param menu 待校验的菜单
     * @param excludeSelf 是否在校验时排除自身（更新时传 true）
     * @return 若不通过，返回 Result；若通过，返回 null
     */
    private Result validateMenuUniqueness(TSysMenu menu, boolean excludeSelf) {
        Integer parentId = menu.getParentId();

        // 1) 同级标题判重
        if (parentId == null) {
            // 一级菜单：在所有一级菜单中(title)唯一
            boolean existsSameTitleOnRoot = menuService.lambdaQuery()
                    .isNull(TSysMenu::getParentId)
                    .eq(TSysMenu::getTitle, menu.getTitle())
                    .apply(excludeSelf ? "AND MENU_ID <> {0}" : "", menu.getMenuId())
                    .count() > 0;
            if (existsSameTitleOnRoot) {
                return Result.error("一级菜单名称已存在");
            }
        } else {
            // 子菜单：同 parentId 下(title)唯一
            boolean existsSameTitleOnSibling = menuService.lambdaQuery()
                    .eq(TSysMenu::getParentId, parentId)
                    .eq(TSysMenu::getTitle, menu.getTitle())
                    .apply(excludeSelf ? "AND MENU_ID <> {0}" : "", menu.getMenuId())
                    .count() > 0;
            if (existsSameTitleOnSibling) {
                return Result.error("同级(同父)菜单名称已存在");
            }
        }

        // 2) path 全局唯一
        boolean existsSamePath = menuService.lambdaQuery()
                .eq(TSysMenu::getPath, menu.getPath())
                .apply(excludeSelf ? "AND MENU_ID <> {0}" : "", menu.getMenuId())
                .count() > 0;
        if (existsSamePath) {
            return Result.error("路径已存在");
        }

        // 3) 子菜单的 component 必须唯一
        if (parentId != null && StringUtils.hasText(menu.getComponent())) {
            boolean existsSameComponentForChildren = menuService.lambdaQuery()
                    .isNotNull(TSysMenu::getParentId)
                    .eq(TSysMenu::getComponent, menu.getComponent())
                    .apply(excludeSelf ? "AND MENU_ID <> {0}" : "", menu.getMenuId())
                    .count() > 0;
            if (existsSameComponentForChildren) {
                return Result.error("子菜单组件已存在");
            }
        }

        return null;
    }

    /**
     * 管理端：按父ID查询子菜单
     */
    @GetMapping("/listByParent")
    public Result listByParent(@RequestParam("parentId") Integer parentId) {
        if (parentId == null) {
            return Result.error("父ID不能为空");
        }
        List<TSysMenu> children = menuService.lambdaQuery()
                .eq(TSysMenu::getParentId, parentId)
                .list();
        return Result.success(children);
    }

    /**
     * 根据角色获取已分配的菜单ID列表
     */
    @GetMapping("/getMenusByRole")
    public Result getMenusByRole(@RequestParam("roleId") String roleId) {
        if (!StringUtils.hasText(roleId)) {
            return Result.error("角色ID不能为空");
        }
        return Result.success(roleMenuService.getMenuIdsByRole(roleId));
    }

    /**
     * 保存角色的菜单授权
     */
    @PostMapping("/saveRoleMenus")
    public Result saveRoleMenus(@RequestBody java.util.Map<String, Object> body) {
        if (body == null) {
            return Result.error("参数不能为空");
        }
        Object rid = body.get("roleId");
        Object mids = body.get("menuIds");
        if (rid == null || !StringUtils.hasText(String.valueOf(rid))) {
            return Result.error("角色ID不能为空");
        }
        java.util.List<Integer> menuIds;
        if (mids instanceof java.util.List) {
            menuIds = (java.util.List<Integer>) mids;
        } else {
            menuIds = Collections.emptyList();
        }
        boolean ok = roleMenuService.saveRoleMenus(String.valueOf(rid), menuIds);
        return ok ? Result.success("保存成功") : Result.error("保存失败");
    }


}
