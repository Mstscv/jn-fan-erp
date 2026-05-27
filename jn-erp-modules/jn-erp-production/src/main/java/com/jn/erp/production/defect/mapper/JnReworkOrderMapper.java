package com.jn.erp.production.defect.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.defect.domain.JnReworkOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnReworkOrderMapper extends BaseMapperPlus<JnReworkOrder> {

    List<JnReworkOrder> selectByOrderId(@Param("orderId") Long orderId);

    List<JnReworkOrder> selectByDefectId(@Param("defectId") Long defectId);
}
