package com.jn.erp.finance.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.finance.account.domain.JnAccount;
import com.jn.erp.finance.account.mapper.JnAccountMapper;
import com.jn.erp.finance.account.service.IJnAccountService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class JnAccountServiceImpl extends ServiceImpl<JnAccountMapper, JnAccount> implements IJnAccountService {

    @Autowired
    private JnAccountMapper accountMapper;

    @Override
    public List<JnAccount> selectList(JnAccount query) {
        LambdaQueryWrapper<JnAccount> wrapper = new LambdaQueryWrapper<>();
        if (query.getAccountType() != null) {
            wrapper.eq(JnAccount::getAccountType, query.getAccountType());
        }
        if (query.getAccountName() != null) {
            wrapper.like(JnAccount::getAccountName, query.getAccountName());
        }
        if (query.getAccountCode() != null) {
            wrapper.like(JnAccount::getAccountCode, query.getAccountCode());
        }
        if (query.getIsActive() != null) {
            wrapper.eq(JnAccount::getIsActive, query.getIsActive());
        }
        if (query.getParentId() != null) {
            wrapper.eq(JnAccount::getParentId, query.getParentId());
        }
        wrapper.orderByAsc(JnAccount::getAccountCode);
        return accountMapper.selectList(wrapper);
    }

    @Override
    public JnAccount selectById(Long accountId) {
        return accountMapper.selectById(accountId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnAccount account) {
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        if (account.getDirection() == null) {
            account.setDirection("DEBIT");
        }
        if (account.getIsActive() == null) {
            account.setIsActive("Y");
        }
        if (account.getIsLeaf() == null) {
            account.setIsLeaf("N");
        }
        account.setCreateBy(SecurityUtils.getUsername());
        return accountMapper.insert(account);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnAccount account) {
        account.setUpdateBy(SecurityUtils.getUsername());
        return accountMapper.updateById(account);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] accountIds) {
        int count = 0;
        for (Long id : accountIds) {
            count += accountMapper.deleteById(id);
        }
        return count;
    }
}
