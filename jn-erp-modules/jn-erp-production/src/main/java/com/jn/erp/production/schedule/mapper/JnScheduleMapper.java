package com.jn.erp.production.schedule.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.schedule.domain.JnSchedule;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface JnScheduleMapper extends BaseMapperPlus<JnSchedule> {

    List<JnSchedule> selectByOrderId(@Param("orderId") Long orderId);

    List<JnSchedule> selectByWorkCenterAndDate(@Param("workCenterId") Long workCenterId, @Param("scheduledDate") LocalDate scheduledDate);

    List<JnSchedule> selectByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
