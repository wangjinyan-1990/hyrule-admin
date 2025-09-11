package com.king.sys.menu.controller;

import com.king.common.Result;
import com.alibaba.fastjson2.JSON;
import com.king.common.utils.JwtUtil;
import com.king.sys.menu.entity.SysMenu;
import com.king.sys.menu.service.IMenuService;
import com.king.sys.user.entity.TSysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.data.redis.core.RedisTemplate;

@CrossOrigin
@RestController
@RequestMapping("/sys/menu")
public class MenuController {

    @Autowired
    private IMenuService menuService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 根据用户角色获取菜单列表
     */
    @GetMapping("/getUserMenus")
    public Result getUserMenus(HttpServletRequest request) {
        // 从token或session中获取用户信息
        String userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error("用户未登录或token无效");
        }
        List<SysMenu> menus = menuService.getMenusByUserId(userId);
        return Result.success(menus);
    }

    /**
     * 管理端：获取全部菜单（扁平列表）
     */
    @GetMapping("/listAll")
    public Result listAll() {
        List<SysMenu> menus = menuService.list();
        return Result.success(menus);
    }

    /**
     * 管理端：新增菜单
     */
    @PostMapping("/create")
    public Result create(@RequestBody SysMenu menu) {
        if (menu == null) {
            return Result.error("参数不能为空");
        }
        boolean ok = menuService.save(menu);
        return ok ? Result.success(menu) : Result.error("新增失败");
    }

    /**
     * 管理端：更新菜单
     */
    @PutMapping("/update")
    public Result update(@RequestBody SysMenu menu) {
        if (menu == null || menu.getMenuId() == null) {
            return Result.error("菜单ID不能为空");
        }
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
        boolean ok = menuService.removeById(menuId);
        return ok ? Result.success() : Result.error("删除失败");
    }

    /**
     * 管理端：按父ID查询子菜单
     */
    @GetMapping("/listByParent")
    public Result listByParent(@RequestParam("parentId") Integer parentId) {
        if (parentId == null) {
            return Result.error("父ID不能为空");
        }
        List<SysMenu> children = menuService.lambdaQuery()
                .eq(SysMenu::getParentId, parentId)
                .list();
        return Result.success(children);
    }

    /**
     * 从请求中获取当前用户ID
     * @param request
     * @return
     */
    private String getCurrentUserId(HttpServletRequest request) {
        // 从JWT token中解析用户ID，优先使用配置的header，其次兼容 X-Token 和 请求参数 token
        String token = request.getHeader(jwtUtil.getHeaderName());
        if (token == null || token.trim().isEmpty()) {
            token = request.getHeader("X-Token");
        }
        if (token == null || token.trim().isEmpty()) {
            token = request.getParameter("token");
        }
        // 先尝试作为JWT解析
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId != null) {
            return userId;
        }
        // 再尝试作为Redis会话token
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        Object obj = redisTemplate.opsForValue().get(token);
        if (obj == null) {
            return null;
        }
        try {
            TSysUser loginUser = JSON.parseObject(JSON.toJSONString(obj), TSysUser.class);
            return loginUser != null ? loginUser.getUserId() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
