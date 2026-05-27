package com.jn.erp.warehouse.inventory.controller;

import com.jn.erp.warehouse.inventory.domain.JnInventory;
import com.jn.erp.warehouse.inventory.service.IJnInventoryService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/erp/warehouse/inventory")
public class JnInventoryController extends BaseController {

    @Autowired
    private IJnInventoryService inventoryService;

    @RequiresPermissions("erp:inventory:list")
    @GetMapping("/list")
    public TableDataInfo list(JnInventory inventory) {
        startPage();
        List<JnInventory> list = inventoryService.selectList(inventory);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:inventory:query")
    @GetMapping("/{inventoryId}")
    public AjaxResult getInfo(@PathVariable Long inventoryId) {
        return success(inventoryService.getById(inventoryId));
    }

    @GetMapping("/by-material")
    public AjaxResult getByMaterial(Long materialId, Long whId) {
        return success(inventoryService.getInventory(materialId, whId, null));
    }

    @RequiresPermissions("erp:inventory:safety")
    @GetMapping("/safety")
    public AjaxResult safetyStock() {
        return success(inventoryService.checkSafetyStock());
    }
}
