package com.jn.erp.production.picking.controller;

import com.jn.erp.production.picking.domain.JnPicking;
import com.jn.erp.production.picking.domain.JnPickingLine;
import com.jn.erp.production.picking.service.IJnPickingService;
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
@RequestMapping("/erp/picking")
public class JnPickingController extends BaseController {

    @Autowired
    private IJnPickingService pickingService;

    @RequiresPermissions("erp:picking:list")
    @GetMapping("/list")
    public TableDataInfo list(JnPicking picking) {
        startPage();
        List<JnPicking> list = pickingService.selectList(picking);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:picking:query")
    @GetMapping("/{pickingId}")
    public AjaxResult getInfo(@PathVariable Long pickingId) {
        return success(pickingService.selectWithLines(pickingId));
    }

    @RequiresPermissions("erp:picking:add")
    @Log(title = "工单领料", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Map<String, Object> body) {
        JnPicking picking = new JnPicking();
        if (body.get("orderId") != null) {
            picking.setOrderId(Long.valueOf(body.get("orderId").toString()));
        }
        if (body.get("orderNo") != null) {
            picking.setOrderNo((String) body.get("orderNo"));
        }
        if (body.get("pickingType") != null) {
            picking.setPickingType((String) body.get("pickingType"));
        }
        if (body.get("totalQty") != null) {
            picking.setTotalQty(Integer.valueOf(body.get("totalQty").toString()));
        }
        if (body.get("warehouseId") != null) {
            picking.setWarehouseId(Long.valueOf(body.get("warehouseId").toString()));
        }
        if (body.get("remark") != null) {
            picking.setRemark((String) body.get("remark"));
        }

        @SuppressWarnings("unchecked")
        List<JnPickingLine> lines = body.get("lines") != null
                ? (List<JnPickingLine>) body.get("lines") : null;

        return toAjax(pickingService.insert(picking, lines));
    }

    @RequiresPermissions("erp:picking:edit")
    @Log(title = "工单领料", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Map<String, Object> body) {
        JnPicking picking = new JnPicking();
        if (body.get("pickingId") != null) {
            picking.setPickingId(Long.valueOf(body.get("pickingId").toString()));
        }
        if (body.get("orderId") != null) {
            picking.setOrderId(Long.valueOf(body.get("orderId").toString()));
        }
        if (body.get("orderNo") != null) {
            picking.setOrderNo((String) body.get("orderNo"));
        }
        if (body.get("pickingType") != null) {
            picking.setPickingType((String) body.get("pickingType"));
        }
        if (body.get("totalQty") != null) {
            picking.setTotalQty(Integer.valueOf(body.get("totalQty").toString()));
        }
        if (body.get("warehouseId") != null) {
            picking.setWarehouseId(Long.valueOf(body.get("warehouseId").toString()));
        }
        if (body.get("remark") != null) {
            picking.setRemark((String) body.get("remark"));
        }

        @SuppressWarnings("unchecked")
        List<JnPickingLine> lines = body.get("lines") != null
                ? (List<JnPickingLine>) body.get("lines") : null;

        return toAjax(pickingService.update(picking, lines));
    }

    @RequiresPermissions("erp:picking:edit")
    @Log(title = "工单领料", businessType = BusinessType.UPDATE)
    @PutMapping("/{pickingId}/approve")
    public AjaxResult approve(@PathVariable Long pickingId) {
        pickingService.approve(pickingId);
        return success();
    }

    @RequiresPermissions("erp:picking:edit")
    @Log(title = "工单领料", businessType = BusinessType.UPDATE)
    @PutMapping("/{pickingId}/reject")
    public AjaxResult reject(@PathVariable Long pickingId) {
        pickingService.reject(pickingId);
        return success();
    }

    @RequiresPermissions("erp:picking:remove")
    @Log(title = "工单领料", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(pickingService.deleteByIds(ids));
    }
}
