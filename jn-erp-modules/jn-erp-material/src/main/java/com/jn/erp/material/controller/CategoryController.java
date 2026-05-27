package com.jn.erp.material.controller;

import com.jn.erp.material.domain.JnMaterialCategory;
import com.jn.erp.material.service.IJnMaterialCategoryService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/erp/category")
public class CategoryController extends BaseController {

    @Autowired
    private IJnMaterialCategoryService categoryService;

    @RequiresPermissions("erp:category:list")
    @GetMapping("/list")
    public TableDataInfo list(JnMaterialCategory category) {
        startPage();
        List<JnMaterialCategory> list = categoryService.selectList(category);
        return getDataTable(list);
    }

    @GetMapping("/tree")
    public AjaxResult tree(JnMaterialCategory category) {
        List<JnMaterialCategory> list = categoryService.selectList(category);
        return success(list);
    }

    @RequiresPermissions("erp:category:query")
    @GetMapping("/{categoryId}")
    public AjaxResult getInfo(@PathVariable Long categoryId) {
        return success(categoryService.selectById(categoryId));
    }

    @RequiresPermissions("erp:category:add")
    @Log(title = "物料分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnMaterialCategory category) {
        category.setCreateBy(SecurityUtils.getUsername());
        return toAjax(categoryService.insert(category));
    }

    @RequiresPermissions("erp:category:edit")
    @Log(title = "物料分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnMaterialCategory category) {
        category.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(categoryService.update(category));
    }

    @RequiresPermissions("erp:category:remove")
    @Log(title = "物料分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds) {
        return toAjax(categoryService.deleteByIds(categoryIds));
    }
}
