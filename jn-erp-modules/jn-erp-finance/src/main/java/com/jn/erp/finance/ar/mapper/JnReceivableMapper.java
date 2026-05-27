package com.jn.erp.finance.ar.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.finance.ar.domain.JnReceivable;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface JnReceivableMapper extends BaseMapperPlus<JnReceivable> {

    List<JnReceivable> selectByCustomer(@Param("customerId") Long customerId);

    List<JnReceivable> selectOverdue(@Param("days") Integer days);

    List<JnReceivable> selectByStatus(@Param("status") String status);

}
