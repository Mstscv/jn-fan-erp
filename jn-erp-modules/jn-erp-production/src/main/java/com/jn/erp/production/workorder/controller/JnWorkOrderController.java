package com.jn.erp.production.workorder.controller;

import com.jn.erp.production.workorder.domain.JnWorkOrder;
import com.jn.erp.production.workorder.domain.JnWorkOrderLine;
import com.jn.erp.production.workorder.service.IJnWorkOrderService;
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
import java.util.Map;

@RestController
@RequestMapping("/erp/work-order")
public class JnWorkOrderController extends BaseController {

    @Autowired
    private IJnWorkOrderService workOrderService;

    @RequiresPermissions("erp:workorder:list")
    @GetMapping("/list")
    public TableDataInfo list(JnWorkOrder workOrder) {
        startPage();
        List<JnWorkOrder> list = workOrderService.selectList(workOrder);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:workorder:query")
    @GetMapping("/{orderId}")
    public AjaxResult getInfo(@PathVariable Long orderId) {
        JnWorkOrder order = workOrderService.selectWithLines(orderId);
        return success(order);
    }

    @RequiresPermissions("erp:workorder:add")
    @Log(title = "生产工单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Map<String, Object> body) {
        JnWorkOrder order = new JnWorkOrder();
        if (body.get("productId") != null) {
            order.setProductId(Long.valueOf(body.get("productId").toString()));
        }
        if (body.get("productCode") != null) {
            order.setProductCode((String) body.get("productCode"));
        }
        if (body.get("productName") != null) {
            order.setProductName((String) body.get("productName"));
        }
        if (body.get("productSpec") != null) {
            order.setProductSpec((String) body.get("productSpec"));
        }
        if (body.get("quantity") != null) {
            order.setQuantity(Integer.valueOf(body.get("quantity").toString()));
        }
        if (body.get("unit") != null) {
            order.setUnit((String) body.get("unit"));
        }
        if (body.get("bomId") != null) {
            order.setBomId(Long.valueOf(body.get("bomId").toString()));
        }
        if (body.get("bomVersion") != null) {
            order.setBomVersion((String) body.get("bomVersion"));
        }
        if (body.get("routingId") != null) {
            order.setRoutingId(Long.valueOf(body.get("routingId").toString()));
        }
        if (body.get("salesOrderId") != null) {
            order.setSalesOrderId(Long.valueOf(body.get("salesOrderId").toString()));
        }
        if (body.get("salesOrderNo") != null) {
            order.setSalesOrderNo((String) body.get("salesOrderNo"));
        }
        if (body.get("customerId") != null) {
            order.setCustomerId(Long.valueOf(body.get("customerId").toString()));
        }
        if (body.get("customerName") != null) {
            order.setCustomerName((String) body.get("customerName"));
        }
        if (body.get("priority") != null) {
            order.setPriority((String) body.get("priority"));
        }
        if (body.get("plannedStart") != null) {
            order.setPlannedStart(java.time.LocalDate.parse((String) body.get("plannedStart")));
        }
        if (body.get("plannedEnd") != null) {
            order.setPlannedEnd(java.time.LocalDate.parse((String) body.get("plannedEnd")));
        }
        if (body.get("remark") != null) {
            order.setRemark((String) body.get("remark"));
        }

        @SuppressWarnings("unchecked")
        List<JnWorkOrderLine> lines = body.get("lines") != null
                ? (List<JnWorkOrderLine>) body.get("lines") : null;

        return toAjax(workOrderService.insert(order, lines));
    }

    @RequiresPermissions("erp:workorder:edit")
    @Log(title = "生产工单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Map<String, Object> body) {
        JnWorkOrder order = new JnWorkOrder();
        if (body.get("orderId") != null) {
            order.setOrderId(Long.valueOf(body.get("orderId").toString()));
        }
        if (body.get("productId") != null) {
            order.setProductId(Long.valueOf(body.get("productId").toString()));
        }
        if (body.get("productCode") != null) {
            order.setProductCode((String) body.get("productCode"));
        }
        if (body.get("productName") != null) {
            order.setProductName((String) body.get("productName"));
        }
        if (body.get("productSpec") != null) {
            order.setProductSpec((String) body.get("productSpec"));
        }
        if (body.get("quantity") != null) {
            order.setQuantity(Integer.valueOf(body.get("quantity").toString()));
        }
        if (body.get("unit") != null) {
            order.setUnit((String) body.get("unit"));
        }
        if (body.get("bomId") != null) {
            order.setBomId(Long.valueOf(body.get("bomId").toString()));
        }
        if (body.get("bomVersion") != null) {
            order.setBomVersion((String) body.get("bomVersion"));
        }
        if (body.get("routingId") != null) {
            order.setRoutingId(Long.valueOf(body.get("routingId").toString()));
        }
        if (body.get("salesOrderId") != null) {
            order.setSalesOrderId(Long.valueOf(body.get("salesOrderId").toString()));
        }
        if (body.get("salesOrderNo") != null) {
            order.setSalesOrderNo((String) body.get("salesOrderNo"));
        }
        if (body.get("customerId") != null) {
            order.setCustomerId(Long.valueOf(body.get("customerId").toString()));
        }
        if (body.get("customerName") != null) {
            order.setCustomerName((String) body.get("customerName"));
        }
        if (body.get("priority") != null) {
            order.setPriority((String) body.get("priority"));
        }
        if (body.get("plannedStart") != null) {
            order.setPlannedStart(java.time.LocalDate.parse((String) body.get("plannedStart")));
        }
        if (body.get("plannedEnd") != null) {
            order.setPlannedEnd(java.time.LocalDate.parse((String) body.get("plannedEnd")));
        }
        if (body.get("remark") != null) {
            order.setRemark((String) body.get("remark"));
        }

        @SuppressWarnings("unchecked")
        List<JnWorkOrderLine> lines = body.get("lines") != null
                ? (List<JnWorkOrderLine>) body.get("lines") : null;

        return toAjax(workOrderService.update(order, lines));
    }

    @RequiresPermissions("erp:workorder:remove")
    @Log(title = "生产工单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds) {
        return toAjax(workOrderService.deleteByIds(orderIds));
    }

    @RequiresPermissions("erp:workorder:edit")
    @Log(title = "生产工单管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{orderId}/status")
    public AjaxResult updateStatus(@PathVariable Long orderId, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        workOrderService.updateStatus(orderId, newStatus);
        return success();
    }

    @RequiresPermissions("erp:workorder:list")
    @GetMapping("/kanban")
    public AjaxResult kanban() {
        return success(workOrderService.getKanbanData());
    }

    @RequiresPermissions("erp:workorder:list")
    @GetMapping("/pending-scheduling")
    public AjaxResult pendingScheduling() {
        return success(workOrderService.getPendingScheduling());
    }

}
