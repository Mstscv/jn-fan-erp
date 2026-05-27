package com.jn.erp.warehouse.outstock.controller;

import com.jn.erp.warehouse.outstock.domain.JnOutStock;
import com.jn.erp.warehouse.outstock.domain.JnOutStockLine;
import com.jn.erp.warehouse.outstock.service.IJnOutStockService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/erp/outstock")
public class JnOutStockController extends BaseController {

    @Autowired
    private IJnOutStockService outStockService;

    @RequiresPermissions("erp:outstock:list")
    @GetMapping("/list")
    public TableDataInfo list(JnOutStock outStock) {
        startPage();
        List<JnOutStock> list = outStockService.selectList(outStock);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:outstock:query")
    @GetMapping("/{outStockId}")
    public AjaxResult getInfo(@PathVariable Long outStockId) {
        return success(outStockService.getById(outStockId));
    }

    @Log(title = "销售出库", businessType = BusinessType.INSERT)
    @RequiresPermissions("erp:outstock:salesOut")
    @PostMapping("/sales-out")
    public AjaxResult salesOut(@RequestParam Long deliveryId) {
        JnOutStock outStock = outStockService.salesOut(deliveryId, SecurityUtils.getUsername());
        return success(outStock);
    }

    @Log(title = "领料出库", businessType = BusinessType.INSERT)
    @RequiresPermissions("erp:outstock:issueOut")
    @PostMapping("/issue-out")
    public AjaxResult issueOut(@RequestParam Long woId, @RequestBody List<JnOutStockLine> materialList) {
        JnOutStock outStock = outStockService.issueOut(woId, materialList, SecurityUtils.getUsername());
        return success(outStock);
    }
}
