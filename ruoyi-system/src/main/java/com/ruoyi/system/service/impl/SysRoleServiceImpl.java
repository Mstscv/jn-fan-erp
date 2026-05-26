package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.service.ISysRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Override
    public List<SysRole> selectRoleList(SysRole role) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getDelFlag, "0");
        wrapper.orderByAsc(SysRole::getRoleSort);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public Set<String> selectRoleKeysByUserId(Long userId) {
        List<SysRole> roles = selectRolesByUserId(userId);
        return roles.stream().map(SysRole::getRoleKey).collect(Collectors.toSet());
    }

    @Override
    public List<SysRole> selectRolesByUserId(Long userId) {
        if (userId == 1L) {
            LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysRole::getDelFlag, "0");
            wrapper.eq(SysRole::getStatus, "0");
            return baseMapper.selectList(wrapper);
        }
        List<SysRole> userRoles = new ArrayList<>();
        return userRoles;
    }

    @Override
    public boolean checkRoleNameUnique(SysRole role) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleName, role.getRoleName());
        wrapper.eq(SysRole::getDelFlag, "0");
        if (role.getRoleId() != null) {
            wrapper.ne(SysRole::getRoleId, role.getRoleId());
        }
        return baseMapper.selectCount(wrapper) == 0;
    }

    @Override
    public boolean checkRoleKeyUnique(SysRole role) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleKey, role.getRoleKey());
        wrapper.eq(SysRole::getDelFlag, "0");
        if (role.getRoleId() != null) {
            wrapper.ne(SysRole::getRoleId, role.getRoleId());
        }
        return baseMapper.selectCount(wrapper) == 0;
    }

    @Override
    @Transactional
    public int insertRole(SysRole role) {
        return baseMapper.insert(role);
    }

    @Override
    @Transactional
    public int updateRole(SysRole role) {
        return baseMapper.updateById(role);
    }

    @Override
    @Transactional
    public int deleteRoleByIds(Long[] roleIds) {
        int count = 0;
        for (Long roleId : roleIds) {
            SysRole role = new SysRole();
            role.setRoleId(roleId);
            role.setDelFlag("1");
            count += baseMapper.updateById(role);
        }
        return count;
    }

    @Override
    public List<Integer> selectRoleListByUserId(Long userId) {
        List<SysRole> roles = selectRolesByUserId(userId);
        return roles.stream().map(r -> r.getRoleId().intValue()).collect(Collectors.toList());
    }
}
