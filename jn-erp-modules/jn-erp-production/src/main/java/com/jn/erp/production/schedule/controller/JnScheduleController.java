package com.jn.erp.production.schedule.controller;

import com.jn.erp.production.schedule.domain.JnSchedule;
import com.jn.erp.production.schedule.service.IJnScheduleService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/erp/schedule")
public class JnScheduleController extends BaseController {

    @Autowired
    private IJnScheduleService scheduleService;

    @RequiresPermissions("erp:schedule:list")
    @GetMapping("/list")
    public TableDataInfo list(JnSchedule schedule) {
        startPage();
        List<JnSchedule> list = scheduleService.selectList(schedule);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:schedule:query")
    @GetMapping("/{scheduleId}")
    public AjaxResult getInfo(@PathVariable Long scheduleId) {
        return success(scheduleService.selectById(scheduleId));
    }

    @RequiresPermissions("erp:schedule:query")
    @GetMapping("/order/{orderId}")
    public AjaxResult getByOrder(@PathVariable Long orderId) {
        return success(scheduleService.selectByOrderId(orderId));
    }

    @RequiresPermissions("erp:schedule:add")
    @Log(title = "有限产能排程", businessType = BusinessType.INSERT)
    @PostMapping("/auto-schedule")
    public AjaxResult autoSchedule(@RequestBody Map<String, Object> body) {
        Long orderId = Long.valueOf(body.get("orderId").toString());
        LocalDate startDate = LocalDate.parse((String) body.get("startDate"));
        return toAjax(scheduleService.scheduleOrder(orderId, startDate));
    }

    @RequiresPermissions("erp:schedule:edit")
    @Log(title = "有限产能排程", businessType = BusinessType.UPDATE)
    @PutMapping("/manual")
    public AjaxResult manual(@RequestBody Map<String, Object> body) {
        Long scheduleId = Long.valueOf(body.get("scheduleId").toString());
        LocalDateTime newStart = LocalDateTime.parse((String) body.get("newStart"));
        LocalDateTime newEnd = LocalDateTime.parse((String) body.get("newEnd"));
        return toAjax(scheduleService.manualSchedule(scheduleId, newStart, newEnd));
    }

    @RequiresPermissions("erp:schedule:edit")
    @Log(title = "有限产能排程", businessType = BusinessType.UPDATE)
    @PutMapping("/{scheduleId}/complete")
    public AjaxResult complete(@PathVariable Long scheduleId) {
        return toAjax(scheduleService.completeOperation(scheduleId));
    }

    @RequiresPermissions("erp:schedule:list")
    @GetMapping("/gantt")
    public AjaxResult gantt(@RequestParam(required = false) LocalDate startDate,
                            @RequestParam(required = false) LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDate.now().plusDays(30);
        }
        return success(scheduleService.getGanttData(startDate, endDate));
    }

    @RequiresPermissions("erp:schedule:list")
    @GetMapping("/conflicts")
    public AjaxResult conflicts(@RequestParam Long workCenterId, @RequestParam LocalDate date) {
        return success(scheduleService.detectConflicts(workCenterId, date));
    }

    @RequiresPermissions("erp:schedule:remove")
    @Log(title = "有限产能排程", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(scheduleService.deleteByIds(ids));
    }
}
