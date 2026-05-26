package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.SysRole;

import java.util.List;
import java.util.Set;

public interface ISysRoleService extends IService<SysRole> {

    List<SysRole> selectRoleList(SysRole role);

    Set<String> selectRoleKeysByUserId(Long userId);

    List<SysRole> selectRolesByUserId(Long userId);

    boolean checkRoleNameUnique(SysRole role);

    boolean checkRoleKeyUnique(SysRole role);

    int insertRole(SysRole role);

    int updateRole(SysRole role);

    int deleteRoleByIds(Long[] roleIds);

    List<Integer> selectRoleListByUserId(Long userId);
}
