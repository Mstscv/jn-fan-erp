package com.jn.erp.purchase.po.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jn.erp.common.core.utils.SequenceUtils;
import com.jn.erp.purchase.po.domain.JnPoLine;
import com.jn.erp.purchase.po.domain.JnPurchaseOrder;
import com.jn.erp.purchase.po.mapper.JnPoLineMapper;
import com.jn.erp.purchase.po.mapper.JnPurchaseOrderMapper;
import com.jn.erp.purchase.po.service.IJnPurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class JnPurchaseOrderServiceImpl implements IJnPurchaseOrderService {

    @Autowired
    private JnPurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    private JnPoLineMapper poLineMapper;

    @Autowired
    private SequenceUtils sequenceUtils;

    @Override
    public List<JnPurchaseOrder> selectList(JnPurchaseOrder order) {
        LambdaQueryWrapper<JnPurchaseOrder> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(JnPurchaseOrder::getDelFlag, "0");
        if (order != null) {
            if (order.getPoNo() != null && !order.getPoNo().isEmpty()) {
                wrapper.like(JnPurchaseOrder::getPoNo, order.getPoNo());
            }
            if (order.getSupplierId() != null) {
                wrapper.eq(JnPurchaseOrder::getSupplierId, order.getSupplierId());
            }
            if (order.getSupplierName() != null && !order.getSupplierName().isEmpty()) {
                wrapper.like(JnPurchaseOrder::getSupplierName, order.getSupplierName());
            }
            if (order.getBuyer() != null && !order.getBuyer().isEmpty()) {
                wrapper.like(JnPurchaseOrder::getBuyer, order.getBuyer());
            }
            if (order.getStatus() != null && !order.getStatus().isEmpty()) {
                wrapper.eq(JnPurchaseOrder::getStatus, order.getStatus());
            }
        }
        wrapper.orderByDesc(JnPurchaseOrder::getCreateTime);
        return purchaseOrderMapper.selectList(wrapper);
    }

    @Override
    public JnPurchaseOrder getById(Long poId) {
        return purchaseOrderMapper.selectById(poId);
    }

    @Override
    public JnPurchaseOrder getByIdWithLines(Long poId) {
        JnPurchaseOrder order = purchaseOrderMapper.selectById(poId);
        if (order != null) {
            LambdaQueryWrapper<JnPoLine> lineWrapper = Wrappers.lambdaQuery();
            lineWrapper.eq(JnPoLine::getPoId, poId);
            List<JnPoLine> lines = poLineMapper.selectList(lineWrapper);
            order.getParams().put("lines", lines);
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWithLines(JnPurchaseOrder order, List<JnPoLine> lines) {
        order.setPoNo(generatePoNo());
        order.setDelFlag("0");
        order.setStatus("DRAFT");
        order.setPaidAmount(BigDecimal.ZERO);
        if (order.getTotalAmount() == null) {
            order.setTotalAmount(BigDecimal.ZERO);
        }
        int result = purchaseOrderMapper.insert(order);
        if (lines != null && !lines.isEmpty()) {
            for (JnPoLine line : lines) {
                line.setPoId(order.getPoId());
                if (line.getQuantity() == null) {
                    line.setQuantity(1);
                }
                if (line.getReceivedQty() == null) {
                    line.setReceivedQty(0);
                }
                if (line.getUnitPrice() != null && line.getQuantity() != null) {
                    line.setAmount(line.getUnitPrice().multiply(BigDecimal.valueOf(line.getQuantity())));
                }
                poLineMapper.insert(line);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWithLines(JnPurchaseOrder order, List<JnPoLine> lines) {
        int result = purchaseOrderMapper.updateById(order);
        LambdaQueryWrapper<JnPoLine> lineWrapper = Wrappers.lambdaQuery();
        lineWrapper.eq(JnPoLine::getPoId, order.getPoId());
        poLineMapper.delete(lineWrapper);
        if (lines != null && !lines.isEmpty()) {
            for (JnPoLine line : lines) {
                line.setLineId(null);
                line.setPoId(order.getPoId());
                if (line.getQuantity() == null) {
                    line.setQuantity(1);
                }
                if (line.getReceivedQty() == null) {
                    line.setReceivedQty(0);
                }
                if (line.getUnitPrice() != null && line.getQuantity() != null) {
                    line.setAmount(line.getUnitPrice().multiply(BigDecimal.valueOf(line.getQuantity())));
                }
                poLineMapper.insert(line);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] poIds) {
        List<Long> ids = Arrays.asList(poIds);
        LambdaQueryWrapper<JnPurchaseOrder> wrapper = Wrappers.lambdaQuery();
        wrapper.in(JnPurchaseOrder::getPoId, ids);
        JnPurchaseOrder updateEntity = new JnPurchaseOrder();
        updateEntity.setDelFlag("1");
        int result = purchaseOrderMapper.update(updateEntity, wrapper);
        LambdaQueryWrapper<JnPoLine> lineWrapper = Wrappers.lambdaQuery();
        lineWrapper.in(JnPoLine::getPoId, ids);
        poLineMapper.delete(lineWrapper);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(Long poId, String newStatus) {
        JnPurchaseOrder order = new JnPurchaseOrder();
        order.setPoId(poId);
        order.setStatus(newStatus);
        return purchaseOrderMapper.updateById(order);
    }

    @Override
    public String generatePoNo() {
        return sequenceUtils.generate("PO_NO");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approve(Long poId, String username) {
        JnPurchaseOrder order = new JnPurchaseOrder();
        order.setPoId(poId);
        order.setStatus("ORDERED");
        order.setApproveBy(username);
        order.setApproveTime(LocalDateTime.now());
        return purchaseOrderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int reject(Long poId, String username) {
        JnPurchaseOrder order = new JnPurchaseOrder();
        order.setPoId(poId);
        order.setStatus("CANCELLED");
        order.setApproveBy(username);
        order.setApproveTime(LocalDateTime.now());
        return purchaseOrderMapper.updateById(order);
    }
}
