package com.jn.erp.production.report;

import com.jn.erp.production.report.domain.JnWorkReport;
import com.jn.erp.production.report.mapper.JnWorkReportMapper;
import com.jn.erp.production.report.service.IJnWorkReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JnWorkReportServiceTest {

    @InjectMocks
    private IJnWorkReportService reportService;

    @Mock
    private JnWorkReportMapper reportMapper;

    @Test
    void testInsertReport() {
        JnWorkReport report = new JnWorkReport();
        report.setOrderId(1L);
        report.setOrderNo("WO-001");
        report.setWorkerId(1L);
        report.setWorkerName("张三");
        report.setStartTime(LocalDateTime.of(2026, 5, 27, 8, 0));
        report.setEndTime(LocalDateTime.of(2026, 5, 27, 10, 30));
        report.setOutputQty(50);
        report.setGoodQty(48);
        report.setDefectQty(2);

        when(reportMapper.insert(any(JnWorkReport.class))).thenReturn(1);

        int result = reportService.insert(report);
        assertEquals(1, result);
    }

    @Test
    void testWorkerStats() {
        JnWorkReport r1 = new JnWorkReport();
        r1.setWorkerId(1L);
        r1.setOutputQty(50);
        r1.setDurationMinutes(BigDecimal.valueOf(150));

        when(reportMapper.selectList(any())).thenReturn(List.of(r1));

        Map<String, Object> stats = reportService.getWorkerStats(1L, LocalDate.now().minusDays(7), LocalDate.now());
        assertNotNull(stats);
    }
}
