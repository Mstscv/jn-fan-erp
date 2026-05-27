package com.jn.erp.production.defect.controller;

import com.jn.erp.production.defect.domain.JnReworkOrder;
import com.jn.erp.production.defect.service.IJnReworkOrderService;
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
@RequestMapping("/erp/rework-order")
public class JnReworkOrderController extends BaseController {

    @Autowired
    private IJnReworkOrderService reworkOrderService;

    @RequiresPermissions("erp:rework:list")
    @GetMapping("/list")
    public TableDataInfo list(JnReworkOrder rework) {
        startPage();
        List<JnReworkOrder> list = reworkOrderService.selectList(rework);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:rework:query")
    @GetMapping("/{reworkId}")
    public AjaxResult getInfo(@PathVariable Long reworkId) {
        return success(reworkOrderService.selectById(reworkId));
    }

    @RequiresPermissions("erp:rework:add")
    @Log(title = "返工单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnReworkOrder rework) {
        return toAjax(reworkOrderService.insert(rework));
    }

    @RequiresPermissions("erp:rework:add")
    @Log(title = "返工单", businessType = BusinessType.INSERT)
    @PostMapping("/from-defect/{defectId}")
    public AjaxResult createFromDefect(@PathVariable Long defectId) {
        return success(reworkOrderService.createFromDefect(defectId));
    }

    @RequiresPermissions("erp:rework:edit")
    @Log(title = "返工单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnReworkOrder rework) {
        return toAjax(reworkOrderService.update(rework));
    }

    @RequiresPermissions("erp:rework:edit")
    @Log(title = "返工单", businessType = BusinessType.UPDATE)
    @PutMapping("/{reworkId}/start")
    public AjaxResult start(@PathVariable Long reworkId) {
        reworkOrderService.start(reworkId);
        return success();
    }

    @RequiresPermissions("erp:rework:edit")
    @Log(title = "返工单", businessType = BusinessType.UPDATE)
    @PutMapping("/{reworkId}/complete")
    public AjaxResult complete(@PathVariable Long reworkId, @RequestBody Map<String, Object> body) {
        Integer actualQty = body.get("actualQty") != null ? Integer.valueOf(body.get("actualQty").toString()) : null;
        reworkOrderService.complete(reworkId, actualQty);
        return success();
    }

    @RequiresPermissions("erp:rework:edit")
    @Log(title = "返工单", businessType = BusinessType.UPDATE)
    @PutMapping("/{reworkId}/cancel")
    public AjaxResult cancel(@PathVariable Long reworkId, @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        reworkOrderService.cancel(reworkId, reason);
        return success();
    }

    @RequiresPermissions("erp:rework:remove")
    @Log(title = "返工单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(reworkOrderService.deleteByIds(ids));
    }
}
