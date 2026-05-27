package com.jn.erp.finance.expense.service;

import com.jn.erp.finance.expense.domain.JnExpenseReimbursement;

import java.util.List;

public interface IJnExpenseReimbursementService {

    List<JnExpenseReimbursement> selectList(JnExpenseReimbursement query);

    JnExpenseReimbursement selectById(Long expenseId);

    int insert(JnExpenseReimbursement expenseReimbursement);

    int update(JnExpenseReimbursement expenseReimbursement);

    int deleteByIds(Long[] expenseIds);

    void submit(Long expenseId);

    void approve(Long expenseId);

    void reject(Long expenseId, String reason);

    void pay(Long expenseId);
}
