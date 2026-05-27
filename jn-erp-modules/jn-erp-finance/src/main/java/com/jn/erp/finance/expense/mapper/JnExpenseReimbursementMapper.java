package com.jn.erp.finance.expense.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.finance.expense.domain.JnExpenseReimbursement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnExpenseReimbursementMapper extends BaseMapperPlus<JnExpenseReimbursement> {

    List<JnExpenseReimbursement> selectByApplicant(@Param("applicant") String applicant);

    List<JnExpenseReimbursement> selectByStatus(@Param("status") String status);
}
