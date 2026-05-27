package com.jn.erp.production.handover.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.handover.domain.JnHandover;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnHandoverMapper extends BaseMapperPlus<JnHandover> {

    List<JnHandover> selectByOrderId(@Param("orderId") Long orderId);
}
