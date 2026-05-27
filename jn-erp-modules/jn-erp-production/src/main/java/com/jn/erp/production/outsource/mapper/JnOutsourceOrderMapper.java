package com.jn.erp.production.outsource.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.outsource.domain.JnOutsourceOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnOutsourceOrderMapper extends BaseMapperPlus<JnOutsourceOrder> {

    List<JnOutsourceOrder> selectBySupplier(@Param("supplierId") Long supplierId);

    List<JnOutsourceOrder> selectByStatus(@Param("status") String status);

}
