package com.jn.erp.sales.quotation.controller;

import com.jn.erp.sales.quotation.domain.JnSalesQuotation;
import com.jn.erp.sales.quotation.service.IJnSalesQuotationService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
@RequestMapping("/erp/quotation")
public class JnSalesQuotationController extends BaseController {

    @Autowired
    private IJnSalesQuotationService jnSalesQuotationService;

    @GetMapping("/list")
    public TableDataInfo list(JnSalesQuotation quotation) {
        startPage();
        List<JnSalesQuotation> list = jnSalesQuotationService.selectList(quotation);
        return getDataTable(list);
    }

    @GetMapping("/{quotationId}")
    public AjaxResult getInfo(@PathVariable Long quotationId) {
        return success(jnSalesQuotationService.getById(quotationId));
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult add(@RequestBody JnSalesQuotation quotation) {
        return toAjax(jnSalesQuotationService.insertWithLines(quotation, quotation.getLines()));
    }

    @PutMapping
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult edit(@RequestBody JnSalesQuotation quotation) {
        return toAjax(jnSalesQuotationService.updateWithLines(quotation, quotation.getLines()));
    }

    @DeleteMapping("/{quotationIds}")
    public AjaxResult remove(@PathVariable Long[] quotationIds) {
        return toAjax(jnSalesQuotationService.deleteByIds(quotationIds));
    }

    @PutMapping("/{quotationId}/approve")
    public AjaxResult approve(@PathVariable Long quotationId) {
        jnSalesQuotationService.approve(quotationId);
        return success();
    }
}
