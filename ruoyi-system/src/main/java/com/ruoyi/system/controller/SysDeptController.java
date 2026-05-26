package com.ruoyi.system.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.R;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/dept")
public class SysDeptController extends BaseController {

    @Autowired
    private ISysDeptService deptService;

    @GetMapping("/list")
    public R<List<SysDept>> list(SysDept dept) {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return success(depts);
    }

    @GetMapping("/treeSelect")
    public R<List<SysDept>> treeSelect(SysDept dept) {
        List<SysDept> depts = deptService.selectDeptList(dept);
        List<SysDept> tree = deptService.buildDeptTree(depts);
        return success(tree);
    }

    @GetMapping("/{deptId}")
    public R<SysDept> getInfo(@PathVariable Long deptId) {
        return success(deptService.getById(deptId));
    }

    @PostMapping
    public R<Void> add(@RequestBody SysDept dept) {
        if (!deptService.checkDeptNameUnique(dept)) {
            return error("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        return toAjax(deptService.insertDept(dept));
    }

    @PutMapping
    public R<Void> edit(@RequestBody SysDept dept) {
        if (!deptService.checkDeptNameUnique(dept)) {
            return error("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        return toAjax(deptService.updateDept(dept));
    }

    @DeleteMapping("/{deptId}")
    public R<Void> remove(@PathVariable Long deptId) {
        if (deptService.hasChildByDeptId(deptId)) {
            return error("存在下级部门，不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId)) {
            return error("部门存在用户，不允许删除");
        }
        return toAjax(deptService.deleteDeptById(deptId));
    }

    private R<Void> toAjax(int rows) {
        return rows > 0 ? success() : error();
    }
}
