package com.jn.erp.finance.account.controller;

import com.jn.erp.finance.account.domain.JnAccount;
import com.jn.erp.finance.account.service.IJnAccountService;
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
@RequestMapping("/erp/account")
public class JnAccountController extends BaseController {

    @Autowired
    private IJnAccountService accountService;

    @RequiresPermissions("erp:account:list")
    @GetMapping("/list")
    public TableDataInfo list(JnAccount account) {
        startPage();
        List<JnAccount> list = accountService.selectList(account);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:account:query")
    @GetMapping("/{accountId}")
    public AjaxResult getInfo(@PathVariable Long accountId) {
        return success(accountService.selectById(accountId));
    }

    @RequiresPermissions("erp:account:add")
    @Log(title = "科目管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnAccount account) {
        return toAjax(accountService.insert(account));
    }

    @RequiresPermissions("erp:account:edit")
    @Log(title = "科目管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnAccount account) {
        return toAjax(accountService.update(account));
    }

    @RequiresPermissions("erp:account:remove")
    @Log(title = "科目管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{accountIds}")
    public AjaxResult remove(@PathVariable Long[] accountIds) {
        return toAjax(accountService.deleteByIds(accountIds));
    }
}
