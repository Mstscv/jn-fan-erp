package com.jn.erp.sales.customer.controller;

import com.jn.erp.sales.customer.domain.JnCustomer;
import com.jn.erp.sales.customer.service.IJnCustomerService;
import com.ruoyi.common.core.utils.PageUtils;
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

@RestController
@RequestMapping("/erp/customer")
public class JnCustomerController extends BaseController {

    @Autowired
    private IJnCustomerService jnCustomerService;

    @GetMapping("/list")
    public TableDataInfo list(JnCustomer customer) {
        startPage();
        List<JnCustomer> list = jnCustomerService.selectList(customer);
        return getDataTable(list);
    }

    @GetMapping("/{customerId}")
    public AjaxResult getInfo(@PathVariable Long customerId) {
        return success(jnCustomerService.getById(customerId));
    }

    @PostMapping
    public AjaxResult add(@RequestBody JnCustomer customer) {
        return toAjax(jnCustomerService.insert(customer));
    }

    @PutMapping
    public AjaxResult edit(@RequestBody JnCustomer customer) {
        return toAjax(jnCustomerService.update(customer));
    }

    @DeleteMapping("/{customerIds}")
    public AjaxResult remove(@PathVariable Long[] customerIds) {
        return toAjax(jnCustomerService.deleteByIds(customerIds));
    }
}
