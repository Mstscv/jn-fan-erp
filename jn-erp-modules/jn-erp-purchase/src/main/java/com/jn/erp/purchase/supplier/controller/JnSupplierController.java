package com.jn.erp.purchase.supplier.controller;

import com.jn.erp.purchase.supplier.domain.JnSupplier;
import com.jn.erp.purchase.supplier.service.IJnSupplierService;
import com.ruoyi.common.core.utils.PageUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
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
@RequestMapping("/erp/supplier")
public class JnSupplierController extends BaseController {

    @Autowired
    private IJnSupplierService jnSupplierService;

    @GetMapping("/list")
    public TableDataInfo list(JnSupplier supplier) {
        startPage();
        List<JnSupplier> list = jnSupplierService.selectList(supplier);
        return getDataTable(list);
    }

    @GetMapping("/{supplierId}")
    public AjaxResult getInfo(@PathVariable Long supplierId) {
        return success(jnSupplierService.getById(supplierId));
    }

    @PostMapping
    public AjaxResult add(@RequestBody JnSupplier supplier) {
        return toAjax(jnSupplierService.insert(supplier));
    }

    @PutMapping
    public AjaxResult edit(@RequestBody JnSupplier supplier) {
        return toAjax(jnSupplierService.update(supplier));
    }

    @DeleteMapping("/{supplierIds}")
    public AjaxResult remove(@PathVariable Long[] supplierIds) {
        return toAjax(jnSupplierService.deleteByIds(supplierIds));
    }
}
