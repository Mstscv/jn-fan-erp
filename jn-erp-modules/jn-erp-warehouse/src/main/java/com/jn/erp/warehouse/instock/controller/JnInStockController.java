package com.jn.erp.warehouse.instock.controller;

import com.jn.erp.warehouse.instock.domain.JnInStock;
import com.jn.erp.warehouse.instock.domain.JnInStockLine;
import com.jn.erp.warehouse.instock.service.IJnInStockService;
import com.jn.erp.warehouse.instock.service.impl.JnInStockServiceImpl;
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
@RequestMapping("/erp/instock")
public class JnInStockController extends BaseController {

    @Autowired
    private IJnInStockService inStockService;

    @RequiresPermissions("erp:instock:list")
    @GetMapping("/list")
    public TableDataInfo list(JnInStock inStock) {
        startPage();
        List<JnInStock> list = inStockService.selectList(inStock);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:instock:query")
    @GetMapping("/{inStockId}")
    public AjaxResult getInfo(@PathVariable Long inStockId) {
        return success(inStockService.getById(inStockId));
    }

    @Log(title = "采购入库", businessType = BusinessType.INSERT)
    @RequiresPermissions("erp:instock:purchaseIn")
    @PostMapping("/purchase-in")
    public AjaxResult purchaseIn(@RequestParam Long poId, @RequestParam Long receiveId) {
        JnInStock inStock = inStockService.purchaseIn(poId, receiveId, SecurityUtils.getUsername());
        return success(inStock);
    }

    @Log(title = "生产入库", businessType = BusinessType.INSERT)
    @RequiresPermissions("erp:instock:productionIn")
    @PostMapping("/production-in")
    public AjaxResult productionIn(@RequestParam Long woId) {
        JnInStock inStock = inStockService.productionIn(woId, SecurityUtils.getUsername());
        return success(inStock);
    }

    @Log(title = "入库明细添加", businessType = BusinessType.INSERT)
    @PostMapping("/{inStockId}/line")
    public AjaxResult addLine(@PathVariable Long inStockId, @RequestBody JnInStockLine line,
                              @RequestParam Long whId) {
        JnInStock inStock = inStockService.getById(inStockId);
        if (inStock == null) {
            return error("入库单不存在");
        }
        JnInStockServiceImpl impl = (JnInStockServiceImpl) inStockService;
        impl.addLineAndUpdateStock(inStock, line, whId, SecurityUtils.getUsername());
        return success();
    }
}
