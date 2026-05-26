package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.SysMenu;

import java.util.List;
import java.util.Set;

public interface ISysMenuService extends IService<SysMenu> {

    List<SysMenu> selectMenuList(SysMenu menu);

    List<SysMenu> selectMenuTreeByUserId(Long userId);

    List<SysMenu> buildMenuTree(List<SysMenu> menus);

    Set<String> selectMenuPermsByUserId(Long userId);

    List<SysMenu> selectMenuListByUserId(SysMenu menu);

    boolean checkMenuNameUnique(SysMenu menu);

    int insertMenu(SysMenu menu);

    int updateMenu(SysMenu menu);

    int deleteMenuById(Long menuId);

    boolean hasChildByMenuId(Long menuId);
}
