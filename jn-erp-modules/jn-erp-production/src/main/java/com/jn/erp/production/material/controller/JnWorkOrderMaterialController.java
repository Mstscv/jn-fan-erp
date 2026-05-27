package com.jn.erp.production.material.controller;

import com.jn.erp.production.material.domain.JnWorkOrderMaterial;
import com.jn.erp.production.material.service.IJnWorkOrderMaterialService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/erp/work-order-material")
public class JnWorkOrderMaterialController extends BaseController {

    @Autowired
    private IJnWorkOrderMaterialService materialService;

    @RequiresPermissions("erp:wo_material:list")
    @GetMapping("/list/{orderId}")
    public AjaxResult list(@PathVariable Long orderId) {
        List<JnWorkOrderMaterial> list = materialService.selectByOrderId(orderId);
        return success(list);
    }

    @RequiresPermissions("erp:wo_material:explode")
    @Log(title = "工单物料需求展开", businessType = BusinessType.OTHER)
    @PostMapping("/explode/{orderId}")
    public AjaxResult explode(@PathVariable Long orderId) {
        List<JnWorkOrderMaterial> list = materialService.explodeBom(orderId);
        return success(list);
    }

    @RequiresPermissions("erp:wo_material:edit")
    @Log(title = "工单物料分配", businessType = BusinessType.UPDATE)
    @PutMapping("/allocate/{demandId}")
    public AjaxResult allocate(@PathVariable Long demandId, @RequestBody Map<String, Object> body) {
        BigDecimal qty = new BigDecimal(body.get("qty").toString());
        return toAjax(materialService.allocateMaterial(demandId, qty));
    }

    @RequiresPermissions("erp:wo_material:edit")
    @Log(title = "工单物料批量分配", businessType = BusinessType.UPDATE)
    @PostMapping("/batch-allocate/{orderId}")
    public AjaxResult batchAllocate(@PathVariable Long orderId) {
        return toAjax(materialService.batchAllocate(orderId));
    }

}
