package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

    @Override
    public List<SysDept> selectDeptList(SysDept dept) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getDelFlag, "0");
        wrapper.orderByAsc(SysDept::getParentId, SysDept::getOrderNum);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> returnList = new ArrayList<>();
        List<Long> tempList = depts.stream().map(SysDept::getDeptId).collect(Collectors.toList());
        for (SysDept dept : depts) {
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList.stream()
                .sorted(Comparator.comparingInt(SysDept::getOrderNum))
                .collect(Collectors.toList());
    }

    private void recursionFn(List<SysDept> list, SysDept t) {
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    private List<SysDept> getChildList(List<SysDept> list, SysDept t) {
        List<SysDept> tlist = new ArrayList<>();
        for (SysDept n : list) {
            if (n.getParentId() != null && n.getParentId().longValue() == t.getDeptId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    private boolean hasChild(List<SysDept> list, SysDept t) {
        return getChildList(list, t).size() > 0;
    }

    @Override
    public boolean checkDeptNameUnique(SysDept dept) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getDeptName, dept.getDeptName());
        wrapper.eq(SysDept::getParentId, dept.getParentId());
        wrapper.eq(SysDept::getDelFlag, "0");
        if (dept.getDeptId() != null) {
            wrapper.ne(SysDept::getDeptId, dept.getDeptId());
        }
        return baseMapper.selectCount(wrapper) == 0;
    }

    @Override
    @Transactional
    public int insertDept(SysDept dept) {
        SysDept parent = baseMapper.selectById(dept.getParentId());
        if (parent != null) {
            dept.setAncestors(parent.getAncestors() + "," + dept.getParentId());
        } else {
            dept.setAncestors("0");
        }
        return baseMapper.insert(dept);
    }

    @Override
    @Transactional
    public int updateDept(SysDept dept) {
        SysDept parent = baseMapper.selectById(dept.getParentId());
        if (parent != null && dept.getDeptId() != null) {
            String newAncestors = parent.getAncestors() + "," + parent.getDeptId();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, dept.getAncestors());
        }
        return baseMapper.updateById(dept);
    }

    private void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(SysDept::getAncestors, oldAncestors + "," + deptId);
        List<SysDept> children = baseMapper.selectList(wrapper);
        for (SysDept child : children) {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
            baseMapper.updateById(child);
        }
    }

    @Override
    @Transactional
    public int deleteDeptById(Long deptId) {
        SysDept dept = new SysDept();
        dept.setDeptId(deptId);
        dept.setDelFlag("1");
        return baseMapper.updateById(dept);
    }

    @Override
    public boolean hasChildByDeptId(Long deptId) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getParentId, deptId);
        wrapper.eq(SysDept::getDelFlag, "0");
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean checkDeptExistUser(Long deptId) {
        return false;
    }
}
