package com.jn.erp.finance.cash.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.finance.cash.domain.JnReceiptPayment;
import com.jn.erp.finance.cash.mapper.JnReceiptPaymentMapper;
import com.jn.erp.finance.cash.service.IJnReceiptPaymentService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class JnReceiptPaymentServiceImpl extends ServiceImpl<JnReceiptPaymentMapper, JnReceiptPayment> implements IJnReceiptPaymentService {

    @Autowired
    private JnReceiptPaymentMapper receiptPaymentMapper;

    @Override
    public List<JnReceiptPayment> selectList(JnReceiptPayment query) {
        LambdaQueryWrapper<JnReceiptPayment> wrapper = new LambdaQueryWrapper<>();
        if (query.getSlipType() != null) {
            wrapper.eq(JnReceiptPayment::getSlipType, query.getSlipType());
        }
        if (query.getDirection() != null) {
            wrapper.eq(JnReceiptPayment::getDirection, query.getDirection());
        }
        if (query.getStatus() != null) {
            wrapper.eq(JnReceiptPayment::getStatus, query.getStatus());
        }
        if (query.getCustomerId() != null) {
            wrapper.eq(JnReceiptPayment::getCustomerId, query.getCustomerId());
        }
        if (query.getSupplierId() != null) {
            wrapper.eq(JnReceiptPayment::getSupplierId, query.getSupplierId());
        }
        wrapper.orderByDesc(JnReceiptPayment::getTransactionDate);
        return receiptPaymentMapper.selectList(wrapper);
    }

    @Override
    public JnReceiptPayment selectById(Long slipId) {
        return receiptPaymentMapper.selectById(slipId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnReceiptPayment receiptPayment) {
        receiptPayment.setSlipNo(generateSlipNo(receiptPayment.getSlipType()));
        if (receiptPayment.getStatus() == null) {
            receiptPayment.setStatus("DRAFT");
        }
        receiptPayment.setCreateBy(SecurityUtils.getUsername());
        return receiptPaymentMapper.insert(receiptPayment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnReceiptPayment receiptPayment) {
        receiptPayment.setUpdateBy(SecurityUtils.getUsername());
        return receiptPaymentMapper.updateById(receiptPayment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] slipIds) {
        int count = 0;
        for (Long id : slipIds) {
            count += receiptPaymentMapper.deleteById(id);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long slipId) {
        JnReceiptPayment slip = receiptPaymentMapper.selectById(slipId);
        if (slip != null) {
            slip.setStatus("APPROVED");
            slip.setApprovedBy(SecurityUtils.getUsername());
            slip.setApprovedTime(LocalDateTime.now());
            receiptPaymentMapper.updateById(slip);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long slipId) {
        JnReceiptPayment slip = receiptPaymentMapper.selectById(slipId);
        if (slip != null) {
            slip.setStatus("CANCELLED");
            receiptPaymentMapper.updateById(slip);
        }
    }

    private String generateSlipNo(String slipType) {
        String prefix;
        if ("RECEIPT".equals(slipType)) {
            prefix = "RC";
        } else if ("PAYMENT".equals(slipType)) {
            prefix = "PY";
        } else if ("TRANSFER".equals(slipType)) {
            prefix = "TF";
        } else {
            prefix = "SL";
        }
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String datePrefix = prefix + "-" + datePart + "-";
        LambdaQueryWrapper<JnReceiptPayment> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(JnReceiptPayment::getSlipNo, datePrefix);
        wrapper.orderByDesc(JnReceiptPayment::getSlipNo);
        wrapper.last("LIMIT 1");
        List<JnReceiptPayment> last = receiptPaymentMapper.selectList(wrapper);
        int seq = 1;
        if (last != null && !last.isEmpty()) {
            String lastNo = last.get(0).getSlipNo();
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
