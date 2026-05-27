package com.jn.erp.sales.ret.controller;

import com.jn.erp.sales.ret.domain.JnSalesReturn;
import com.jn.erp.sales.ret.service.IJnSalesReturnService;
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
import java.util.Map;

@RestController
@RequestMapping("/erp/sales-return")
public class JnSalesReturnController extends BaseController {

    @Autowired
    private IJnSalesReturnService jnSalesReturnService;

    @GetMapping("/list")
    public TableDataInfo list(JnSalesReturn salesReturn) {
        startPage();
        List<JnSalesReturn> list = jnSalesReturnService.selectList(salesReturn);
        return getDataTable(list);
    }

    @GetMapping("/{returnId}")
    public AjaxResult getInfo(@PathVariable Long returnId) {
        return success(jnSalesReturnService.getById(returnId));
    }

    @PostMapping
    public AjaxResult add(@RequestBody JnSalesReturn salesReturn) {
        return toAjax(jnSalesReturnService.insert(salesReturn));
    }

    @PutMapping
    public AjaxResult edit(@RequestBody JnSalesReturn salesReturn) {
        return toAjax(jnSalesReturnService.update(salesReturn));
    }

    @DeleteMapping("/{returnIds}")
    public AjaxResult remove(@PathVariable Long[] returnIds) {
        return toAjax(jnSalesReturnService.deleteByIds(returnIds));
    }

    @PutMapping("/{returnId}/status")
    public AjaxResult updateStatus(@PathVariable Long returnId, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        if (newStatus == null || newStatus.isEmpty()) {
            return error("状态不能为空");
        }
        return toAjax(jnSalesReturnService.updateStatus(returnId, newStatus));
    }
}
