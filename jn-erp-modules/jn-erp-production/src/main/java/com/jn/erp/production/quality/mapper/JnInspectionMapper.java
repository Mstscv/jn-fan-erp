package com.jn.erp.production.quality.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.quality.domain.JnInspection;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface JnInspectionMapper extends BaseMapperPlus<JnInspection> {

    List<JnInspection> selectBySourceOrder(@Param("sourceOrderId") Long sourceOrderId);

    List<JnInspection> selectByTypeAndDate(@Param("type") String type, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
