package com.jn.erp.warehouse.wh.controller;

import com.jn.erp.warehouse.wh.domain.JnWarehouse;
import com.jn.erp.warehouse.wh.service.IJnWarehouseService;
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
@RequestMapping("/erp/warehouse")
public class JnWarehouseController extends BaseController {

    @Autowired
    private IJnWarehouseService warehouseService;

    @RequiresPermissions("erp:warehouse:list")
    @GetMapping("/list")
    public TableDataInfo list(JnWarehouse warehouse) {
        startPage();
        List<JnWarehouse> list = warehouseService.selectList(warehouse);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:warehouse:query")
    @GetMapping("/{whId}")
    public AjaxResult getInfo(@PathVariable Long whId) {
        return success(warehouseService.selectById(whId));
    }

    @RequiresPermissions("erp:warehouse:add")
    @Log(title = "仓库管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnWarehouse warehouse) {
        warehouse.setCreateBy(SecurityUtils.getUsername());
        return toAjax(warehouseService.insert(warehouse));
    }

    @RequiresPermissions("erp:warehouse:edit")
    @Log(title = "仓库管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnWarehouse warehouse) {
        warehouse.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(warehouseService.update(warehouse));
    }

    @RequiresPermissions("erp:warehouse:remove")
    @Log(title = "仓库管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{whIds}")
    public AjaxResult remove(@PathVariable Long[] whIds) {
        return toAjax(warehouseService.deleteByIds(whIds));
    }
}
