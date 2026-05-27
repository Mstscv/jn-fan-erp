package com.jn.erp.production.routing.controller;

import com.jn.erp.production.routing.domain.JnWorkCenter;
import com.jn.erp.production.routing.service.IJnWorkCenterService;
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

@RestController
@RequestMapping("/erp/work-center")
public class JnWorkCenterController extends BaseController {

    @Autowired
    private IJnWorkCenterService workCenterService;

    @RequiresPermissions("erp:workcenter:list")
    @GetMapping("/list")
    public TableDataInfo list(JnWorkCenter workCenter) {
        startPage();
        List<JnWorkCenter> list = workCenterService.selectList(workCenter);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:workcenter:query")
    @GetMapping("/{workCenterId}")
    public AjaxResult getInfo(@PathVariable Long workCenterId) {
        return success(workCenterService.selectById(workCenterId));
    }

    @RequiresPermissions("erp:workcenter:add")
    @Log(title = "工作中心管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnWorkCenter workCenter) {
        return toAjax(workCenterService.insert(workCenter));
    }

    @RequiresPermissions("erp:workcenter:edit")
    @Log(title = "工作中心管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnWorkCenter workCenter) {
        return toAjax(workCenterService.update(workCenter));
    }

    @RequiresPermissions("erp:workcenter:remove")
    @Log(title = "工作中心管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{workCenterIds}")
    public AjaxResult remove(@PathVariable Long[] workCenterIds) {
        return toAjax(workCenterService.deleteByIds(workCenterIds));
    }
}
