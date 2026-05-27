package com.jn.erp.purchase.price.controller;

import com.jn.erp.purchase.price.domain.JnSupplierPrice;
import com.jn.erp.purchase.price.service.IJnSupplierPriceService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/erp/supplier-price")
public class JnSupplierPriceController extends BaseController {

    @Autowired
    private IJnSupplierPriceService jnSupplierPriceService;

    @GetMapping("/list")
    public TableDataInfo list(JnSupplierPrice price) {
        startPage();
        List<JnSupplierPrice> list = jnSupplierPriceService.selectList(price);
        return getDataTable(list);
    }

    @GetMapping("/{priceId}")
    public AjaxResult getInfo(@PathVariable Long priceId) {
        return success(jnSupplierPriceService.getById(priceId));
    }

    @PostMapping
    public AjaxResult add(@RequestBody JnSupplierPrice price) {
        return toAjax(jnSupplierPriceService.insert(price));
    }

    @PutMapping
    public AjaxResult edit(@RequestBody JnSupplierPrice price) {
        return toAjax(jnSupplierPriceService.update(price));
    }

    @DeleteMapping("/{priceIds}")
    public AjaxResult remove(@PathVariable Long[] priceIds) {
        return toAjax(jnSupplierPriceService.deleteByIds(priceIds));
    }

    @GetMapping("/best")
    public AjaxResult bestPrice(@RequestParam Long supplierId, @RequestParam Long materialId) {
        return success(jnSupplierPriceService.getBestPrice(supplierId, materialId));
    }
}
