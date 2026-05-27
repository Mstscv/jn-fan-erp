package com.jn.erp.finance.account.service;

import com.jn.erp.finance.account.domain.JnAccount;

import java.util.List;

public interface IJnAccountService {

    List<JnAccount> selectList(JnAccount query);

    JnAccount selectById(Long accountId);

    int insert(JnAccount account);

    int update(JnAccount account);

    int deleteByIds(Long[] accountIds);
}
