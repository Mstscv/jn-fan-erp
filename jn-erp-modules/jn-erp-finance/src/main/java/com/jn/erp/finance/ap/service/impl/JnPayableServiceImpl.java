package com.jn.erp.finance.ap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.common.core.utils.SequenceUtils;
import com.jn.erp.finance.ap.domain.JnPayable;
import com.jn.erp.finance.ap.domain.JnPayablePayment;
import com.jn.erp.finance.ap.mapper.JnPayableMapper;
import com.jn.erp.finance.ap.mapper.JnPayablePaymentMapper;
import com.jn.erp.finance.ap.service.IJnPayableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JnPayableServiceImpl extends ServiceImpl<JnPayableMapper, JnPayable> implements IJnPayableService {

    @Autowired
    private JnPayablePaymentMapper paymentMapper;

    @Autowired
    private SequenceUtils sequenceUtils;

    @Override
    public List<JnPayable> selectList(JnPayable query) {
        LambdaQueryWrapper<JnPayable> wrapper = Wrappers.lambdaQuery();
        if (query != null) {
            if (query.getPayableNo() != null && !query.getPayableNo().isEmpty()) {
                wrapper.like(JnPayable::getPayableNo, query.getPayableNo());
            }
            if (query.getSupplierId() != null) {
                wrapper.eq(JnPayable::getSupplierId, query.getSupplierId());
            }
            if (query.getSupplierName() != null && !query.getSupplierName().isEmpty()) {
                wrapper.like(JnPayable::getSupplierName, query.getSupplierName());
            }
            if (query.getSourceType() != null && !query.getSourceType().isEmpty()) {
                wrapper.eq(JnPayable::getSourceType, query.getSourceType());
            }
            if (query.getStatus() != null && !query.getStatus().isEmpty()) {
                wrapper.eq(JnPayable::getStatus, query.getStatus());
            }
        }
        wrapper.orderByDesc(JnPayable::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public JnPayable selectById(Long payableId) {
        return baseMapper.selectById(payableId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnPayable payable) {
        payable.setPayableNo(generatePayableNo());
        if (payable.getPaidAmount() == null) {
            payable.setPaidAmount(BigDecimal.ZERO);
        }
        if (payable.getCurrency() == null) {
            payable.setCurrency("CNY");
        }
        if (payable.getStatus() == null) {
            payable.setStatus("PENDING");
        }
        if (payable.getTotalAmount() != null) {
            payable.setBalanceAmount(payable.getTotalAmount().subtract(payable.getPaidAmount()));
        }
        return baseMapper.insert(payable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnPayable payable) {
        if (payable.getTotalAmount() != null && payable.getPaidAmount() != null) {
            payable.setBalanceAmount(payable.getTotalAmount().subtract(payable.getPaidAmount()));
        }
        return baseMapper.updateById(payable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] payableIds) {
        return baseMapper.deleteBatchIds(Arrays.asList(payableIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pay(Long payableId, BigDecimal amount) {
        JnPayable payable = baseMapper.selectById(payableId);
        if (payable == null) {
            throw new RuntimeException("应付账款记录不存在");
        }
        if ("CANCELLED".equals(payable.getStatus())) {
            throw new RuntimeException("已取消的记录无法付款");
        }
        if ("SETTLED".equals(payable.getStatus())) {
            throw new RuntimeException("已结清的记录无需付款");
        }

        BigDecimal newPaidAmount = payable.getPaidAmount().add(amount);
        BigDecimal newBalanceAmount = payable.getTotalAmount().subtract(newPaidAmount);

        payable.setPaidAmount(newPaidAmount);
        payable.setBalanceAmount(newBalanceAmount);

        if (newBalanceAmount.compareTo(BigDecimal.ZERO) <= 0) {
            payable.setStatus("SETTLED");
        } else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
            payable.setStatus("PARTIAL");
        }

        baseMapper.updateById(payable);

        JnPayablePayment payment = new JnPayablePayment();
        payment.setPayableId(payableId);
        payment.setPayableNo(payable.getPayableNo());
        payment.setPaymentNo(generatePaymentNo());
        payment.setPaymentDate(LocalDate.now());
        payment.setAmount(amount);
        paymentMapper.insert(payment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long payableId) {
        JnPayable payable = baseMapper.selectById(payableId);
        if (payable == null) {
            throw new RuntimeException("应付账款记录不存在");
        }
        if ("SETTLED".equals(payable.getStatus())) {
            throw new RuntimeException("已结清的记录无法取消");
        }
        payable.setStatus("CANCELLED");
        baseMapper.updateById(payable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateFromPurchase(Long purchaseId, String purchaseNo, Long supplierId, String supplierName, BigDecimal totalAmount) {
        JnPayable payable = new JnPayable();
        payable.setSourceType("PURCHASE");
        payable.setSourceId(purchaseId);
        payable.setSourceNo(purchaseNo);
        payable.setSupplierId(supplierId);
        payable.setSupplierName(supplierName);
        payable.setTotalAmount(totalAmount);
        payable.setPaidAmount(BigDecimal.ZERO);
        payable.setBalanceAmount(totalAmount);
        payable.setCurrency("CNY");
        payable.setStatus("PENDING");
        payable.setPayableNo(generatePayableNo());
        baseMapper.insert(payable);
    }

    @Override
    public Map<String, Object> getAgingAnalysis(Long supplierId) {
        LambdaQueryWrapper<JnPayable> wrapper = Wrappers.lambdaQuery();
        wrapper.ne(JnPayable::getStatus, "SETTLED");
        wrapper.ne(JnPayable::getStatus, "CANCELLED");
        if (supplierId != null) {
            wrapper.eq(JnPayable::getSupplierId, supplierId);
        }
        List<JnPayable> list = baseMapper.selectList(wrapper);

        BigDecimal bucket0to30 = BigDecimal.ZERO;
        BigDecimal bucket31to60 = BigDecimal.ZERO;
        BigDecimal bucket61to90 = BigDecimal.ZERO;
        BigDecimal bucket90plus = BigDecimal.ZERO;
        LocalDate today = LocalDate.now();

        for (JnPayable p : list) {
            if (p.getDueDate() == null || p.getBalanceAmount() == null) {
                continue;
            }
            long daysOverdue = ChronoUnit.DAYS.between(p.getDueDate(), today);
            if (daysOverdue <= 0) {
                continue;
            }
            if (daysOverdue <= 30) {
                bucket0to30 = bucket0to30.add(p.getBalanceAmount());
            } else if (daysOverdue <= 60) {
                bucket31to60 = bucket31to60.add(p.getBalanceAmount());
            } else if (daysOverdue <= 90) {
                bucket61to90 = bucket61to90.add(p.getBalanceAmount());
            } else {
                bucket90plus = bucket90plus.add(p.getBalanceAmount());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("0-30天", bucket0to30);
        result.put("31-60天", bucket31to60);
        result.put("61-90天", bucket61to90);
        result.put("90天以上", bucket90plus);
        result.put("total", bucket0to30.add(bucket31to60).add(bucket61to90).add(bucket90plus));

        return result;
    }

    private String generatePayableNo() {
        return sequenceUtils.generate("PAYABLE_NO");
    }

    private String generatePaymentNo() {
        return sequenceUtils.generate("PAYMENT_NO");
    }

}
