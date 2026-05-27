package com.jn.erp.production.schedule;

import com.jn.erp.production.routing.domain.JnRoutingLine;
import com.jn.erp.production.routing.mapper.JnRoutingLineMapper;
import com.jn.erp.production.schedule.domain.JnSchedule;
import com.jn.erp.production.schedule.mapper.JnScheduleMapper;
import com.jn.erp.production.schedule.service.IJnScheduleService;
import com.jn.erp.production.workorder.domain.JnWorkOrder;
import com.jn.erp.production.workorder.mapper.JnWorkOrderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JnScheduleServiceTest {

    @InjectMocks
    private IJnScheduleService scheduleService;

    @Mock
    private JnScheduleMapper scheduleMapper;
    @Mock
    private JnWorkOrderMapper workOrderMapper;
    @Mock
    private JnRoutingLineMapper routingLineMapper;

    @Test
    void testScheduleOrder() {
        JnWorkOrder order = new JnWorkOrder();
        order.setOrderId(1L);
        order.setOrderNo("WO-20260527-0001");
        order.setRoutingId(1L);

        JnRoutingLine line = new JnRoutingLine();
        line.setRoutingLineId(1L);
        line.setSeqNo(10);
        line.setOperationId(1L);
        line.setOperationCode("CUT-001");
        line.setOperationName("下料");
        line.setWorkCenterId(1L);
        line.setStandardTime(BigDecimal.valueOf(30));
        line.setSetupTime(BigDecimal.valueOf(10));
        line.setWorkCenterCode("WC-01");
        line.setWorkCenterName("下料车间");

        when(workOrderMapper.selectById(1L)).thenReturn(order);
        when(routingLineMapper.selectByRoutingId(1L)).thenReturn(List.of(line));
        when(scheduleMapper.selectByWorkCenterAndDate(any(), any())).thenReturn(new ArrayList<>());
        when(workOrderMapper.updateById(any())).thenReturn(1);

        int result = scheduleService.scheduleOrder(1L, LocalDate.now());
        assertEquals(1, result);
    }

    @Test
    void testManualSchedule() {
        JnSchedule schedule = new JnSchedule();
        schedule.setScheduleId(1L);
        schedule.setWorkCenterId(1L);
        schedule.setStartTime(LocalDateTime.now());
        schedule.setEndTime(LocalDateTime.now().plusHours(2));

        when(scheduleMapper.selectById(1L)).thenReturn(schedule);
        when(scheduleMapper.selectByWorkCenterAndDate(any(), any())).thenReturn(new ArrayList<>());
        when(scheduleMapper.updateById(any())).thenReturn(1);

        LocalDateTime newStart = LocalDateTime.now().plusDays(1).withHour(8).withMinute(0);
        LocalDateTime newEnd = newStart.plusHours(3);
        int result = scheduleService.manualSchedule(1L, newStart, newEnd);
        assertEquals(1, result);
    }

    @Test
    void testDetectConflicts() {
        JnSchedule a = new JnSchedule();
        a.setScheduleId(1L);
        a.setStartTime(LocalDateTime.of(2026, 5, 27, 8, 0));
        a.setEndTime(LocalDateTime.of(2026, 5, 27, 10, 0));

        JnSchedule b = new JnSchedule();
        b.setScheduleId(2L);
        b.setStartTime(LocalDateTime.of(2026, 5, 27, 9, 0));
        b.setEndTime(LocalDateTime.of(2026, 5, 27, 11, 0));

        when(scheduleMapper.selectByWorkCenterAndDate(1L, LocalDate.of(2026, 5, 27))).thenReturn(List.of(a, b));

        Map<String, Object> conflicts = scheduleService.detectConflicts(1L, LocalDate.of(2026, 5, 27));
        assertTrue((Boolean) conflicts.get("hasConflict"));
    }

    @Test
    void testGetGanttData() {
        JnSchedule s = new JnSchedule();
        s.setScheduleId(1L);
        s.setOrderNo("WO-001");
        s.setOperationName("焊接");
        s.setWorkCenterName("焊接车间");

        when(scheduleMapper.selectByDateRange(any(), any())).thenReturn(List.of(s));

        List<Map<String, Object>> gantt = scheduleService.getGanttData(LocalDate.now(), LocalDate.now().plusDays(7));
        assertEquals(1, gantt.size());
    }
}
