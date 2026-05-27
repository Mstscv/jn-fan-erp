package com.jn.erp.production.schedule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.routing.mapper.JnRoutingLineMapper;
import com.jn.erp.production.schedule.domain.JnSchedule;
import com.jn.erp.production.schedule.mapper.JnScheduleMapper;
import com.jn.erp.production.schedule.service.IJnScheduleService;
import com.jn.erp.production.workorder.domain.JnWorkOrder;
import com.jn.erp.production.workorder.mapper.JnWorkOrderMapper;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JnScheduleServiceImpl extends ServiceImpl<JnScheduleMapper, JnSchedule> implements IJnScheduleService {

    @Autowired
    private JnScheduleMapper scheduleMapper;

    @Autowired
    private JnWorkOrderMapper workOrderMapper;

    @Autowired
    private JnRoutingLineMapper routingLineMapper;

    @Override
    public List<JnSchedule> selectList(JnSchedule query) {
        LambdaQueryWrapper<JnSchedule> wrapper = new LambdaQueryWrapper<>();
        if (query.getOrderId() != null) {
            wrapper.eq(JnSchedule::getOrderId, query.getOrderId());
        }
        if (query.getOrderNo() != null && !query.getOrderNo().isEmpty()) {
            wrapper.like(JnSchedule::getOrderNo, query.getOrderNo());
        }
        if (query.getWorkCenterId() != null) {
            wrapper.eq(JnSchedule::getWorkCenterId, query.getWorkCenterId());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(JnSchedule::getStatus, query.getStatus());
        }
        if (query.getScheduledDate() != null) {
            wrapper.eq(JnSchedule::getScheduledDate, query.getScheduledDate());
        }
        wrapper.orderByAsc(JnSchedule::getSeqNo).orderByAsc(JnSchedule::getStartTime);
        return scheduleMapper.selectList(wrapper);
    }

    @Override
    public JnSchedule selectById(Long id) {
        return scheduleMapper.selectById(id);
    }

    @Override
    public List<JnSchedule> selectByOrderId(Long orderId) {
        return scheduleMapper.selectByOrderId(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int scheduleOrder(Long orderId, LocalDate startDate) {
        JnWorkOrder order = workOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("工单不存在: " + orderId);
        }

        List<JnRoutingLine> routingLines = routingLineMapper.selectByRoutingId(order.getRoutingId());
        if (routingLines == null || routingLines.isEmpty()) {
            throw new RuntimeException("未找到工艺路线: " + order.getRoutingId());
        }

        routingLines.sort((a, b) -> {
            int seqA = a.getSeqNo() != null ? a.getSeqNo() : 0;
            int seqB = b.getSeqNo() != null ? b.getSeqNo() : 0;
            return Integer.compare(seqA, seqB);
        });

        LocalDateTime currentStart = startDate.atTime(LocalTime.of(8, 0));

        for (JnRoutingLine line : routingLines) {
            JnSchedule schedule = new JnSchedule();
            schedule.setOrderId(orderId);
            schedule.setOrderNo(order.getOrderNo());
            schedule.setOperationId(line.getOperationId());
            schedule.setOperationCode(line.getOperationCode());
            schedule.setOperationName(line.getOperationName());
            schedule.setRoutingLineId(line.getRoutingLineId());
            schedule.setWorkCenterId(line.getWorkCenterId());
            schedule.setWorkCenterCode(line.getWorkCenterCode());
            schedule.setWorkCenterName(line.getWorkCenterName());
            schedule.setScheduledDate(currentStart.toLocalDate());
            schedule.setSeqNo(line.getSeqNo());
            schedule.setStatus("PENDING");
            schedule.setPriority("NORMAL");

            BigDecimal totalMinutes = BigDecimal.ZERO;
            if (line.getStandardTime() != null) {
                totalMinutes = totalMinutes.add(line.getStandardTime());
            }
            if (line.getSetupTime() != null) {
                totalMinutes = totalMinutes.add(line.getSetupTime());
            }
            schedule.setPlannedDuration(totalMinutes);

            List<JnSchedule> conflicts = scheduleMapper.selectByWorkCenterAndDate(
                    line.getWorkCenterId(), currentStart.toLocalDate());

            if (conflicts != null && !conflicts.isEmpty()) {
                LocalDateTime lastEnd = currentStart;
                for (JnSchedule existing : conflicts) {
                    if (existing.getEndTime() != null && existing.getEndTime().isAfter(lastEnd)) {
                        lastEnd = existing.getEndTime();
                    }
                }
                if (lastEnd.isAfter(currentStart)) {
                    currentStart = lastEnd;
                }
            }

            schedule.setStartTime(currentStart);

            LocalDateTime endTime = currentStart.plusMinutes(totalMinutes.longValue());
            schedule.setEndTime(endTime);

            schedule.setCreateBy(SecurityUtils.getUsername());
            scheduleMapper.insert(schedule);

            currentStart = endTime;
        }

        JnWorkOrder updateOrder = new JnWorkOrder();
        updateOrder.setOrderId(orderId);
        updateOrder.setStatus("SCHEDULED");
        updateOrder.setUpdateBy(SecurityUtils.getUsername());
        workOrderMapper.updateById(updateOrder);

        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int manualSchedule(Long scheduleId, LocalDateTime newStart, LocalDateTime newEnd) {
        JnSchedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("排程记录不存在: " + scheduleId);
        }

        List<JnSchedule> conflicts = scheduleMapper.selectByWorkCenterAndDate(
                schedule.getWorkCenterId(), newStart.toLocalDate());

        boolean hasConflict = false;
        if (conflicts != null) {
            for (JnSchedule existing : conflicts) {
                if (existing.getScheduleId().equals(scheduleId)) {
                    continue;
                }
                if (newStart.isBefore(existing.getEndTime()) && newEnd.isAfter(existing.getStartTime())) {
                    hasConflict = true;
                    break;
                }
            }
        }

        if (hasConflict) {
            throw new RuntimeException("时间冲突，请重新选择时间");
        }

        schedule.setStartTime(newStart);
        schedule.setEndTime(newEnd);
        schedule.setScheduledDate(newStart.toLocalDate());

        if (schedule.getPlannedDuration() != null) {
            long newDurationMs = java.time.Duration.between(newStart, newEnd).toMinutes();
            schedule.setPlannedDuration(BigDecimal.valueOf(newDurationMs));
        }

        schedule.setUpdateBy(SecurityUtils.getUsername());
        return scheduleMapper.updateById(schedule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int completeOperation(Long scheduleId) {
        JnSchedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("排程记录不存在: " + scheduleId);
        }

        schedule.setStatus("COMPLETED");
        if (schedule.getStartTime() != null) {
            long actualMinutes = java.time.Duration.between(schedule.getStartTime(), LocalDateTime.now()).toMinutes();
            schedule.setActualDuration(BigDecimal.valueOf(Math.max(actualMinutes, 1)));
        }
        schedule.setUpdateBy(SecurityUtils.getUsername());
        return scheduleMapper.updateById(schedule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] ids) {
        int count = 0;
        for (Long id : ids) {
            count += scheduleMapper.deleteById(id);
        }
        return count;
    }

    @Override
    public List<Map<String, Object>> getGanttData(LocalDate startDate, LocalDate endDate) {
        List<JnSchedule> schedules = scheduleMapper.selectByDateRange(startDate, endDate);
        List<Map<String, Object>> result = new ArrayList<>();
        for (JnSchedule s : schedules) {
            Map<String, Object> item = new HashMap<>();
            item.put("scheduleId", s.getScheduleId());
            item.put("orderNo", s.getOrderNo());
            item.put("operationName", s.getOperationName());
            item.put("workCenterName", s.getWorkCenterName());
            item.put("startTime", s.getStartTime());
            item.put("endTime", s.getEndTime());
            item.put("status", s.getStatus());
            item.put("seqNo", s.getSeqNo());
            result.add(item);
        }
        return result;
    }

    @Override
    public Map<String, Object> detectConflicts(Long workCenterId, LocalDate date) {
        Map<String, Object> result = new HashMap<>();
        List<JnSchedule> schedules = scheduleMapper.selectByWorkCenterAndDate(workCenterId, date);

        List<Map<String, Object>> conflictingList = new ArrayList<>();
        for (int i = 0; i < schedules.size(); i++) {
            for (int j = i + 1; j < schedules.size(); j++) {
                JnSchedule a = schedules.get(i);
                JnSchedule b = schedules.get(j);
                if (a.getStartTime() != null && a.getEndTime() != null
                        && b.getStartTime() != null && b.getEndTime() != null) {
                    if (a.getStartTime().isBefore(b.getEndTime()) && b.getStartTime().isBefore(a.getEndTime())) {
                        Map<String, Object> conflict = new HashMap<>();
                        conflict.put("scheduleIdA", a.getScheduleId());
                        conflict.put("scheduleIdB", b.getScheduleId());
                        conflict.put("orderNoA", a.getOrderNo());
                        conflict.put("orderNoB", b.getOrderNo());
                        conflictingList.add(conflict);
                    }
                }
            }
        }

        result.put("hasConflict", !conflictingList.isEmpty());
        result.put("conflicts", conflictingList);
        result.put("totalSchedules", schedules.size());
        return result;
    }
}
