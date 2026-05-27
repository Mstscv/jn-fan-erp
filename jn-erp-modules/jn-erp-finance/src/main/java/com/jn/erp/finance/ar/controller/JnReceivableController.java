package com.jn.erp.finance.ar.controller;

import com.jn.erp.finance.ar.domain.JnReceivable;
import com.jn.erp.finance.ar.service.IJnReceivableService;
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
@RequestMapping("/erp/receivable")
public class JnReceivableController extends BaseController {

    @Autowired
    private IJnReceivableService receivableService;

    @RequiresPermissions("erp:receivable:list")
    @GetMapping("/list")
    public TableDataInfo list(JnReceivable receivable) {
        startPage();
        List<JnReceivable> list = receivableService.selectList(receivable);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:receivable:query")
    @GetMapping("/{receivableId}")
    public AjaxResult getInfo(@PathVariable Long receivableId) {
        return success(receivableService.selectById(receivableId));
    }

    @RequiresPermissions("erp:receivable:add")
    @Log(title = "应收账款", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnReceivable receivable) {
        receivable.setCreateBy(SecurityUtils.getUsername());
        return toAjax(receivableService.insert(receivable));
    }

    @RequiresPermissions("erp:receivable:edit")
    @Log(title = "应收账款", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnReceivable receivable) {
        receivable.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(receivableService.update(receivable));
    }

    @RequiresPermissions("erp:receivable:remove")
    @Log(title = "应收账款", businessType = BusinessType.DELETE)
    @DeleteMapping("/{receivableIds}")
    public AjaxResult remove(@PathVariable Long[] receivableIds) {
        return toAjax(receivableService.deleteByIds(receivableIds));
    }

    @RequiresPermissions("erp:receivable:writeOff")
    @Log(title = "应收账款核销", businessType = BusinessType.UPDATE)
    @PutMapping("/{receivableId}/write-off")
    public AjaxResult writeOff(@PathVariable Long receivableId, @RequestBody Map<String, BigDecimal> body) {
        BigDecimal amount = body.get("amount");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return error("核销金额必须大于0");
        }
        receivableService.writeOff(receivableId, amount);
        return success();
    }

    @RequiresPermissions("erp:receivable:cancel")
    @Log(title = "应收账款取消", businessType = BusinessType.UPDATE)
    @PutMapping("/{receivableId}/cancel")
    public AjaxResult cancel(@PathVariable Long receivableId) {
        receivableService.cancel(receivableId);
        return success();
    }

    @RequiresPermissions("erp:receivable:list")
    @GetMapping("/aging")
    public AjaxResult agingAnalysis(@RequestParam(required = false) Long customerId) {
        Map<String, Object> result = receivableService.getAgingAnalysis(customerId);
        return success(result);
    }

    @RequiresPermissions("erp:receivable:list")
    @GetMapping("/overdue")
    public TableDataInfo overdue(@RequestParam(required = false, defaultValue = "0") Integer days) {
        startPage();
        List<JnReceivable> list = receivableService.selectList(null);
        return getDataTable(list);
    }

}
