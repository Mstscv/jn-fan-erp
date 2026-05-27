package com.jn.erp.production.workorder.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.workorder.domain.JnWorkOrderLine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnWorkOrderLineMapper extends BaseMapperPlus<JnWorkOrderLine> {

    List<JnWorkOrderLine> selectByOrderId(@Param("orderId") Long orderId);

    int deleteByOrderId(@Param("orderId") Long orderId);

}
