package com.jn.erp.production.routing.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.routing.domain.JnRouting;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnRoutingMapper extends BaseMapperPlus<JnRouting> {

    List<JnRouting> selectByProductId(@Param("productId") Long productId);

    JnRouting selectByRoutingCode(@Param("routingCode") String routingCode);
}
