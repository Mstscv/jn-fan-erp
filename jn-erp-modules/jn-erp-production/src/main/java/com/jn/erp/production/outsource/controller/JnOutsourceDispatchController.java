package com.jn.erp.production.outsource.controller;

import com.jn.erp.production.outsource.domain.JnOutsourceDispatch;
import com.jn.erp.production.outsource.domain.JnOutsourceDispatchLine;
import com.jn.erp.production.outsource.service.IJnOutsourceDispatchService;
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
@RequestMapping("/erp/outsource-dispatch")
public class JnOutsourceDispatchController extends BaseController {

    @Autowired
    private IJnOutsourceDispatchService outsourceDispatchService;

    @RequiresPermissions("erp:outsource:dispatch:list")
    @GetMapping("/list")
    public TableDataInfo list(JnOutsourceDispatch outsourceDispatch) {
        startPage();
        List<JnOutsourceDispatch> list = outsourceDispatchService.selectList(outsourceDispatch);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:outsource:dispatch:query")
    @GetMapping("/{dispatchId}")
    public AjaxResult getInfo(@PathVariable Long dispatchId) {
        JnOutsourceDispatch dispatch = outsourceDispatchService.selectWithLines(dispatchId);
        return success(dispatch);
    }

    @RequiresPermissions("erp:outsource:dispatch:add")
    @Log(title = "委外发料", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Map<String, Object> body) {
        JnOutsourceDispatch dispatch = new JnOutsourceDispatch();
        if (body.get("orderId") != null) {
            dispatch.setOrderId(Long.valueOf(body.get("orderId").toString()));
        }
        if (body.get("orderNo") != null) {
            dispatch.setOrderNo((String) body.get("orderNo"));
        }
        if (body.get("supplierId") != null) {
            dispatch.setSupplierId(Long.valueOf(body.get("supplierId").toString()));
        }
        if (body.get("supplierName") != null) {
            dispatch.setSupplierName((String) body.get("supplierName"));
        }
        if (body.get("dispatchDate") != null) {
            dispatch.setDispatchDate(java.time.LocalDate.parse((String) body.get("dispatchDate")));
        }
        if (body.get("warehouseId") != null) {
            dispatch.setWarehouseId(Long.valueOf(body.get("warehouseId").toString()));
        }
        if (body.get("warehouseName") != null) {
            dispatch.setWarehouseName((String) body.get("warehouseName"));
        }
        if (body.get("totalQty") != null) {
            dispatch.setTotalQty(Integer.valueOf(body.get("totalQty").toString()));
        }
        if (body.get("remark") != null) {
            dispatch.setRemark((String) body.get("remark"));
        }

        @SuppressWarnings("unchecked")
        List<JnOutsourceDispatchLine> lines = body.get("lines") != null
                ? (List<JnOutsourceDispatchLine>) body.get("lines") : null;

        return toAjax(outsourceDispatchService.insert(dispatch, lines));
    }

    @RequiresPermissions("erp:outsource:dispatch:edit")
    @Log(title = "委外发料", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Map<String, Object> body) {
        JnOutsourceDispatch dispatch = new JnOutsourceDispatch();
        if (body.get("dispatchId") != null) {
            dispatch.setDispatchId(Long.valueOf(body.get("dispatchId").toString()));
        }
        if (body.get("orderId") != null) {
            dispatch.setOrderId(Long.valueOf(body.get("orderId").toString()));
        }
        if (body.get("orderNo") != null) {
            dispatch.setOrderNo((String) body.get("orderNo"));
        }
        if (body.get("supplierId") != null) {
            dispatch.setSupplierId(Long.valueOf(body.get("supplierId").toString()));
        }
        if (body.get("supplierName") != null) {
            dispatch.setSupplierName((String) body.get("supplierName"));
        }
        if (body.get("dispatchDate") != null) {
            dispatch.setDispatchDate(java.time.LocalDate.parse((String) body.get("dispatchDate")));
        }
        if (body.get("warehouseId") != null) {
            dispatch.setWarehouseId(Long.valueOf(body.get("warehouseId").toString()));
        }
        if (body.get("warehouseName") != null) {
            dispatch.setWarehouseName((String) body.get("warehouseName"));
        }
        if (body.get("totalQty") != null) {
            dispatch.setTotalQty(Integer.valueOf(body.get("totalQty").toString()));
        }
        if (body.get("remark") != null) {
            dispatch.setRemark((String) body.get("remark"));
        }

        @SuppressWarnings("unchecked")
        List<JnOutsourceDispatchLine> lines = body.get("lines") != null
                ? (List<JnOutsourceDispatchLine>) body.get("lines") : null;

        return toAjax(outsourceDispatchService.update(dispatch, lines));
    }

    @RequiresPermissions("erp:outsource:dispatch:edit")
    @Log(title = "委外发料", businessType = BusinessType.UPDATE)
    @PutMapping("/{dispatchId}/approve")
    public AjaxResult approve(@PathVariable Long dispatchId) {
        outsourceDispatchService.approve(dispatchId);
        return success();
    }

    @RequiresPermissions("erp:outsource:dispatch:remove")
    @Log(title = "委外发料", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(outsourceDispatchService.deleteByIds(ids));
    }

}
