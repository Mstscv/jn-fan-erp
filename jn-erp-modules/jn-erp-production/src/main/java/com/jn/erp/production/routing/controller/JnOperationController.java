package com.jn.erp.production.routing.controller;

import com.jn.erp.production.routing.domain.JnOperation;
import com.jn.erp.production.routing.service.IJnOperationService;
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
@RequestMapping("/erp/operation")
public class JnOperationController extends BaseController {

    @Autowired
    private IJnOperationService operationService;

    @RequiresPermissions("erp:operation:list")
    @GetMapping("/list")
    public TableDataInfo list(JnOperation operation) {
        startPage();
        List<JnOperation> list = operationService.selectList(operation);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:operation:query")
    @GetMapping("/{operationId}")
    public AjaxResult getInfo(@PathVariable Long operationId) {
        return success(operationService.selectById(operationId));
    }

    @RequiresPermissions("erp:operation:add")
    @Log(title = "工序管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnOperation operation) {
        return toAjax(operationService.insert(operation));
    }

    @RequiresPermissions("erp:operation:edit")
    @Log(title = "工序管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnOperation operation) {
        return toAjax(operationService.update(operation));
    }

    @RequiresPermissions("erp:operation:remove")
    @Log(title = "工序管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{operationIds}")
    public AjaxResult remove(@PathVariable Long[] operationIds) {
        return toAjax(operationService.deleteByIds(operationIds));
    }
}
