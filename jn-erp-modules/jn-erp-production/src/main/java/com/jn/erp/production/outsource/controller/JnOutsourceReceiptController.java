package com.jn.erp.production.outsource.controller;

import com.jn.erp.production.outsource.domain.JnOutsourceReceipt;
import com.jn.erp.production.outsource.domain.JnOutsourceReceiptLine;
import com.jn.erp.production.outsource.service.IJnOutsourceReceiptService;
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
@RequestMapping("/erp/outsource-receipt")
public class JnOutsourceReceiptController extends BaseController {

    @Autowired
    private IJnOutsourceReceiptService outsourceReceiptService;

    @RequiresPermissions("erp:outsource:receipt:list")
    @GetMapping("/list")
    public TableDataInfo list(JnOutsourceReceipt outsourceReceipt) {
        startPage();
        List<JnOutsourceReceipt> list = outsourceReceiptService.selectList(outsourceReceipt);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:outsource:receipt:query")
    @GetMapping("/{receiptId}")
    public AjaxResult getInfo(@PathVariable Long receiptId) {
        JnOutsourceReceipt receipt = outsourceReceiptService.selectWithLines(receiptId);
        return success(receipt);
    }

    @RequiresPermissions("erp:outsource:receipt:add")
    @Log(title = "委外收货", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Map<String, Object> body) {
        JnOutsourceReceipt receipt = new JnOutsourceReceipt();
        if (body.get("orderId") != null) {
            receipt.setOrderId(Long.valueOf(body.get("orderId").toString()));
        }
        if (body.get("orderNo") != null) {
            receipt.setOrderNo((String) body.get("orderNo"));
        }
        if (body.get("supplierId") != null) {
            receipt.setSupplierId(Long.valueOf(body.get("supplierId").toString()));
        }
        if (body.get("supplierName") != null) {
            receipt.setSupplierName((String) body.get("supplierName"));
        }
        if (body.get("receiptDate") != null) {
            receipt.setReceiptDate(java.time.LocalDate.parse((String) body.get("receiptDate")));
        }
        if (body.get("warehouseId") != null) {
            receipt.setWarehouseId(Long.valueOf(body.get("warehouseId").toString()));
        }
        if (body.get("warehouseName") != null) {
            receipt.setWarehouseName((String) body.get("warehouseName"));
        }
        if (body.get("inspectionId") != null) {
            receipt.setInspectionId(Long.valueOf(body.get("inspectionId").toString()));
        }
        if (body.get("totalQty") != null) {
            receipt.setTotalQty(Integer.valueOf(body.get("totalQty").toString()));
        }
        if (body.get("totalAmount") != null) {
            receipt.setTotalAmount(new java.math.BigDecimal(body.get("totalAmount").toString()));
        }
        if (body.get("remark") != null) {
            receipt.setRemark((String) body.get("remark"));
        }

        @SuppressWarnings("unchecked")
        List<JnOutsourceReceiptLine> lines = body.get("lines") != null
                ? (List<JnOutsourceReceiptLine>) body.get("lines") : null;

        return toAjax(outsourceReceiptService.insert(receipt, lines));
    }

    @RequiresPermissions("erp:outsource:receipt:edit")
    @Log(title = "委外收货", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Map<String, Object> body) {
        JnOutsourceReceipt receipt = new JnOutsourceReceipt();
        if (body.get("receiptId") != null) {
            receipt.setReceiptId(Long.valueOf(body.get("receiptId").toString()));
        }
        if (body.get("orderId") != null) {
            receipt.setOrderId(Long.valueOf(body.get("orderId").toString()));
        }
        if (body.get("orderNo") != null) {
            receipt.setOrderNo((String) body.get("orderNo"));
        }
        if (body.get("supplierId") != null) {
            receipt.setSupplierId(Long.valueOf(body.get("supplierId").toString()));
        }
        if (body.get("supplierName") != null) {
            receipt.setSupplierName((String) body.get("supplierName"));
        }
        if (body.get("receiptDate") != null) {
            receipt.setReceiptDate(java.time.LocalDate.parse((String) body.get("receiptDate")));
        }
        if (body.get("warehouseId") != null) {
            receipt.setWarehouseId(Long.valueOf(body.get("warehouseId").toString()));
        }
        if (body.get("warehouseName") != null) {
            receipt.setWarehouseName((String) body.get("warehouseName"));
        }
        if (body.get("inspectionId") != null) {
            receipt.setInspectionId(Long.valueOf(body.get("inspectionId").toString()));
        }
        if (body.get("totalQty") != null) {
            receipt.setTotalQty(Integer.valueOf(body.get("totalQty").toString()));
        }
        if (body.get("totalAmount") != null) {
            receipt.setTotalAmount(new java.math.BigDecimal(body.get("totalAmount").toString()));
        }
        if (body.get("remark") != null) {
            receipt.setRemark((String) body.get("remark"));
        }

        @SuppressWarnings("unchecked")
        List<JnOutsourceReceiptLine> lines = body.get("lines") != null
                ? (List<JnOutsourceReceiptLine>) body.get("lines") : null;

        return toAjax(outsourceReceiptService.update(receipt, lines));
    }

    @RequiresPermissions("erp:outsource:receipt:edit")
    @Log(title = "委外收货", businessType = BusinessType.UPDATE)
    @PutMapping("/{receiptId}/approve")
    public AjaxResult approve(@PathVariable Long receiptId) {
        outsourceReceiptService.approve(receiptId);
        return success();
    }

    @RequiresPermissions("erp:outsource:receipt:edit")
    @Log(title = "委外收货", businessType = BusinessType.UPDATE)
    @PutMapping("/{receiptId}/reject")
    public AjaxResult reject(@PathVariable Long receiptId, @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        outsourceReceiptService.reject(receiptId, reason);
        return success();
    }

    @RequiresPermissions("erp:outsource:receipt:remove")
    @Log(title = "委外收货", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(outsourceReceiptService.deleteByIds(ids));
    }

}
