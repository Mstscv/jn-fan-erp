package com.jn.erp.production.handover.controller;

import com.jn.erp.production.handover.domain.JnHandover;
import com.jn.erp.production.handover.service.IJnHandoverService;
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
@RequestMapping("/erp/handover")
public class JnHandoverController extends BaseController {

    @Autowired
    private IJnHandoverService handoverService;

    @RequiresPermissions("erp:handover:list")
    @GetMapping("/list")
    public TableDataInfo list(JnHandover handover) {
        startPage();
        List<JnHandover> list = handoverService.selectList(handover);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:handover:query")
    @GetMapping("/{handoverId}")
    public AjaxResult getInfo(@PathVariable Long handoverId) {
        return success(handoverService.selectById(handoverId));
    }

    @RequiresPermissions("erp:handover:query")
    @GetMapping("/order/{orderId}")
    public AjaxResult getByOrder(@PathVariable Long orderId) {
        return success(handoverService.selectByOrderId(orderId));
    }

    @RequiresPermissions("erp:handover:add")
    @Log(title = "工序交接单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnHandover handover) {
        return toAjax(handoverService.insert(handover));
    }

    @RequiresPermissions("erp:handover:edit")
    @Log(title = "工序交接单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnHandover handover) {
        return toAjax(handoverService.update(handover));
    }

    @RequiresPermissions("erp:handover:edit")
    @Log(title = "工序交接单", businessType = BusinessType.UPDATE)
    @PutMapping("/{handoverId}/confirm")
    public AjaxResult confirm(@PathVariable Long handoverId, @RequestBody Map<String, Object> body) {
        Integer actualQty = body.get("actualQty") != null ? Integer.valueOf(body.get("actualQty").toString()) : 0;
        Integer defectQty = body.get("defectQty") != null ? Integer.valueOf(body.get("defectQty").toString()) : 0;
        handoverService.confirm(handoverId, actualQty, defectQty);
        return success();
    }

    @RequiresPermissions("erp:handover:edit")
    @Log(title = "工序交接单", businessType = BusinessType.UPDATE)
    @PutMapping("/{handoverId}/reject")
    public AjaxResult reject(@PathVariable Long handoverId, @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        handoverService.reject(handoverId, reason);
        return success();
    }

    @RequiresPermissions("erp:handover:remove")
    @Log(title = "工序交接单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(handoverService.deleteByIds(ids));
    }
}
