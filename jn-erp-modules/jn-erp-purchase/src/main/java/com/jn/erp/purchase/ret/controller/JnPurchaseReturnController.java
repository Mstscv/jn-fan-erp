package com.jn.erp.purchase.ret.controller;

import com.jn.erp.purchase.ret.domain.JnPurchaseReturn;
import com.jn.erp.purchase.ret.service.IJnPurchaseReturnService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
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
import java.util.Map;

@RestController
@RequestMapping("/erp/purchase-return")
public class JnPurchaseReturnController extends BaseController {

    @Autowired
    private IJnPurchaseReturnService purchaseReturnService;

    @GetMapping("/list")
    public TableDataInfo list(JnPurchaseReturn ret) {
        startPage();
        List<JnPurchaseReturn> list = purchaseReturnService.selectList(ret);
        return getDataTable(list);
    }

    @GetMapping("/{returnId}")
    public AjaxResult getInfo(@PathVariable Long returnId) {
        return success(purchaseReturnService.getById(returnId));
    }

    @Log(title = "采购退货", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnPurchaseReturn ret) {
        return toAjax(purchaseReturnService.insert(ret));
    }

    @Log(title = "采购退货", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnPurchaseReturn ret) {
        return toAjax(purchaseReturnService.update(ret));
    }

    @Log(title = "采购退货", businessType = BusinessType.DELETE)
    @DeleteMapping("/{returnIds}")
    public AjaxResult remove(@PathVariable Long[] returnIds) {
        return toAjax(purchaseReturnService.deleteByIds(returnIds));
    }

    @Log(title = "采购退货状态", businessType = BusinessType.UPDATE)
    @PutMapping("/{returnId}/status")
    public AjaxResult updateStatus(@PathVariable Long returnId, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return toAjax(purchaseReturnService.updateStatus(returnId, status));
    }
}
