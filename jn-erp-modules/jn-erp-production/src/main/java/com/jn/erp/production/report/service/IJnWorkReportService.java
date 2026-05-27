package com.jn.erp.production.report.service;

import com.jn.erp.production.report.domain.JnWorkReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IJnWorkReportService {

    List<JnWorkReport> selectList(JnWorkReport query);

    JnWorkReport selectById(Long id);

    List<JnWorkReport> selectByOrderId(Long orderId);

    int insert(JnWorkReport report);

    int update(JnWorkReport report);

    int deleteByIds(Long[] ids);

    void submit(Long reportId);

    void approve(Long reportId);

    void reject(Long reportId, String reason);

    Map<String, Object> getWorkerStats(Long workerId, LocalDate startDate, LocalDate endDate);

    Map<String, Object> getTeamStats(Long teamId, LocalDate startDate, LocalDate endDate);
}
