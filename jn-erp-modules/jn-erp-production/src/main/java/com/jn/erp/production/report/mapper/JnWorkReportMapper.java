package com.jn.erp.production.report.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.report.domain.JnWorkReport;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface JnWorkReportMapper extends BaseMapperPlus<JnWorkReport> {

    List<JnWorkReport> selectByOrderId(@Param("orderId") Long orderId);

    List<JnWorkReport> selectByWorkerId(@Param("workerId") Long workerId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
