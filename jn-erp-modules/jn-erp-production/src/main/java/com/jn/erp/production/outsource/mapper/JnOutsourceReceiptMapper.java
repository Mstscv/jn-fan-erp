package com.jn.erp.production.outsource.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.outsource.domain.JnOutsourceReceipt;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnOutsourceReceiptMapper extends BaseMapperPlus<JnOutsourceReceipt> {

    List<JnOutsourceReceipt> selectByOrderId(@Param("orderId") Long orderId);

}
