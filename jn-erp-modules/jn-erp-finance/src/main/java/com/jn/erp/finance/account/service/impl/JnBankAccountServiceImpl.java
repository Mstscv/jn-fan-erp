package com.jn.erp.finance.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.finance.account.domain.JnBankAccount;
import com.jn.erp.finance.account.mapper.JnBankAccountMapper;
import com.jn.erp.finance.account.service.IJnBankAccountService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class JnBankAccountServiceImpl extends ServiceImpl<JnBankAccountMapper, JnBankAccount> implements IJnBankAccountService {

    @Autowired
    private JnBankAccountMapper bankAccountMapper;

    @Override
    public List<JnBankAccount> selectList(JnBankAccount query) {
        LambdaQueryWrapper<JnBankAccount> wrapper = new LambdaQueryWrapper<>();
        if (query.getBankName() != null) {
            wrapper.like(JnBankAccount::getBankName, query.getBankName());
        }
        if (query.getAccountName() != null) {
            wrapper.like(JnBankAccount::getAccountName, query.getAccountName());
        }
        if (query.getIsActive() != null) {
            wrapper.eq(JnBankAccount::getIsActive, query.getIsActive());
        }
        wrapper.orderByAsc(JnBankAccount::getBankAccountId);
        return bankAccountMapper.selectList(wrapper);
    }

    @Override
    public JnBankAccount selectById(Long bankAccountId) {
        return bankAccountMapper.selectById(bankAccountId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnBankAccount bankAccount) {
        if (bankAccount.getCurrency() == null) {
            bankAccount.setCurrency("CNY");
        }
        if (bankAccount.getBalance() == null) {
            bankAccount.setBalance(BigDecimal.ZERO);
        }
        if (bankAccount.getIsActive() == null) {
            bankAccount.setIsActive("Y");
        }
        bankAccount.setCreateBy(SecurityUtils.getUsername());
        return bankAccountMapper.insert(bankAccount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnBankAccount bankAccount) {
        bankAccount.setUpdateBy(SecurityUtils.getUsername());
        return bankAccountMapper.updateById(bankAccount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] bankAccountIds) {
        int count = 0;
        for (Long id : bankAccountIds) {
            count += bankAccountMapper.deleteById(id);
        }
        return count;
    }
}
