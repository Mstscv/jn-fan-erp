package com.jn.erp.production.outsource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.outsource.domain.JnOutsourceOrder;
import com.jn.erp.production.outsource.domain.JnOutsourceOrderLine;
import com.jn.erp.production.outsource.mapper.JnOutsourceOrderLineMapper;
import com.jn.erp.production.outsource.mapper.JnOutsourceOrderMapper;
import com.jn.erp.production.outsource.service.IJnOutsourceOrderService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Service
public class JnOutsourceOrderServiceImpl extends ServiceImpl<JnOutsourceOrderMapper, JnOutsourceOrder> implements IJnOutsourceOrderService {

    @Autowired
    private JnOutsourceOrderMapper outsourceOrderMapper;

    @Autowired
    private JnOutsourceOrderLineMapper outsourceOrderLineMapper;

    @Override
    public List<JnOutsourceOrder> selectList(JnOutsourceOrder query) {
        LambdaQueryWrapper<JnOutsourceOrder> wrapper = new LambdaQueryWrapper<>();
        if (query.getOrderNo() != null && !query.getOrderNo().isEmpty()) {
            wrapper.like(JnOutsourceOrder::getOrderNo, query.getOrderNo());
        }
        if (query.getSupplierName() != null && !query.getSupplierName().isEmpty()) {
            wrapper.like(JnOutsourceOrder::getSupplierName, query.getSupplierName());
        }
        if (query.getProductName() != null && !query.getProductName().isEmpty()) {
            wrapper.like(JnOutsourceOrder::getProductName, query.getProductName());
        }
        if (query.getProcessType() != null && !query.getProcessType().isEmpty()) {
            wrapper.eq(JnOutsourceOrder::getProcessType, query.getProcessType());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(JnOutsourceOrder::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(JnOutsourceOrder::getCreateTime);
        return outsourceOrderMapper.selectList(wrapper);
    }

    @Override
    public JnOutsourceOrder selectById(Long id) {
        return outsourceOrderMapper.selectById(id);
    }

    @Override
    public JnOutsourceOrder selectWithLines(Long id) {
        JnOutsourceOrder order = outsourceOrderMapper.selectById(id);
        if (order != null) {
            List<JnOutsourceOrderLine> lines = outsourceOrderLineMapper.selectByOrderId(id);
            order.setParams(new HashMap<>());
            order.getParams().put("lines", lines);
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnOutsourceOrder order, List<JnOutsourceOrderLine> lines) {
        order.setOrderNo(generateOrderNo());
        if (order.getStatus() == null) {
            order.setStatus("DRAFT");
        }
        order.setCreateBy(SecurityUtils.getUsername());
        int result = outsourceOrderMapper.insert(order);

        if (lines != null && !lines.isEmpty()) {
            for (JnOutsourceOrderLine line : lines) {
                line.setOrderId(order.getOrderId());
                if (line.getReceivedQty() == null) {
                    line.setReceivedQty(0);
                }
                if (line.getSortOrder() == null) {
                    line.setSortOrder(0);
                }
                outsourceOrderLineMapper.insert(line);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnOutsourceOrder order, List<JnOutsourceOrderLine> lines) {
        order.setUpdateBy(SecurityUtils.getUsername());
        int result = outsourceOrderMapper.updateById(order);

        outsourceOrderLineMapper.deleteByOrderId(order.getOrderId());

        if (lines != null && !lines.isEmpty()) {
            for (JnOutsourceOrderLine line : lines) {
                line.setLineId(null);
                line.setOrderId(order.getOrderId());
                if (line.getReceivedQty() == null) {
                    line.setReceivedQty(0);
                }
                if (line.getSortOrder() == null) {
                    line.setSortOrder(0);
                }
                outsourceOrderLineMapper.insert(line);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] ids) {
        int count = 0;
        for (Long id : ids) {
            outsourceOrderLineMapper.deleteByOrderId(id);
            count += outsourceOrderMapper.deleteById(id);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(Long orderId) {
        JnOutsourceOrder order = outsourceOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("委外加工订单不存在: " + orderId);
        }
        order.setStatus("PENDING");
        order.setUpdateBy(SecurityUtils.getUsername());
        outsourceOrderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long orderId) {
        JnOutsourceOrder order = outsourceOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("委外加工订单不存在: " + orderId);
        }
        order.setStatus("APPROVED");
        order.setUpdateBy(SecurityUtils.getUsername());
        outsourceOrderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long orderId, String reason) {
        JnOutsourceOrder order = outsourceOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("委外加工订单不存在: " + orderId);
        }
        order.setStatus("CANCELLED");
        order.setRemark(reason);
        order.setUpdateBy(SecurityUtils.getUsername());
        outsourceOrderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(Long orderId) {
        JnOutsourceOrder order = outsourceOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("委外加工订单不存在: " + orderId);
        }
        order.setStatus("COMPLETED");
        order.setUpdateBy(SecurityUtils.getUsername());
        outsourceOrderMapper.updateById(order);
    }

    private String generateOrderNo() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "OS-" + datePart + "-";
        LambdaQueryWrapper<JnOutsourceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(JnOutsourceOrder::getOrderNo, prefix);
        wrapper.orderByDesc(JnOutsourceOrder::getOrderNo);
        wrapper.last("LIMIT 1");
        JnOutsourceOrder last = outsourceOrderMapper.selectOne(wrapper);
        int seq = 1;
        if (last != null && last.getOrderNo() != null) {
            String lastCode = last.getOrderNo();
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
