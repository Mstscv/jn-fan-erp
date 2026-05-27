package com.jn.erp.production.routing.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.routing.domain.JnRoutingLine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnRoutingLineMapper extends BaseMapperPlus<JnRoutingLine> {

    List<JnRoutingLine> selectByRoutingId(@Param("routingId") Long routingId);

    int deleteByRoutingId(@Param("routingId") Long routingId);
}
