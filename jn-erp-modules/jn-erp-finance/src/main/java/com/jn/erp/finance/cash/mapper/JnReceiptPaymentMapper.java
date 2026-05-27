package com.jn.erp.finance.cash.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.finance.cash.domain.JnReceiptPayment;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface JnReceiptPaymentMapper extends BaseMapperPlus<JnReceiptPayment> {

    List<JnReceiptPayment> selectByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<JnReceiptPayment> selectByCustomerOrSupplier(@Param("customerId") Long customerId, @Param("supplierId") Long supplierId);
}
