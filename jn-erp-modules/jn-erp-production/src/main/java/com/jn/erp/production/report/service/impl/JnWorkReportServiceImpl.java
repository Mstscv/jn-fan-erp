package com.jn.erp.production.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.report.domain.JnWorkReport;
import com.jn.erp.production.report.mapper.JnWorkReportMapper;
import com.jn.erp.production.report.service.IJnWorkReportService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JnWorkReportServiceImpl extends ServiceImpl<JnWorkReportMapper, JnWorkReport> implements IJnWorkReportService {

    @Autowired
    private JnWorkReportMapper workReportMapper;

    @Override
    public List<JnWorkReport> selectList(JnWorkReport query) {
        LambdaQueryWrapper<JnWorkReport> wrapper = new LambdaQueryWrapper<>();
        if (query.getReportNo() != null && !query.getReportNo().isEmpty()) {
            wrapper.like(JnWorkReport::getReportNo, query.getReportNo());
        }
        if (query.getOrderNo() != null && !query.getOrderNo().isEmpty()) {
            wrapper.like(JnWorkReport::getOrderNo, query.getOrderNo());
        }
        if (query.getOrderId() != null) {
            wrapper.eq(JnWorkReport::getOrderId, query.getOrderId());
        }
        if (query.getOperationId() != null) {
            wrapper.eq(JnWorkReport::getOperationId, query.getOperationId());
        }
        if (query.getWorkerId() != null) {
            wrapper.eq(JnWorkReport::getWorkerId, query.getWorkerId());
        }
        if (query.getTeamId() != null) {
            wrapper.eq(JnWorkReport::getTeamId, query.getTeamId());
        }
        if (query.getWorkCenterId() != null) {
            wrapper.eq(JnWorkReport::getWorkCenterId, query.getWorkCenterId());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(JnWorkReport::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(JnWorkReport::getCreateTime);
        return workReportMapper.selectList(wrapper);
    }

    @Override
    public JnWorkReport selectById(Long id) {
        return workReportMapper.selectById(id);
    }

    @Override
    public List<JnWorkReport> selectByOrderId(Long orderId) {
        return workReportMapper.selectByOrderId(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnWorkReport report) {
        report.setReportNo(generateReportNo());
        if (report.getDefectQty() == null) {
            report.setDefectQty(0);
        }
        if (report.getReworkFlag() == null) {
            report.setReworkFlag("N");
        }
        if (report.getStatus() == null) {
            report.setStatus("DRAFT");
        }
        if (report.getStartTime() != null && report.getEndTime() != null) {
            long minutes = Duration.between(report.getStartTime(), report.getEndTime()).toMinutes();
            report.setDurationMinutes(BigDecimal.valueOf(Math.max(minutes, 0)));
        }
        report.setCreateBy(SecurityUtils.getUsername());
        return workReportMapper.insert(report);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnWorkReport report) {
        if (report.getStartTime() != null && report.getEndTime() != null) {
            long minutes = Duration.between(report.getStartTime(), report.getEndTime()).toMinutes();
            report.setDurationMinutes(BigDecimal.valueOf(Math.max(minutes, 0)));
        }
        report.setUpdateBy(SecurityUtils.getUsername());
        return workReportMapper.updateById(report);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] ids) {
        int count = 0;
        for (Long id : ids) {
            count += workReportMapper.deleteById(id);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(Long reportId) {
        JnWorkReport report = workReportMapper.selectById(reportId);
        if (report == null) {
            throw new RuntimeException("报工单不存在: " + reportId);
        }
        report.setStatus("SUBMITTED");
        report.setUpdateBy(SecurityUtils.getUsername());
        workReportMapper.updateById(report);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long reportId) {
        JnWorkReport report = workReportMapper.selectById(reportId);
        if (report == null) {
            throw new RuntimeException("报工单不存在: " + reportId);
        }
        report.setStatus("APPROVED");
        report.setUpdateBy(SecurityUtils.getUsername());
        workReportMapper.updateById(report);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long reportId, String reason) {
        JnWorkReport report = workReportMapper.selectById(reportId);
        if (report == null) {
            throw new RuntimeException("报工单不存在: " + reportId);
        }
        report.setStatus("REJECTED");
        String existingRemark = report.getRemark();
        if (existingRemark != null && !existingRemark.isEmpty()) {
            report.setRemark(existingRemark + "; " + reason);
        } else {
            report.setRemark(reason);
        }
        report.setUpdateBy(SecurityUtils.getUsername());
        workReportMapper.updateById(report);
    }

    @Override
    public Map<String, Object> getWorkerStats(Long workerId, LocalDate startDate, LocalDate endDate) {
        List<JnWorkReport> reports = workReportMapper.selectByWorkerId(workerId, startDate, endDate);
        Map<String, Object> stats = new HashMap<>();
        int totalOutput = 0;
        BigDecimal totalDuration = BigDecimal.ZERO;
        for (JnWorkReport r : reports) {
            totalOutput += (r.getOutputQty() != null ? r.getOutputQty() : 0);
            if (r.getDurationMinutes() != null) {
                totalDuration = totalDuration.add(r.getDurationMinutes());
            }
        }
        stats.put("workerId", workerId);
        stats.put("totalReports", reports.size());
        stats.put("totalOutputQty", totalOutput);
        stats.put("totalDurationMinutes", totalDuration);
        if (totalDuration.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal efficiency = BigDecimal.valueOf(totalOutput).divide(totalDuration, 4, RoundingMode.HALF_UP);
            stats.put("efficiency", efficiency);
        } else {
            stats.put("efficiency", BigDecimal.ZERO);
        }
        return stats;
    }

    @Override
    public Map<String, Object> getTeamStats(Long teamId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<JnWorkReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JnWorkReport::getTeamId, teamId);
        if (startDate != null) {
            wrapper.ge(JnWorkReport::getCreateTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(JnWorkReport::getCreateTime, endDate.atTime(23, 59, 59));
        }
        List<JnWorkReport> reports = workReportMapper.selectList(wrapper);
        Map<String, Object> stats = new HashMap<>();
        int totalOutput = 0;
        BigDecimal totalDuration = BigDecimal.ZERO;
        for (JnWorkReport r : reports) {
            totalOutput += (r.getOutputQty() != null ? r.getOutputQty() : 0);
            if (r.getDurationMinutes() != null) {
                totalDuration = totalDuration.add(r.getDurationMinutes());
            }
        }
        stats.put("teamId", teamId);
        stats.put("totalReports", reports.size());
        stats.put("totalOutputQty", totalOutput);
        stats.put("totalDurationMinutes", totalDuration);
        if (totalDuration.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal efficiency = BigDecimal.valueOf(totalOutput).divide(totalDuration, 4, RoundingMode.HALF_UP);
            stats.put("efficiency", efficiency);
        } else {
            stats.put("efficiency", BigDecimal.ZERO);
        }
        return stats;
    }

    private String generateReportNo() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "RP-" + datePart + "-";
        LambdaQueryWrapper<JnWorkReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(JnWorkReport::getReportNo, prefix);
        wrapper.orderByDesc(JnWorkReport::getReportNo);
        wrapper.last("LIMIT 1");
        JnWorkReport last = workReportMapper.selectOne(wrapper);
        int seq = 1;
        if (last != null && last.getReportNo() != null) {
            String lastCode = last.getReportNo();
            String seqStr = lastCode.substring(lastCode.lastIndexOf("-") + 1);
            try {
                seq = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException e) {
                seq = 1;
            }
        }
        return prefix + String.format("%04d", seq);
    }
}
