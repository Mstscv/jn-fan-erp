package com.jn.erp.production.outsource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.outsource.domain.JnOutsourceReceipt;
import com.jn.erp.production.outsource.domain.JnOutsourceReceiptLine;
import com.jn.erp.production.outsource.mapper.JnOutsourceReceiptLineMapper;
import com.jn.erp.production.outsource.mapper.JnOutsourceReceiptMapper;
import com.jn.erp.production.outsource.service.IJnOutsourceReceiptService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Service
public class JnOutsourceReceiptServiceImpl extends ServiceImpl<JnOutsourceReceiptMapper, JnOutsourceReceipt> implements IJnOutsourceReceiptService {

    @Autowired
    private JnOutsourceReceiptMapper outsourceReceiptMapper;

    @Autowired
    private JnOutsourceReceiptLineMapper outsourceReceiptLineMapper;

    @Override
    public List<JnOutsourceReceipt> selectList(JnOutsourceReceipt query) {
        LambdaQueryWrapper<JnOutsourceReceipt> wrapper = new LambdaQueryWrapper<>();
        if (query.getReceiptNo() != null && !query.getReceiptNo().isEmpty()) {
            wrapper.like(JnOutsourceReceipt::getReceiptNo, query.getReceiptNo());
        }
        if (query.getOrderNo() != null && !query.getOrderNo().isEmpty()) {
            wrapper.like(JnOutsourceReceipt::getOrderNo, query.getOrderNo());
        }
        if (query.getSupplierName() != null && !query.getSupplierName().isEmpty()) {
            wrapper.like(JnOutsourceReceipt::getSupplierName, query.getSupplierName());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(JnOutsourceReceipt::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(JnOutsourceReceipt::getCreateTime);
        return outsourceReceiptMapper.selectList(wrapper);
    }

    @Override
    public JnOutsourceReceipt selectById(Long id) {
        return outsourceReceiptMapper.selectById(id);
    }

    @Override
    public JnOutsourceReceipt selectWithLines(Long id) {
        JnOutsourceReceipt receipt = outsourceReceiptMapper.selectById(id);
        if (receipt != null) {
            List<JnOutsourceReceiptLine> lines = outsourceReceiptLineMapper.selectByReceiptId(id);
            receipt.setParams(new HashMap<>());
            receipt.getParams().put("lines", lines);
        }
        return receipt;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnOutsourceReceipt receipt, List<JnOutsourceReceiptLine> lines) {
        receipt.setReceiptNo(generateReceiptNo());
        if (receipt.getStatus() == null) {
            receipt.setStatus("DRAFT");
        }
        receipt.setCreateBy(SecurityUtils.getUsername());
        int result = outsourceReceiptMapper.insert(receipt);

        if (lines != null && !lines.isEmpty()) {
            for (JnOutsourceReceiptLine line : lines) {
                line.setReceiptId(receipt.getReceiptId());
                if (line.getQcResult() == null) {
                    line.setQcResult("PENDING");
                }
                if (line.getSortOrder() == null) {
                    line.setSortOrder(0);
                }
                outsourceReceiptLineMapper.insert(line);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnOutsourceReceipt receipt, List<JnOutsourceReceiptLine> lines) {
        receipt.setUpdateBy(SecurityUtils.getUsername());
        int result = outsourceReceiptMapper.updateById(receipt);

        outsourceReceiptLineMapper.deleteByReceiptId(receipt.getReceiptId());

        if (lines != null && !lines.isEmpty()) {
            for (JnOutsourceReceiptLine line : lines) {
                line.setLineId(null);
                line.setReceiptId(receipt.getReceiptId());
                if (line.getQcResult() == null) {
                    line.setQcResult("PENDING");
                }
                if (line.getSortOrder() == null) {
                    line.setSortOrder(0);
                }
                outsourceReceiptLineMapper.insert(line);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] ids) {
        int count = 0;
        for (Long id : ids) {
            outsourceReceiptLineMapper.deleteByReceiptId(id);
            count += outsourceReceiptMapper.deleteById(id);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long receiptId) {
        JnOutsourceReceipt receipt = outsourceReceiptMapper.selectById(receiptId);
        if (receipt == null) {
            throw new RuntimeException("委外收货单不存在: " + receiptId);
        }
        receipt.setStatus("APPROVED");
        receipt.setUpdateBy(SecurityUtils.getUsername());
        outsourceReceiptMapper.updateById(receipt);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long receiptId, String reason) {
        JnOutsourceReceipt receipt = outsourceReceiptMapper.selectById(receiptId);
        if (receipt == null) {
            throw new RuntimeException("委外收货单不存在: " + receiptId);
        }
        receipt.setStatus("REJECTED");
        receipt.setRemark(reason);
        receipt.setUpdateBy(SecurityUtils.getUsername());
        outsourceReceiptMapper.updateById(receipt);
    }

    private String generateReceiptNo() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "OR-" + datePart + "-";
        LambdaQueryWrapper<JnOutsourceReceipt> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(JnOutsourceReceipt::getReceiptNo, prefix);
        wrapper.orderByDesc(JnOutsourceReceipt::getReceiptNo);
        wrapper.last("LIMIT 1");
        JnOutsourceReceipt last = outsourceReceiptMapper.selectOne(wrapper);
        int seq = 1;
        if (last != null && last.getReceiptNo() != null) {
            String lastCode = last.getReceiptNo();
            String seqStr = lastCode.substring(lastCode.lastIndexOf("-") + 1);
            try {
                seq = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException e) {
                seq = 1;
            }
        }
        return prefix + String.format("%04d", seq);
    }

}
