package com.jn.erp.finance.ap.controller;

import com.jn.erp.finance.ap.domain.JnPayable;
import com.jn.erp.finance.ap.service.IJnPayableService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.common.security.utils.SecurityUtils;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/erp/payable")
public class JnPayableController extends BaseController {

    @Autowired
    private IJnPayableService payableService;

    @RequiresPermissions("erp:payable:list")
    @GetMapping("/list")
    public TableDataInfo list(JnPayable payable) {
        startPage();
        List<JnPayable> list = payableService.selectList(payable);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:payable:query")
    @GetMapping("/{payableId}")
    public AjaxResult getInfo(@PathVariable Long payableId) {
        return success(payableService.selectById(payableId));
    }

    @RequiresPermissions("erp:payable:add")
    @Log(title = "应付账款", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnPayable payable) {
        payable.setCreateBy(SecurityUtils.getUsername());
        return toAjax(payableService.insert(payable));
    }

    @RequiresPermissions("erp:payable:edit")
    @Log(title = "应付账款", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnPayable payable) {
        payable.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(payableService.update(payable));
    }

    @RequiresPermissions("erp:payable:remove")
    @Log(title = "应付账款", businessType = BusinessType.DELETE)
    @DeleteMapping("/{payableIds}")
    public AjaxResult remove(@PathVariable Long[] payableIds) {
        return toAjax(payableService.deleteByIds(payableIds));
    }

    @RequiresPermissions("erp:payable:pay")
    @Log(title = "应付账款付款", businessType = BusinessType.UPDATE)
    @PutMapping("/{payableId}/pay")
    public AjaxResult pay(@PathVariable Long payableId, @RequestBody Map<String, BigDecimal> body) {
        BigDecimal amount = body.get("amount");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return error("付款金额必须大于0");
        }
        payableService.pay(payableId, amount);
        return success();
    }

    @RequiresPermissions("erp:payable:cancel")
    @Log(title = "应付账款取消", businessType = BusinessType.UPDATE)
    @PutMapping("/{payableId}/cancel")
    public AjaxResult cancel(@PathVariable Long payableId) {
        payableService.cancel(payableId);
        return success();
    }

    @RequiresPermissions("erp:payable:list")
    @GetMapping("/aging")
    public AjaxResult agingAnalysis(@RequestParam(required = false) Long supplierId) {
        Map<String, Object> result = payableService.getAgingAnalysis(supplierId);
        return success(result);
    }

    @RequiresPermissions("erp:payable:list")
    @GetMapping("/overdue")
    public TableDataInfo overdue(@RequestParam(required = false, defaultValue = "0") Integer days) {
        startPage();
        List<JnPayable> list = payableService.selectList(null);
        return getDataTable(list);
    }

}
