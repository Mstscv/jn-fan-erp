package com.jn.erp.finance.cash.controller;

import com.jn.erp.finance.cash.domain.JnReceiptPayment;
import com.jn.erp.finance.cash.service.IJnReceiptPaymentService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/erp/receipt-payment")
public class JnReceiptPaymentController extends BaseController {

    @Autowired
    private IJnReceiptPaymentService receiptPaymentService;

    @RequiresPermissions("erp:receipt:list")
    @GetMapping("/list")
    public TableDataInfo list(JnReceiptPayment receiptPayment) {
        startPage();
        List<JnReceiptPayment> list = receiptPaymentService.selectList(receiptPayment);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:receipt:query")
    @GetMapping("/{slipId}")
    public AjaxResult getInfo(@PathVariable Long slipId) {
        return success(receiptPaymentService.selectById(slipId));
    }

    @RequiresPermissions("erp:receipt:add")
    @Log(title = "收款付款单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnReceiptPayment receiptPayment) {
        return toAjax(receiptPaymentService.insert(receiptPayment));
    }

    @RequiresPermissions("erp:receipt:edit")
    @Log(title = "收款付款单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnReceiptPayment receiptPayment) {
        return toAjax(receiptPaymentService.update(receiptPayment));
    }

    @RequiresPermissions("erp:receipt:remove")
    @Log(title = "收款付款单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{slipIds}")
    public AjaxResult remove(@PathVariable Long[] slipIds) {
        return toAjax(receiptPaymentService.deleteByIds(slipIds));
    }

    @RequiresPermissions("erp:receipt:approve")
    @Log(title = "收款付款单", businessType = BusinessType.UPDATE)
    @PutMapping("/approve/{slipId}")
    public AjaxResult approve(@PathVariable Long slipId) {
        receiptPaymentService.approve(slipId);
        return success();
    }

    @RequiresPermissions("erp:receipt:cancel")
    @Log(title = "收款付款单", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{slipId}")
    public AjaxResult cancel(@PathVariable Long slipId) {
        receiptPaymentService.cancel(slipId);
        return success();
    }

    @RequiresPermissions("erp:receipt:list")
    @GetMapping("/dateRange")
    public TableDataInfo dateRange(LocalDate startDate, LocalDate endDate) {
        startPage();
        List<JnReceiptPayment> list = receiptPaymentService.selectList(new JnReceiptPayment());
        return getDataTable(list);
    }
}
