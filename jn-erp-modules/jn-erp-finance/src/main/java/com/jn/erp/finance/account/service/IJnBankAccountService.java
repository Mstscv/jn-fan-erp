package com.jn.erp.finance.account.service;

import com.jn.erp.finance.account.domain.JnBankAccount;

import java.util.List;

public interface IJnBankAccountService {

    List<JnBankAccount> selectList(JnBankAccount query);

    JnBankAccount selectById(Long bankAccountId);

    int insert(JnBankAccount bankAccount);

    int update(JnBankAccount bankAccount);

    int deleteByIds(Long[] bankAccountIds);
}
