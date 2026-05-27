package com.jn.erp.finance.costing.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.finance.costing.domain.JnWorkOrderCost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnWorkOrderCostMapper extends BaseMapperPlus<JnWorkOrderCost> {

    List<JnWorkOrderCost> selectByOrderId(@Param("orderId") Long orderId);

}
