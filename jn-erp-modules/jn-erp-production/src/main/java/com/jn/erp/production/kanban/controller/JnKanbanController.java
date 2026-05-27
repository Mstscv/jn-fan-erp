package com.jn.erp.production.kanban.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jn.erp.production.handover.domain.JnHandover;
import com.jn.erp.production.handover.mapper.JnHandoverMapper;
import com.jn.erp.production.schedule.domain.JnSchedule;
import com.jn.erp.production.schedule.mapper.JnScheduleMapper;
import com.jn.erp.production.workorder.domain.JnWorkOrder;
import com.jn.erp.production.workorder.mapper.JnWorkOrderMapper;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/erp/kanban")
public class JnKanbanController extends BaseController {

    @Autowired
    private JnWorkOrderMapper workOrderMapper;

    @Autowired
    private JnHandoverMapper handoverMapper;

    @Autowired
    private JnScheduleMapper scheduleMapper;

    @GetMapping("/dashboard")
    public AjaxResult dashboard() {
        Map<String, Object> result = new HashMap<>();

        long totalOrders = workOrderMapper.selectCount(new LambdaQueryWrapper<>());

        long pendingCount = workOrderMapper.selectCount(
                new LambdaQueryWrapper<JnWorkOrder>().eq(JnWorkOrder::getStatus, "PENDING"));

        long scheduledCount = workOrderMapper.selectCount(
                new LambdaQueryWrapper<JnWorkOrder>().eq(JnWorkOrder::getStatus, "SCHEDULED"));

        long inProgressCount = workOrderMapper.selectCount(
                new LambdaQueryWrapper<JnWorkOrder>().eq(JnWorkOrder::getStatus, "IN_PROGRESS"));

        long completedCount = workOrderMapper.selectCount(
                new LambdaQueryWrapper<JnWorkOrder>().eq(JnWorkOrder::getStatus, "COMPLETED"));

        long todayCompleted = workOrderMapper.selectCount(
                new LambdaQueryWrapper<JnWorkOrder>()
                        .eq(JnWorkOrder::getStatus, "COMPLETED")
                        .apply("DATE(create_time) = CURDATE()"));

        long overdueCount = workOrderMapper.selectCount(
                new LambdaQueryWrapper<JnWorkOrder>()
                        .lt(JnWorkOrder::getPlannedEnd, LocalDate.now())
                        .notIn(JnWorkOrder::getStatus, "COMPLETED", "CLOSED", "CANCELLED"));

        double onTimeRate = 0;
        if (completedCount > 0) {
            long onTimeCompleted = workOrderMapper.selectCount(
                new LambdaQueryWrapper<JnWorkOrder>()
                        .eq(JnWorkOrder::getStatus, "COMPLETED")
                        .apply("actual_end <= planned_end"));
            onTimeRate = BigDecimal.valueOf(onTimeCompleted)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(completedCount), 2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        result.put("totalOrders", totalOrders);
        result.put("pendingCount", pendingCount);
        result.put("scheduledCount", scheduledCount);
        result.put("inProgressCount", inProgressCount);
        result.put("completedCount", completedCount);
        result.put("todayCompleted", todayCompleted);
        result.put("overdueCount", overdueCount);
        result.put("onTimeRate", onTimeRate);

        return success(result);
    }

    @GetMapping("/order-status")
    public AjaxResult orderStatus() {
        Map<String, Object> result = new HashMap<>();

        List<Map<String, Object>> pending = buildOrderStatusList("PENDING");
        List<Map<String, Object>> scheduled = buildOrderStatusList("SCHEDULED");
        List<Map<String, Object>> inProgress = buildOrderStatusList("IN_PROGRESS");
        List<Map<String, Object>> completed = buildOrderStatusList("COMPLETED");

        result.put("pending", pending);
        result.put("scheduled", scheduled);
        result.put("inProgress", inProgress);
        result.put("completed", completed);

        return success(result);
    }

    @GetMapping("/recent-activity")
    public AjaxResult recentActivity() {
        List<Map<String, Object>> allActivities = new ArrayList<>();

        List<JnHandover> recentHandovers = handoverMapper.selectList(
                new LambdaQueryWrapper<JnHandover>()
                        .orderByDesc(JnHandover::getCreateTime)
                        .last("LIMIT 20"));

        for (JnHandover h : recentHandovers) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", "HANDOVER");
            item.put("description", "工序交接: " + h.getHandoverNo() + " - " + h.getFromOperationName() + " -> " + h.getToOperationName());
            item.put("time", h.getCreateTime());
            allActivities.add(item);
        }

        List<JnSchedule> recentSchedules = scheduleMapper.selectList(
                new LambdaQueryWrapper<JnSchedule>()
                        .eq(JnSchedule::getStatus, "COMPLETED")
                        .orderByDesc(JnSchedule::getUpdateTime)
                        .last("LIMIT 20"));

        for (JnSchedule s : recentSchedules) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", "SCHEDULE_COMPLETE");
            item.put("description", "工序完成: " + s.getOrderNo() + " - " + s.getOperationName());
            item.put("time", s.getUpdateTime() != null ? s.getUpdateTime() : s.getCreateTime());
            allActivities.add(item);
        }

        allActivities.sort((a, b) -> {
            LocalDateTime ta = (LocalDateTime) a.get("time");
            LocalDateTime tb = (LocalDateTime) b.get("time");
            if (ta == null && tb == null) return 0;
            if (ta == null) return 1;
            if (tb == null) return -1;
            return tb.compareTo(ta);
        });

        if (allActivities.size() > 20) {
            allActivities = allActivities.subList(0, 20);
        }

        return success(allActivities);
    }

    private List<Map<String, Object>> buildOrderStatusList(String status) {
        List<JnWorkOrder> orders = workOrderMapper.selectList(
                new LambdaQueryWrapper<JnWorkOrder>()
                        .eq(JnWorkOrder::getStatus, status)
                        .orderByDesc(JnWorkOrder::getCreateTime));
        return orders.stream().map(o -> {
            Map<String, Object> item = new HashMap<>();
            item.put("orderId", o.getOrderId());
            item.put("orderNo", o.getOrderNo());
            item.put("productName", o.getProductName());
            item.put("quantity", o.getQuantity());
            item.put("plannedStart", o.getPlannedStart());
            item.put("plannedEnd", o.getPlannedEnd());
            item.put("customerName", o.getCustomerName());
            item.put("priority", o.getPriority());
            return item;
        }).collect(Collectors.toList());
    }
}
