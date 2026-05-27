package com.jn.erp.production.outsource.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.outsource.domain.JnOutsourceOrderLine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnOutsourceOrderLineMapper extends BaseMapperPlus<JnOutsourceOrderLine> {

    List<JnOutsourceOrderLine> selectByOrderId(@Param("orderId") Long orderId);

    int deleteByOrderId(@Param("orderId") Long orderId);

}
