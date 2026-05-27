package com.jn.erp.purchase.po.controller;

import com.jn.erp.purchase.po.domain.JnPoLine;
import com.jn.erp.purchase.po.domain.JnPurchaseOrder;
import com.jn.erp.purchase.po.service.IJnPurchaseOrderService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.utils.SecurityUtils;
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
import java.util.Map;

@RestController
@RequestMapping("/erp/purchase-order")
public class JnPurchaseOrderController extends BaseController {

    @Autowired
    private IJnPurchaseOrderService purchaseOrderService;

    @GetMapping("/list")
    public TableDataInfo list(JnPurchaseOrder order) {
        startPage();
        List<JnPurchaseOrder> list = purchaseOrderService.selectList(order);
        return getDataTable(list);
    }

    @GetMapping("/{poId}")
    public AjaxResult getInfo(@PathVariable Long poId) {
        return success(purchaseOrderService.getByIdWithLines(poId));
    }

    @Log(title = "采购订单", businessType = BusinessType.INSERT)
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult add(@RequestBody Map<String, Object> params) {
        JnPurchaseOrder order = new JnPurchaseOrder();
        order.setSupplierId(Long.valueOf(params.get("supplierId").toString()));
        order.setSupplierName((String) params.get("supplierName"));
        order.setBuyer((String) params.get("buyer"));
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
        order.setRemark((String) params.get("remark"));
        order.setCreateBy(SecurityUtils.getUsername());

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> linesData = (List<Map<String, Object>>) params.get("lines");
        List<JnPoLine> lines = new java.util.ArrayList<>();
        if (linesData != null) {
            for (Map<String, Object> lineMap : linesData) {
                JnPoLine line = new JnPoLine();
                if (lineMap.get("materialId") != null) {
                    line.setMaterialId(Long.valueOf(lineMap.get("materialId").toString()));
                }
                line.setMaterialCode((String) lineMap.get("materialCode"));
                line.setMaterialName((String) lineMap.get("materialName"));
                line.setSpec((String) lineMap.get("spec"));
                if (lineMap.get("quantity") != null) {
                    line.setQuantity(Integer.valueOf(lineMap.get("quantity").toString()));
                }
                if (lineMap.get("unitPrice") != null) {
                    line.setUnitPrice(new java.math.BigDecimal(lineMap.get("unitPrice").toString()));
                }
                line.setRemark((String) lineMap.get("remark"));
                lines.add(line);
            }
        }

        return toAjax(purchaseOrderService.insertWithLines(order, lines));
    }

    @Log(title = "采购订单", businessType = BusinessType.UPDATE)
    @PutMapping
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult edit(@RequestBody Map<String, Object> params) {
        JnPurchaseOrder order = new JnPurchaseOrder();
        order.setPoId(Long.valueOf(params.get("poId").toString()));
        if (params.get("supplierId") != null) {
            order.setSupplierId(Long.valueOf(params.get("supplierId").toString()));
        }
        order.setSupplierName((String) params.get("supplierName"));
        order.setBuyer((String) params.get("buyer"));
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
        order.setRemark((String) params.get("remark"));
        order.setUpdateBy(SecurityUtils.getUsername());

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> linesData = (List<Map<String, Object>>) params.get("lines");
        List<JnPoLine> lines = new java.util.ArrayList<>();
        if (linesData != null) {
            for (Map<String, Object> lineMap : linesData) {
                JnPoLine line = new JnPoLine();
                if (lineMap.get("materialId") != null) {
                    line.setMaterialId(Long.valueOf(lineMap.get("materialId").toString()));
                }
                line.setMaterialCode((String) lineMap.get("materialCode"));
                line.setMaterialName((String) lineMap.get("materialName"));
                line.setSpec((String) lineMap.get("spec"));
                if (lineMap.get("quantity") != null) {
                    line.setQuantity(Integer.valueOf(lineMap.get("quantity").toString()));
                }
                if (lineMap.get("unitPrice") != null) {
                    line.setUnitPrice(new java.math.BigDecimal(lineMap.get("unitPrice").toString()));
                }
                line.setRemark((String) lineMap.get("remark"));
                lines.add(line);
            }
        }

        return toAjax(purchaseOrderService.updateWithLines(order, lines));
    }

    @Log(title = "采购订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{poIds}")
    public AjaxResult remove(@PathVariable Long[] poIds) {
        return toAjax(purchaseOrderService.deleteByIds(poIds));
    }

    @Log(title = "采购订单状态", businessType = BusinessType.UPDATE)
    @PutMapping("/{poId}/status")
    public AjaxResult updateStatus(@PathVariable Long poId, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return toAjax(purchaseOrderService.updateStatus(poId, status));
    }

    @Log(title = "采购订单审批", businessType = BusinessType.UPDATE)
    @PutMapping("/{poId}/approve")
    public AjaxResult approve(@PathVariable Long poId) {
        return toAjax(purchaseOrderService.approve(poId, SecurityUtils.getUsername()));
    }

    @Log(title = "采购订单审批", businessType = BusinessType.UPDATE)
    @PutMapping("/{poId}/reject")
    public AjaxResult reject(@PathVariable Long poId) {
        return toAjax(purchaseOrderService.reject(poId, SecurityUtils.getUsername()));
    }
}
