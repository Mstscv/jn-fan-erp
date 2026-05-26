package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysDept;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ISysDeptService extends IService<SysDept> {

    List<SysDept> selectDeptList(SysDept dept);

    List<SysDept> buildDeptTree(List<SysDept> depts);

    boolean checkDeptNameUnique(SysDept dept);

    int insertDept(SysDept dept);

    int updateDept(SysDept dept);

    int deleteDeptById(Long deptId);

    boolean hasChildByDeptId(Long deptId);

    boolean checkDeptExistUser(Long deptId);
}
