package com.jn.erp.production.outsource.controller;

import com.jn.erp.production.outsource.domain.JnOutsourceOrder;
import com.jn.erp.production.outsource.domain.JnOutsourceOrderLine;
import com.jn.erp.production.outsource.service.IJnOutsourceOrderService;
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
@RequestMapping("/erp/outsource-order")
public class JnOutsourceOrderController extends BaseController {

    @Autowired
    private IJnOutsourceOrderService outsourceOrderService;

    @RequiresPermissions("erp:outsource:order:list")
    @GetMapping("/list")
    public TableDataInfo list(JnOutsourceOrder outsourceOrder) {
        startPage();
        List<JnOutsourceOrder> list = outsourceOrderService.selectList(outsourceOrder);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:outsource:order:query")
    @GetMapping("/{orderId}")
    public AjaxResult getInfo(@PathVariable Long orderId) {
        JnOutsourceOrder order = outsourceOrderService.selectWithLines(orderId);
        return success(order);
    }

    @RequiresPermissions("erp:outsource:order:add")
    @Log(title = "委外加工订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Map<String, Object> body) {
        JnOutsourceOrder order = new JnOutsourceOrder();
        if (body.get("supplierId") != null) {
            order.setSupplierId(Long.valueOf(body.get("supplierId").toString()));
        }
        if (body.get("supplierName") != null) {
            order.setSupplierName((String) body.get("supplierName"));
        }
        if (body.get("supplierContact") != null) {
            order.setSupplierContact((String) body.get("supplierContact"));
        }
        if (body.get("supplierPhone") != null) {
            order.setSupplierPhone((String) body.get("supplierPhone"));
        }
        if (body.get("processType") != null) {
            order.setProcessType((String) body.get("processType"));
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
        if (body.get("orderQty") != null) {
            order.setOrderQty(Integer.valueOf(body.get("orderQty").toString()));
        }
        if (body.get("unitPrice") != null) {
            order.setUnitPrice(new java.math.BigDecimal(body.get("unitPrice").toString()));
        }
        if (body.get("totalAmount") != null) {
            order.setTotalAmount(new java.math.BigDecimal(body.get("totalAmount").toString()));
        }
        if (body.get("deliveryDate") != null) {
            order.setDeliveryDate(java.time.LocalDate.parse((String) body.get("deliveryDate")));
        }
        if (body.get("orderDate") != null) {
            order.setOrderDate(java.time.LocalDate.parse((String) body.get("orderDate")));
        }
        if (body.get("qualityRequirement") != null) {
            order.setQualityRequirement((String) body.get("qualityRequirement"));
        }
        if (body.get("remark") != null) {
            order.setRemark((String) body.get("remark"));
        }

        @SuppressWarnings("unchecked")
        List<JnOutsourceOrderLine> lines = body.get("lines") != null
                ? (List<JnOutsourceOrderLine>) body.get("lines") : null;

        return toAjax(outsourceOrderService.insert(order, lines));
    }

    @RequiresPermissions("erp:outsource:order:edit")
    @Log(title = "委外加工订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Map<String, Object> body) {
        JnOutsourceOrder order = new JnOutsourceOrder();
        if (body.get("orderId") != null) {
            order.setOrderId(Long.valueOf(body.get("orderId").toString()));
        }
        if (body.get("supplierId") != null) {
            order.setSupplierId(Long.valueOf(body.get("supplierId").toString()));
        }
        if (body.get("supplierName") != null) {
            order.setSupplierName((String) body.get("supplierName"));
        }
        if (body.get("supplierContact") != null) {
            order.setSupplierContact((String) body.get("supplierContact"));
        }
        if (body.get("supplierPhone") != null) {
            order.setSupplierPhone((String) body.get("supplierPhone"));
        }
        if (body.get("processType") != null) {
            order.setProcessType((String) body.get("processType"));
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
        if (body.get("orderQty") != null) {
            order.setOrderQty(Integer.valueOf(body.get("orderQty").toString()));
        }
        if (body.get("unitPrice") != null) {
            order.setUnitPrice(new java.math.BigDecimal(body.get("unitPrice").toString()));
        }
        if (body.get("totalAmount") != null) {
            order.setTotalAmount(new java.math.BigDecimal(body.get("totalAmount").toString()));
        }
        if (body.get("deliveryDate") != null) {
            order.setDeliveryDate(java.time.LocalDate.parse((String) body.get("deliveryDate")));
        }
        if (body.get("orderDate") != null) {
            order.setOrderDate(java.time.LocalDate.parse((String) body.get("orderDate")));
        }
        if (body.get("qualityRequirement") != null) {
            order.setQualityRequirement((String) body.get("qualityRequirement"));
        }
        if (body.get("remark") != null) {
            order.setRemark((String) body.get("remark"));
        }

        @SuppressWarnings("unchecked")
        List<JnOutsourceOrderLine> lines = body.get("lines") != null
                ? (List<JnOutsourceOrderLine>) body.get("lines") : null;

        return toAjax(outsourceOrderService.update(order, lines));
    }

    @RequiresPermissions("erp:outsource:order:remove")
    @Log(title = "委外加工订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds) {
        return toAjax(outsourceOrderService.deleteByIds(orderIds));
    }

    @RequiresPermissions("erp:outsource:order:edit")
    @Log(title = "委外加工订单", businessType = BusinessType.UPDATE)
    @PutMapping("/{orderId}/submit")
    public AjaxResult submit(@PathVariable Long orderId) {
        outsourceOrderService.submit(orderId);
        return success();
    }

    @RequiresPermissions("erp:outsource:order:edit")
    @Log(title = "委外加工订单", businessType = BusinessType.UPDATE)
    @PutMapping("/{orderId}/approve")
    public AjaxResult approve(@PathVariable Long orderId) {
        outsourceOrderService.approve(orderId);
        return success();
    }

    @RequiresPermissions("erp:outsource:order:edit")
    @Log(title = "委外加工订单", businessType = BusinessType.UPDATE)
    @PutMapping("/{orderId}/reject")
    public AjaxResult reject(@PathVariable Long orderId, @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        outsourceOrderService.reject(orderId, reason);
        return success();
    }

    @RequiresPermissions("erp:outsource:order:edit")
    @Log(title = "委外加工订单", businessType = BusinessType.UPDATE)
    @PutMapping("/{orderId}/complete")
    public AjaxResult complete(@PathVariable Long orderId) {
        outsourceOrderService.complete(orderId);
        return success();
    }

}
