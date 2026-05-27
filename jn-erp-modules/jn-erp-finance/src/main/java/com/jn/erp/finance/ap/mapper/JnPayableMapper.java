package com.jn.erp.finance.ap.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.finance.ap.domain.JnPayable;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface JnPayableMapper extends BaseMapperPlus<JnPayable> {

    List<JnPayable> selectBySupplier(@Param("supplierId") Long supplierId);

    List<JnPayable> selectOverdue(@Param("days") Integer days);

    List<JnPayable> selectByStatus(@Param("status") String status);

}
