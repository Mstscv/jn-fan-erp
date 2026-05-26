package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysMenu;
import com.ruoyi.system.mapper.SysMenuMapper;
import com.ruoyi.system.service.ISysMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Override
    public List<SysMenu> selectMenuList(SysMenu menu) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getParentId, SysMenu::getOrderNum);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId) {
        List<SysMenu> menus;
        if (userId != null && userId == 1L) {
            menus = selectMenuAll();
        } else {
            menus = selectMenuList(new SysMenu());
        }
        return buildMenuTree(menus);
    }

    private List<SysMenu> selectMenuAll() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, "0");
        wrapper.eq(SysMenu::getVisible, "0");
        wrapper.in(SysMenu::getMenuType, "M", "C");
        wrapper.orderByAsc(SysMenu::getParentId, SysMenu::getOrderNum);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<>();
        List<Long> tempList = menus.stream().map(SysMenu::getMenuId).collect(Collectors.toList());
        for (SysMenu menu : menus) {
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList.stream()
                .sorted(Comparator.comparingInt(SysMenu::getOrderNum))
                .collect(Collectors.toList());
    }

    private void recursionFn(List<SysMenu> list, SysMenu t) {
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tlist = new ArrayList<>();
        for (SysMenu n : list) {
            if (n.getParentId() != null && n.getParentId().longValue() == t.getMenuId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    private boolean hasChild(List<SysMenu> list, SysMenu t) {
        return getChildList(list, t).size() > 0;
    }

    @Override
    public Set<String> selectMenuPermsByUserId(Long userId) {
        List<SysMenu> perms = baseMapper.selectList(new LambdaQueryWrapper<SysMenu>().isNotNull(SysMenu::getPerms));
        Set<String> permsSet = new HashSet<>();
        for (SysMenu perm : perms) {
            if (perm.getPerms() != null && !perm.getPerms().isEmpty()) {
                permsSet.addAll(Arrays.asList(perm.getPerms().trim().split(",")));
            }
        }
        return permsSet;
    }

    @Override
    public List<SysMenu> selectMenuListByUserId(SysMenu menu) {
        return selectMenuList(menu);
    }

    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getMenuName, menu.getMenuName());
        wrapper.eq(SysMenu::getParentId, menu.getParentId());
        if (menu.getMenuId() != null) {
            wrapper.ne(SysMenu::getMenuId, menu.getMenuId());
        }
        return baseMapper.selectCount(wrapper) == 0;
    }

    @Override
    @Transactional
    public int insertMenu(SysMenu menu) {
        return baseMapper.insert(menu);
    }

    @Override
    @Transactional
    public int updateMenu(SysMenu menu) {
        return baseMapper.updateById(menu);
    }

    @Override
    @Transactional
    public int deleteMenuById(Long menuId) {
        return baseMapper.deleteById(menuId);
    }

    @Override
    public boolean hasChildByMenuId(Long menuId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, menuId);
        return baseMapper.selectCount(wrapper) > 0;
    }
}
