package com.jn.erp.finance.costing.controller;

import com.jn.erp.finance.costing.domain.JnWorkOrderCost;
import com.jn.erp.finance.costing.service.IJnCostingService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/erp/costing")
public class JnCostingController extends BaseController {

    @Autowired
    private IJnCostingService costingService;

    @RequiresPermissions("erp:costing:list")
    @GetMapping("/list")
    public TableDataInfo list(JnWorkOrderCost cost) {
        startPage();
        List<JnWorkOrderCost> list = costingService.selectList(cost);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:costing:query")
    @GetMapping("/{costId}")
    public AjaxResult getInfo(@PathVariable Long costId) {
        return success(costingService.selectById(costId));
    }

    @RequiresPermissions("erp:costing:calculate")
    @Log(title = "工单成本核算", businessType = BusinessType.OTHER)
    @PostMapping("/calculate/{orderId}")
    public AjaxResult calculate(@PathVariable Long orderId) {
        return toAjax(costingService.calculateWorkOrderCost(orderId));
    }

    @RequiresPermissions("erp:costing:calculate")
    @Log(title = "工单成本核算-批量", businessType = BusinessType.OTHER)
    @PostMapping("/batch-calculate")
    public AjaxResult batchCalculate(@RequestBody List<Long> orderIds) {
        return toAjax(costingService.batchCalculateWorkOrderCosts(orderIds));
    }

    @RequiresPermissions("erp:costing:list")
    @GetMapping("/product-summary")
    public AjaxResult productSummary(@RequestParam(required = false) Long productId,
                                     @RequestParam(required = false) LocalDate startDate,
                                     @RequestParam(required = false) LocalDate endDate) {
        List<Map<String, Object>> list = costingService.getProductCostSummary(productId, startDate, endDate);
        return success(list);
    }

    @RequiresPermissions("erp:costing:approve")
    @Log(title = "成本核算审批", businessType = BusinessType.UPDATE)
    @PutMapping("/{costId}/approve")
    public AjaxResult approve(@PathVariable Long costId) {
        return toAjax(costingService.approve(costId));
    }

    @RequiresPermissions("erp:costing:remove")
    @Log(title = "工单成本核算", businessType = BusinessType.DELETE)
    @DeleteMapping("/{costIds}")
    public AjaxResult remove(@PathVariable Long[] costIds) {
        return toAjax(costingService.deleteByIds(costIds));
    }
}
