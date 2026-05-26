package com.ruoyi.system.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.R;
import com.ruoyi.system.domain.SysMenu;
import com.ruoyi.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/menu")
public class SysMenuController extends BaseController {

    @Autowired
    private ISysMenuService menuService;

    @GetMapping("/list")
    public R<List<SysMenu>> list(SysMenu menu) {
        List<SysMenu> menus = menuService.selectMenuList(menu);
        return success(menus);
    }

    @GetMapping("/treeMenu")
    public R<List<SysMenu>> treeMenu() {
        List<SysMenu> menus = menuService.selectMenuList(new SysMenu());
        return success(menuService.buildMenuTree(menus));
    }

    @GetMapping("/treeselect")
    public R<List<SysMenu>> treeselect(SysMenu menu) {
        List<SysMenu> menus = menuService.selectMenuList(menu);
        List<SysMenu> tree = menuService.buildMenuTree(menus);
        return success(tree);
    }

    @GetMapping("/{menuId}")
    public R<SysMenu> getInfo(@PathVariable Long menuId) {
        return success(menuService.getById(menuId));
    }

    @PostMapping
    public R<Void> add(@RequestBody SysMenu menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        return toAjax(menuService.insertMenu(menu));
    }

    @PutMapping
    public R<Void> edit(@RequestBody SysMenu menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        return toAjax(menuService.updateMenu(menu));
    }

    @DeleteMapping("/{menuId}")
    public R<Void> remove(@PathVariable Long menuId) {
        if (menuService.hasChildByMenuId(menuId)) {
            return error("存在子菜单，不允许删除");
        }
        return toAjax(menuService.deleteMenuById(menuId));
    }

    private R<Void> toAjax(int rows) {
        return rows > 0 ? success() : error();
    }
}
