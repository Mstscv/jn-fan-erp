package com.jn.erp.production.team.controller;

import com.jn.erp.production.team.domain.JnWorker;
import com.jn.erp.production.team.service.IJnWorkerService;
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
@RequestMapping("/erp/worker")
public class JnWorkerController extends BaseController {

    @Autowired
    private IJnWorkerService workerService;

    @RequiresPermissions("erp:worker:list")
    @GetMapping("/list")
    public TableDataInfo list(JnWorker worker) {
        startPage();
        List<JnWorker> list = workerService.selectList(worker);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:worker:query")
    @GetMapping("/{workerId}")
    public AjaxResult getInfo(@PathVariable Long workerId) {
        return success(workerService.selectById(workerId));
    }

    @RequiresPermissions("erp:worker:query")
    @GetMapping("/team/{teamId}")
    public AjaxResult getByTeamId(@PathVariable Long teamId) {
        return success(workerService.selectByTeamId(teamId));
    }

    @RequiresPermissions("erp:worker:add")
    @Log(title = "工人管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnWorker worker) {
        return toAjax(workerService.insert(worker));
    }

    @RequiresPermissions("erp:worker:edit")
    @Log(title = "工人管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnWorker worker) {
        return toAjax(workerService.update(worker));
    }

    @RequiresPermissions("erp:worker:remove")
    @Log(title = "工人管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{workerIds}")
    public AjaxResult remove(@PathVariable Long[] workerIds) {
        return toAjax(workerService.deleteByIds(workerIds));
    }
}
