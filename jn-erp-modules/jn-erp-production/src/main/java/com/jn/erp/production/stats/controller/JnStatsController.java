package com.jn.erp.production.stats.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jn.erp.production.equipment.domain.JnEquipment;
import com.jn.erp.production.equipment.mapper.JnEquipmentMapper;
import com.jn.erp.production.report.domain.JnWorkReport;
import com.jn.erp.production.report.mapper.JnWorkReportMapper;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/erp/stats")
public class JnStatsController extends BaseController {

    @Autowired
    private JnWorkReportMapper workReportMapper;

    @Autowired
    private JnEquipmentMapper equipmentMapper;

    @GetMapping("/labor-hours")
    public AjaxResult laborHours(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) Long workerId) {
        LambdaQueryWrapper<JnWorkReport> wrapper = new LambdaQueryWrapper<JnWorkReport>()
                .between(JnWorkReport::getStartTime, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
        if (teamId != null) {
            wrapper.eq(JnWorkReport::getTeamId, teamId);
        }
        if (workerId != null) {
            wrapper.eq(JnWorkReport::getWorkerId, workerId);
        }
        List<JnWorkReport> reports = workReportMapper.selectList(wrapper);
        Map<Long, List<JnWorkReport>> grouped = reports.stream()
                .collect(Collectors.groupingBy(JnWorkReport::getWorkerId));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, List<JnWorkReport>> entry : grouped.entrySet()) {
            List<JnWorkReport> workerReports = entry.getValue();
            JnWorkReport first = workerReports.get(0);
            Map<String, Object> item = new HashMap<>();
            item.put("workerId", entry.getKey());
            item.put("workerName", first.getWorkerName());
            item.put("teamName", first.getTeamName());
            double totalHours = workerReports.stream()
                    .mapToDouble(r -> r.getDurationMinutes() != null ? r.getDurationMinutes() : 0)
                    .sum() / 60.0;
            item.put("totalHours", BigDecimal.valueOf(totalHours).setScale(2, RoundingMode.HALF_UP).doubleValue());
            int totalOutput = workerReports.stream()
                    .mapToInt(r -> r.getOutputQty() != null ? r.getOutputQty() : 0)
                    .sum();
            item.put("totalOutput", totalOutput);
            item.put("reportCount", workerReports.size());
            double avgHoursPerReport = workerReports.size() > 0 ? totalHours / workerReports.size() : 0;
            item.put("avgHoursPerReport", BigDecimal.valueOf(avgHoursPerReport).setScale(2, RoundingMode.HALF_UP).doubleValue());
            result.add(item);
        }
        result.sort((a, b) -> Double.compare((Double) b.get("totalHours"), (Double) a.get("totalHours")));
        return success(result);
    }

    @GetMapping("/daily-production")
    public AjaxResult dailyProduction(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) Long teamId) {
        if (date == null) {
            date = LocalDate.now();
        }
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        LambdaQueryWrapper<JnWorkReport> wrapper = new LambdaQueryWrapper<JnWorkReport>()
                .between(JnWorkReport::getStartTime, start, end);
        if (teamId != null) {
            wrapper.eq(JnWorkReport::getTeamId, teamId);
        }
        List<JnWorkReport> reports = workReportMapper.selectList(wrapper);
        Map<Long, List<JnWorkReport>> grouped = reports.stream()
                .filter(r -> r.getOperationId() != null)
                .collect(Collectors.groupingBy(JnWorkReport::getOperationId));
        List<Map<String, Object>> operations = new ArrayList<>();
        for (Map.Entry<Long, List<JnWorkReport>> entry : grouped.entrySet()) {
            List<JnWorkReport> opReports = entry.getValue();
            JnWorkReport first = opReports.get(0);
            int totalOutput = opReports.stream().mapToInt(r -> r.getOutputQty() != null ? r.getOutputQty() : 0).sum();
            int goodOutput = opReports.stream().mapToInt(r -> r.getGoodQty() != null ? r.getGoodQty() : 0).sum();
            int defectOutput = opReports.stream().mapToInt(r -> r.getDefectQty() != null ? r.getDefectQty() : 0).sum();
            Map<String, Object> item = new HashMap<>();
            item.put("operationId", entry.getKey());
            item.put("operationName", first.getOperationName());
            item.put("totalOutput", totalOutput);
            item.put("goodOutput", goodOutput);
            item.put("defectOutput", defectOutput);
            item.put("reportCount", opReports.size());
            item.put("defectRate", totalOutput > 0
                    ? BigDecimal.valueOf(defectOutput * 100.0 / totalOutput).setScale(2, RoundingMode.HALF_UP).doubleValue()
                    : 0);
            operations.add(item);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("operations", operations);
        int totalOutput = reports.stream().mapToInt(r -> r.getOutputQty() != null ? r.getOutputQty() : 0).sum();
        int totalGood = reports.stream().mapToInt(r -> r.getGoodQty() != null ? r.getGoodQty() : 0).sum();
        int totalDefect = reports.stream().mapToInt(r -> r.getDefectQty() != null ? r.getDefectQty() : 0).sum();
        Map<String, Object> overall = new HashMap<>();
        overall.put("totalOutput", totalOutput);
        overall.put("totalGood", totalGood);
        overall.put("totalDefect", totalDefect);
        overall.put("overallDefectRate", totalOutput > 0
                ? BigDecimal.valueOf(totalDefect * 100.0 / totalOutput).setScale(2, RoundingMode.HALF_UP).doubleValue()
                : 0);
        result.put("overall", overall);
        return success(result);
    }

    @GetMapping("/utilization")
    public AjaxResult utilization(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) Long workCenterId) {
        LambdaQueryWrapper<JnEquipment> wrapper = new LambdaQueryWrapper<JnEquipment>()
                .eq(JnEquipment::getIsActive, "Y");
        if (workCenterId != null) {
            wrapper.eq(JnEquipment::getWorkCenterId, workCenterId);
        }
        List<JnEquipment> equipmentList = equipmentMapper.selectList(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        for (JnEquipment eq : equipmentList) {
            Map<String, Object> item = new HashMap<>();
            item.put("equipmentId", eq.getEquipmentId());
            item.put("equipmentName", eq.getEquipmentName());
            item.put("workCenterName", eq.getWorkCenterName());
            double runningHours = eq.getRunningHours() != null ? eq.getRunningHours().doubleValue() : 0;
            double idleHours = eq.getIdleHours() != null ? eq.getIdleHours().doubleValue() : 0;
            double downtimeHours = eq.getDowntimeHours() != null ? eq.getDowntimeHours().doubleValue() : 0;
            if ("IDLE".equals(eq.getStatus())) {
                runningHours = 0;
            }
            double totalHours = runningHours + idleHours + downtimeHours;
            double utilizationRate = totalHours > 0
                    ? BigDecimal.valueOf(runningHours / totalHours * 100).setScale(2, RoundingMode.HALF_UP).doubleValue()
                    : 0;
            item.put("runningHours", runningHours);
            item.put("idleHours", idleHours);
            item.put("downtimeHours", downtimeHours);
            item.put("utilizationRate", utilizationRate);
            item.put("status", eq.getStatus());
            result.add(item);
        }
        return success(result);
    }
}
