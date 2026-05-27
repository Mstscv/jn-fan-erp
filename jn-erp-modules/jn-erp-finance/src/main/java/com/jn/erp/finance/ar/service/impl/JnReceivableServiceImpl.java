package com.jn.erp.finance.ar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.common.core.utils.SequenceUtils;
import com.jn.erp.finance.ar.domain.JnReceivable;
import com.jn.erp.finance.ar.domain.JnReceivableWriteOff;
import com.jn.erp.finance.ar.mapper.JnReceivableMapper;
import com.jn.erp.finance.ar.mapper.JnReceivableWriteOffMapper;
import com.jn.erp.finance.ar.service.IJnReceivableService;
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
public class JnReceivableServiceImpl extends ServiceImpl<JnReceivableMapper, JnReceivable> implements IJnReceivableService {

    @Autowired
    private JnReceivableWriteOffMapper writeOffMapper;

    @Autowired
    private SequenceUtils sequenceUtils;

    @Override
    public List<JnReceivable> selectList(JnReceivable query) {
        LambdaQueryWrapper<JnReceivable> wrapper = Wrappers.lambdaQuery();
        if (query != null) {
            if (query.getReceivableNo() != null && !query.getReceivableNo().isEmpty()) {
                wrapper.like(JnReceivable::getReceivableNo, query.getReceivableNo());
            }
            if (query.getCustomerId() != null) {
                wrapper.eq(JnReceivable::getCustomerId, query.getCustomerId());
            }
            if (query.getCustomerName() != null && !query.getCustomerName().isEmpty()) {
                wrapper.like(JnReceivable::getCustomerName, query.getCustomerName());
            }
            if (query.getSourceType() != null && !query.getSourceType().isEmpty()) {
                wrapper.eq(JnReceivable::getSourceType, query.getSourceType());
            }
            if (query.getStatus() != null && !query.getStatus().isEmpty()) {
                wrapper.eq(JnReceivable::getStatus, query.getStatus());
            }
        }
        wrapper.orderByDesc(JnReceivable::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public JnReceivable selectById(Long receivableId) {
        return baseMapper.selectById(receivableId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnReceivable receivable) {
        receivable.setReceivableNo(generateReceivableNo());
        if (receivable.getPaidAmount() == null) {
            receivable.setPaidAmount(BigDecimal.ZERO);
        }
        if (receivable.getCurrency() == null) {
            receivable.setCurrency("CNY");
        }
        if (receivable.getStatus() == null) {
            receivable.setStatus("PENDING");
        }
        if (receivable.getTotalAmount() != null) {
            receivable.setBalanceAmount(receivable.getTotalAmount().subtract(receivable.getPaidAmount()));
        }
        return baseMapper.insert(receivable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnReceivable receivable) {
        if (receivable.getTotalAmount() != null && receivable.getPaidAmount() != null) {
            receivable.setBalanceAmount(receivable.getTotalAmount().subtract(receivable.getPaidAmount()));
        }
        return baseMapper.updateById(receivable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] receivableIds) {
        return baseMapper.deleteBatchIds(Arrays.asList(receivableIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void writeOff(Long receivableId, BigDecimal amount) {
        JnReceivable receivable = baseMapper.selectById(receivableId);
        if (receivable == null) {
            throw new RuntimeException("应收账款记录不存在");
        }
        if ("CANCELLED".equals(receivable.getStatus())) {
            throw new RuntimeException("已取消的记录无法核销");
        }
        if ("SETTLED".equals(receivable.getStatus())) {
            throw new RuntimeException("已结清的记录无需核销");
        }

        BigDecimal newPaidAmount = receivable.getPaidAmount().add(amount);
        BigDecimal newBalanceAmount = receivable.getTotalAmount().subtract(newPaidAmount);

        receivable.setPaidAmount(newPaidAmount);
        receivable.setBalanceAmount(newBalanceAmount);

        if (newBalanceAmount.compareTo(BigDecimal.ZERO) <= 0) {
            receivable.setStatus("SETTLED");
        } else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
            receivable.setStatus("PARTIAL");
        }

        baseMapper.updateById(receivable);

        JnReceivableWriteOff writeOff = new JnReceivableWriteOff();
        writeOff.setReceivableId(receivableId);
        writeOff.setReceivableNo(receivable.getReceivableNo());
        writeOff.setWriteOffNo(generateWriteOffNo());
        writeOff.setWriteOffDate(LocalDate.now());
        writeOff.setAmount(amount);
        writeOffMapper.insert(writeOff);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long receivableId) {
        JnReceivable receivable = baseMapper.selectById(receivableId);
        if (receivable == null) {
            throw new RuntimeException("应收账款记录不存在");
        }
        if ("SETTLED".equals(receivable.getStatus())) {
            throw new RuntimeException("已结清的记录无法取消");
        }
        receivable.setStatus("CANCELLED");
        baseMapper.updateById(receivable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateFromDelivery(Long deliveryId, String deliveryNo, Long customerId, String customerName, BigDecimal totalAmount) {
        JnReceivable receivable = new JnReceivable();
        receivable.setSourceType("DELIVERY");
        receivable.setSourceId(deliveryId);
        receivable.setSourceNo(deliveryNo);
        receivable.setCustomerId(customerId);
        receivable.setCustomerName(customerName);
        receivable.setTotalAmount(totalAmount);
        receivable.setPaidAmount(BigDecimal.ZERO);
        receivable.setBalanceAmount(totalAmount);
        receivable.setCurrency("CNY");
        receivable.setStatus("PENDING");
        receivable.setReceivableNo(generateReceivableNo());
        baseMapper.insert(receivable);
    }

    @Override
    public Map<String, Object> getAgingAnalysis(Long customerId) {
        LambdaQueryWrapper<JnReceivable> wrapper = Wrappers.lambdaQuery();
        wrapper.ne(JnReceivable::getStatus, "SETTLED");
        wrapper.ne(JnReceivable::getStatus, "CANCELLED");
        if (customerId != null) {
            wrapper.eq(JnReceivable::getCustomerId, customerId);
        }
        List<JnReceivable> list = baseMapper.selectList(wrapper);

        BigDecimal bucket0to30 = BigDecimal.ZERO;
        BigDecimal bucket31to60 = BigDecimal.ZERO;
        BigDecimal bucket61to90 = BigDecimal.ZERO;
        BigDecimal bucket90plus = BigDecimal.ZERO;
        LocalDate today = LocalDate.now();

        for (JnReceivable r : list) {
            if (r.getDueDate() == null || r.getBalanceAmount() == null) {
                continue;
            }
            long daysOverdue = ChronoUnit.DAYS.between(r.getDueDate(), today);
            if (daysOverdue <= 0) {
                continue;
            }
            if (daysOverdue <= 30) {
                bucket0to30 = bucket0to30.add(r.getBalanceAmount());
            } else if (daysOverdue <= 60) {
                bucket31to60 = bucket31to60.add(r.getBalanceAmount());
            } else if (daysOverdue <= 90) {
                bucket61to90 = bucket61to90.add(r.getBalanceAmount());
            } else {
                bucket90plus = bucket90plus.add(r.getBalanceAmount());
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

    private String generateReceivableNo() {
        return sequenceUtils.generate("RECEIVABLE_NO");
    }

    private String generateWriteOffNo() {
        return sequenceUtils.generate("WRITE_OFF_NO");
    }

}
