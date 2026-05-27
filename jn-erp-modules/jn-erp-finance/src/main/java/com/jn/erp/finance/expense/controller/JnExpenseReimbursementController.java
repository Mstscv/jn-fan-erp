package com.jn.erp.finance.expense.controller;

import com.jn.erp.finance.expense.domain.JnExpenseReimbursement;
import com.jn.erp.finance.expense.service.IJnExpenseReimbursementService;
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

import java.util.List;

@RestController
@RequestMapping("/erp/expense")
public class JnExpenseReimbursementController extends BaseController {

    @Autowired
    private IJnExpenseReimbursementService expenseReimbursementService;

    @RequiresPermissions("erp:expense:list")
    @GetMapping("/list")
    public TableDataInfo list(JnExpenseReimbursement expenseReimbursement) {
        startPage();
        List<JnExpenseReimbursement> list = expenseReimbursementService.selectList(expenseReimbursement);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:expense:query")
    @GetMapping("/{expenseId}")
    public AjaxResult getInfo(@PathVariable Long expenseId) {
        return success(expenseReimbursementService.selectById(expenseId));
    }

    @RequiresPermissions("erp:expense:add")
    @Log(title = "费用报销", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnExpenseReimbursement expenseReimbursement) {
        return toAjax(expenseReimbursementService.insert(expenseReimbursement));
    }

    @RequiresPermissions("erp:expense:edit")
    @Log(title = "费用报销", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnExpenseReimbursement expenseReimbursement) {
        return toAjax(expenseReimbursementService.update(expenseReimbursement));
    }

    @RequiresPermissions("erp:expense:remove")
    @Log(title = "费用报销", businessType = BusinessType.DELETE)
    @DeleteMapping("/{expenseIds}")
    public AjaxResult remove(@PathVariable Long[] expenseIds) {
        return toAjax(expenseReimbursementService.deleteByIds(expenseIds));
    }

    @RequiresPermissions("erp:expense:submit")
    @Log(title = "费用报销", businessType = BusinessType.UPDATE)
    @PutMapping("/submit/{expenseId}")
    public AjaxResult submit(@PathVariable Long expenseId) {
        expenseReimbursementService.submit(expenseId);
        return success();
    }

    @RequiresPermissions("erp:expense:approve")
    @Log(title = "费用报销", businessType = BusinessType.UPDATE)
    @PutMapping("/approve/{expenseId}")
    public AjaxResult approve(@PathVariable Long expenseId) {
        expenseReimbursementService.approve(expenseId);
        return success();
    }

    @RequiresPermissions("erp:expense:reject")
    @Log(title = "费用报销", businessType = BusinessType.UPDATE)
    @PutMapping("/reject/{expenseId}")
    public AjaxResult reject(@PathVariable Long expenseId, String reason) {
        expenseReimbursementService.reject(expenseId, reason);
        return success();
    }

    @RequiresPermissions("erp:expense:pay")
    @Log(title = "费用报销", businessType = BusinessType.UPDATE)
    @PutMapping("/pay/{expenseId}")
    public AjaxResult pay(@PathVariable Long expenseId) {
        expenseReimbursementService.pay(expenseId);
        return success();
    }
}
