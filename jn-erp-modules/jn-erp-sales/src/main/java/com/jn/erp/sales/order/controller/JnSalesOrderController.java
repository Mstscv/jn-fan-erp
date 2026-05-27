package com.jn.erp.sales.order.controller;

import com.jn.erp.sales.order.domain.JnOrderLine;
import com.jn.erp.sales.order.domain.JnSalesOrder;
import com.jn.erp.sales.order.service.IJnSalesOrderService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/erp/sales-order")
public class JnSalesOrderController extends BaseController {

    @Autowired
    private IJnSalesOrderService jnSalesOrderService;

    @GetMapping("/list")
    public TableDataInfo list(JnSalesOrder order) {
        startPage();
        List<JnSalesOrder> list = jnSalesOrderService.selectList(order);
        return getDataTable(list);
    }

    @GetMapping("/{orderId}")
    public AjaxResult getInfo(@PathVariable Long orderId) {
        JnSalesOrder order = jnSalesOrderService.getById(orderId);
        List<JnOrderLine> lines = jnSalesOrderService.getLinesByOrderId(orderId);
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("lines", lines);
        return success(result);
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult add(@RequestBody Map<String, Object> params) {
        JnSalesOrder order = new JnSalesOrder();
        order.setQuotationId(params.get("quotationId") != null ? Long.valueOf(params.get("quotationId").toString()) : null);
        order.setCustomerId(Long.valueOf(params.get("customerId").toString()));
        order.setCustomerName((String) params.get("customerName"));
        order.setSalesman((String) params.get("salesman"));
        if (params.get("orderDate") != null) {
            order.setOrderDate(java.time.LocalDate.parse((String) params.get("orderDate")));
        }
        if (params.get("deliveryDate") != null) {
            order.setDeliveryDate(java.time.LocalDate.parse((String) params.get("deliveryDate")));
        }
        if (params.get("totalAmount") != null) {
            order.setTotalAmount(new java.math.BigDecimal(params.get("totalAmount").toString()));
        }
        order.setPaymentTerms((String) params.get("paymentTerms"));
        order.setDeliveryAddr((String) params.get("deliveryAddr"));
        order.setRemark((String) params.get("remark"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> lineMaps = (List<Map<String, Object>>) params.get("lines");
        List<JnOrderLine> lines = new java.util.ArrayList<>();
        if (lineMaps != null) {
            for (Map<String, Object> lineMap : lineMaps) {
                JnOrderLine line = new JnOrderLine();
                line.setMaterialId(Long.valueOf(lineMap.get("materialId").toString()));
                line.setMaterialCode((String) lineMap.get("materialCode"));
                line.setMaterialName((String) lineMap.get("materialName"));
                line.setSpec((String) lineMap.get("spec"));
                if (lineMap.get("quantity") != null) {
                    line.setQuantity(Integer.valueOf(lineMap.get("quantity").toString()));
                }
                line.setUnitPrice(new java.math.BigDecimal(lineMap.get("unitPrice").toString()));
                if (lineMap.get("amount") != null) {
                    line.setAmount(new java.math.BigDecimal(lineMap.get("amount").toString()));
                }
                line.setRemark((String) lineMap.get("remark"));
                lines.add(line);
            }
        }

        return toAjax(jnSalesOrderService.insertWithLines(order, lines));
    }

    @PutMapping
    public AjaxResult edit(@RequestBody JnSalesOrder order) {
        return toAjax(jnSalesOrderService.update(order));
    }

    @PutMapping("/{orderId}/status")
    public AjaxResult updateStatus(@PathVariable Long orderId, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        if (newStatus == null || newStatus.isEmpty()) {
            return error("状态不能为空");
        }
        return toAjax(jnSalesOrderService.updateStatus(orderId, newStatus));
    }

    @DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds) {
        return toAjax(jnSalesOrderService.deleteByIds(orderIds));
    }
}
