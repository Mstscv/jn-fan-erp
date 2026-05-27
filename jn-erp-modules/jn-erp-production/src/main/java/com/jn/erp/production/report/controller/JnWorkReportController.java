package com.jn.erp.production.report.controller;

import com.jn.erp.production.report.domain.JnWorkReport;
import com.jn.erp.production.report.service.IJnWorkReportService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/erp/work-report")
public class JnWorkReportController extends BaseController {

    @Autowired
    private IJnWorkReportService workReportService;

    @RequiresPermissions("erp:workreport:list")
    @GetMapping("/list")
    public TableDataInfo list(JnWorkReport report) {
        startPage();
        List<JnWorkReport> list = workReportService.selectList(report);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:workreport:query")
    @GetMapping("/{reportId}")
    public AjaxResult getInfo(@PathVariable Long reportId) {
        return success(workReportService.selectById(reportId));
    }

    @RequiresPermissions("erp:workreport:query")
    @GetMapping("/order/{orderId}")
    public AjaxResult getByOrder(@PathVariable Long orderId) {
        return success(workReportService.selectByOrderId(orderId));
    }

    @RequiresPermissions("erp:workreport:add")
    @Log(title = "工序报工单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnWorkReport report) {
        return toAjax(workReportService.insert(report));
    }

    @RequiresPermissions("erp:workreport:edit")
    @Log(title = "工序报工单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnWorkReport report) {
        return toAjax(workReportService.update(report));
    }

    @RequiresPermissions("erp:workreport:edit")
    @Log(title = "工序报工单", businessType = BusinessType.UPDATE)
    @PutMapping("/{reportId}/submit")
    public AjaxResult submit(@PathVariable Long reportId) {
        workReportService.submit(reportId);
        return success();
    }

    @RequiresPermissions("erp:workreport:edit")
    @Log(title = "工序报工单", businessType = BusinessType.UPDATE)
    @PutMapping("/{reportId}/approve")
    public AjaxResult approve(@PathVariable Long reportId) {
        workReportService.approve(reportId);
        return success();
    }

    @RequiresPermissions("erp:workreport:edit")
    @Log(title = "工序报工单", businessType = BusinessType.UPDATE)
    @PutMapping("/{reportId}/reject")
    public AjaxResult reject(@PathVariable Long reportId, @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        workReportService.reject(reportId, reason);
        return success();
    }

    @RequiresPermissions("erp:workreport:list")
    @GetMapping("/stats/worker")
    public AjaxResult workerStats(@RequestParam Long workerId, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return success(workReportService.getWorkerStats(workerId, startDate, endDate));
    }

    @RequiresPermissions("erp:workreport:list")
    @GetMapping("/stats/team")
    public AjaxResult teamStats(@RequestParam Long teamId, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return success(workReportService.getTeamStats(teamId, startDate, endDate));
    }

    @RequiresPermissions("erp:workreport:remove")
    @Log(title = "工序报工单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(workReportService.deleteByIds(ids));
    }
}
