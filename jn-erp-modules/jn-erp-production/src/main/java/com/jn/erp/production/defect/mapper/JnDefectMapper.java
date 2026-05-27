package com.jn.erp.production.defect.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.defect.domain.JnDefect;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnDefectMapper extends BaseMapperPlus<JnDefect> {

    List<JnDefect> selectByOrderId(@Param("orderId") Long orderId);

    List<JnDefect> selectByReportId(@Param("reportId") Long reportId);
}
