package com.jn.erp.finance.ap.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.finance.ap.domain.JnPayablePayment;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface JnPayablePaymentMapper extends BaseMapperPlus<JnPayablePayment> {

    List<JnPayablePayment> selectByPayableId(@Param("payableId") Long payableId);

}
