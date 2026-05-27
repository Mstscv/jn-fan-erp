package com.jn.erp.production.workorder.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.workorder.domain.JnWorkOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnWorkOrderMapper extends BaseMapperPlus<JnWorkOrder> {

    List<JnWorkOrder> selectByStatus(@Param("status") String status);

    List<JnWorkOrder> selectPendingScheduling();

}
