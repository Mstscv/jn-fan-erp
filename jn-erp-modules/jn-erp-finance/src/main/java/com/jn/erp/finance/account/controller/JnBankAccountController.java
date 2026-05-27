package com.jn.erp.finance.account.controller;

import com.jn.erp.finance.account.domain.JnBankAccount;
import com.jn.erp.finance.account.service.IJnBankAccountService;
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
@RequestMapping("/erp/bank-account")
public class JnBankAccountController extends BaseController {

    @Autowired
    private IJnBankAccountService bankAccountService;

    @RequiresPermissions("erp:bankaccount:list")
    @GetMapping("/list")
    public TableDataInfo list(JnBankAccount bankAccount) {
        startPage();
        List<JnBankAccount> list = bankAccountService.selectList(bankAccount);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:bankaccount:query")
    @GetMapping("/{bankAccountId}")
    public AjaxResult getInfo(@PathVariable Long bankAccountId) {
        return success(bankAccountService.selectById(bankAccountId));
    }

    @RequiresPermissions("erp:bankaccount:add")
    @Log(title = "银行账户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnBankAccount bankAccount) {
        return toAjax(bankAccountService.insert(bankAccount));
    }

    @RequiresPermissions("erp:bankaccount:edit")
    @Log(title = "银行账户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnBankAccount bankAccount) {
        return toAjax(bankAccountService.update(bankAccount));
    }

    @RequiresPermissions("erp:bankaccount:remove")
    @Log(title = "银行账户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{bankAccountIds}")
    public AjaxResult remove(@PathVariable Long[] bankAccountIds) {
        return toAjax(bankAccountService.deleteByIds(bankAccountIds));
    }
}
