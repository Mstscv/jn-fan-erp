package com.jn.erp.finance.expense.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.finance.expense.domain.JnExpenseReimbursement;
import com.jn.erp.finance.expense.mapper.JnExpenseReimbursementMapper;
import com.jn.erp.finance.expense.service.IJnExpenseReimbursementService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class JnExpenseReimbursementServiceImpl extends ServiceImpl<JnExpenseReimbursementMapper, JnExpenseReimbursement> implements IJnExpenseReimbursementService {

    @Autowired
    private JnExpenseReimbursementMapper expenseReimbursementMapper;

    @Override
    public List<JnExpenseReimbursement> selectList(JnExpenseReimbursement query) {
        LambdaQueryWrapper<JnExpenseReimbursement> wrapper = new LambdaQueryWrapper<>();
        if (query.getExpenseType() != null) {
            wrapper.eq(JnExpenseReimbursement::getExpenseType, query.getExpenseType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(JnExpenseReimbursement::getStatus, query.getStatus());
        }
        if (query.getApplicant() != null) {
            wrapper.eq(JnExpenseReimbursement::getApplicant, query.getApplicant());
        }
        if (query.getDepartment() != null) {
            wrapper.eq(JnExpenseReimbursement::getDepartment, query.getDepartment());
        }
        wrapper.orderByDesc(JnExpenseReimbursement::getExpenseDate);
        return expenseReimbursementMapper.selectList(wrapper);
    }

    @Override
    public JnExpenseReimbursement selectById(Long expenseId) {
        return expenseReimbursementMapper.selectById(expenseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnExpenseReimbursement expenseReimbursement) {
        expenseReimbursement.setExpenseNo(generateExpenseNo());
        if (expenseReimbursement.getStatus() == null) {
            expenseReimbursement.setStatus("DRAFT");
        }
        if (expenseReimbursement.getInvoiceAttached() == null) {
            expenseReimbursement.setInvoiceAttached("N");
        }
        expenseReimbursement.setCreateBy(SecurityUtils.getUsername());
        return expenseReimbursementMapper.insert(expenseReimbursement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnExpenseReimbursement expenseReimbursement) {
        expenseReimbursement.setUpdateBy(SecurityUtils.getUsername());
        return expenseReimbursementMapper.updateById(expenseReimbursement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] expenseIds) {
        int count = 0;
        for (Long id : expenseIds) {
            count += expenseReimbursementMapper.deleteById(id);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(Long expenseId) {
        JnExpenseReimbursement expense = expenseReimbursementMapper.selectById(expenseId);
        if (expense != null) {
            expense.setStatus("PENDING");
            expenseReimbursementMapper.updateById(expense);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long expenseId) {
        JnExpenseReimbursement expense = expenseReimbursementMapper.selectById(expenseId);
        if (expense != null) {
            expense.setStatus("APPROVED");
            expense.setApprover(SecurityUtils.getUsername());
            expense.setApprovedTime(LocalDateTime.now());
            expenseReimbursementMapper.updateById(expense);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long expenseId, String reason) {
        JnExpenseReimbursement expense = expenseReimbursementMapper.selectById(expenseId);
        if (expense != null) {
            expense.setStatus("REJECTED");
            expense.setRemark(reason);
            expenseReimbursementMapper.updateById(expense);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pay(Long expenseId) {
        JnExpenseReimbursement expense = expenseReimbursementMapper.selectById(expenseId);
        if (expense != null) {
            expense.setStatus("PAID");
            expenseReimbursementMapper.updateById(expense);
        }
    }

    private String generateExpenseNo() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String datePrefix = "EXP-" + datePart + "-";
        LambdaQueryWrapper<JnExpenseReimbursement> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(JnExpenseReimbursement::getExpenseNo, datePrefix);
        wrapper.orderByDesc(JnExpenseReimbursement::getExpenseNo);
        wrapper.last("LIMIT 1");
        List<JnExpenseReimbursement> last = expenseReimbursementMapper.selectList(wrapper);
        int seq = 1;
        if (last != null && !last.isEmpty()) {
            String lastNo = last.get(0).getExpenseNo();
            String seqStr = lastNo.substring(lastNo.lastIndexOf("-") + 1);
            try {
                seq = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException e) {
                seq = 1;
            }
        }
        return datePrefix + String.format("%04d", seq);
    }
}
