package com.jn.erp.production.schedule.service;

import com.jn.erp.production.schedule.domain.JnSchedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IJnScheduleService {

    List<JnSchedule> selectList(JnSchedule query);

    JnSchedule selectById(Long id);

    List<JnSchedule> selectByOrderId(Long orderId);

    int scheduleOrder(Long orderId, LocalDate startDate);

    int manualSchedule(Long scheduleId, LocalDateTime newStart, LocalDateTime newEnd);

    int completeOperation(Long scheduleId);

    int deleteByIds(Long[] ids);

    List<Map<String, Object>> getGanttData(LocalDate startDate, LocalDate endDate);

    Map<String, Object> detectConflicts(Long workCenterId, LocalDate date);
}
