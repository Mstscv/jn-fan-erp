package com.jn.erp.production.outsource.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.outsource.domain.JnOutsourceDispatch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnOutsourceDispatchMapper extends BaseMapperPlus<JnOutsourceDispatch> {

    List<JnOutsourceDispatch> selectByOrderId(@Param("orderId") Long orderId);

}
