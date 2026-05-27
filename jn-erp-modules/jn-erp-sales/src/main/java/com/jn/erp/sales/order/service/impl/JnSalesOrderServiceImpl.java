package com.jn.erp.sales.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jn.erp.common.core.utils.SequenceUtils;
import com.jn.erp.sales.order.domain.JnOrderLine;
import com.jn.erp.sales.order.domain.JnSalesOrder;
import com.jn.erp.sales.order.mapper.JnOrderLineMapper;
import com.jn.erp.sales.order.mapper.JnSalesOrderMapper;
import com.jn.erp.sales.order.service.IJnSalesOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class JnSalesOrderServiceImpl implements IJnSalesOrderService {

    @Autowired
    private JnSalesOrderMapper jnSalesOrderMapper;

    @Autowired
    private JnOrderLineMapper jnOrderLineMapper;

    @Autowired
    private SequenceUtils sequenceUtils;

    @Override
    public List<JnSalesOrder> selectList(JnSalesOrder query) {
        LambdaQueryWrapper<JnSalesOrder> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(JnSalesOrder::getDelFlag, "0");
        if (query != null) {
            if (query.getStatus() != null && !query.getStatus().isEmpty()) {
                wrapper.eq(JnSalesOrder::getStatus, query.getStatus());
            }
            if (query.getCustomerId() != null) {
                wrapper.eq(JnSalesOrder::getCustomerId, query.getCustomerId());
            }
            if (query.getCustomerName() != null && !query.getCustomerName().isEmpty()) {
                wrapper.like(JnSalesOrder::getCustomerName, query.getCustomerName());
            }
            if (query.getOrderNo() != null && !query.getOrderNo().isEmpty()) {
                wrapper.like(JnSalesOrder::getOrderNo, query.getOrderNo());
            }
            if (query.getSalesman() != null && !query.getSalesman().isEmpty()) {
                wrapper.like(JnSalesOrder::getSalesman, query.getSalesman());
            }
        }
        wrapper.orderByDesc(JnSalesOrder::getCreateTime);
        return jnSalesOrderMapper.selectList(wrapper);
    }

    @Override
    public JnSalesOrder getById(Long orderId) {
        return jnSalesOrderMapper.selectById(orderId);
    }

    @Override
    public List<JnOrderLine> getLinesByOrderId(Long orderId) {
        LambdaQueryWrapper<JnOrderLine> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(JnOrderLine::getOrderId, orderId);
        return jnOrderLineMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWithLines(JnSalesOrder order, List<JnOrderLine> lines) {
        order.setOrderNo(generateOrderNo());
        order.setDelFlag("0");
        if (order.getStatus() == null) {
            order.setStatus("PENDING");
        }
        if (order.getPaidAmount() == null) {
            order.setPaidAmount(BigDecimal.ZERO);
        }
        int result = jnSalesOrderMapper.insert(order);
        if (lines != null && !lines.isEmpty()) {
            for (JnOrderLine line : lines) {
                line.setOrderId(order.getOrderId());
                if (line.getQuantity() == null) {
                    line.setQuantity(1);
                }
                if (line.getShippedQty() == null) {
                    line.setShippedQty(0);
                }
            }
            jnOrderLineMapper.insert(lines);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnSalesOrder order) {
        jnOrderLineMapper.delete(Wrappers.lambdaQuery(JnOrderLine.class)
                .eq(JnOrderLine::getOrderId, order.getOrderId()));
        return jnSalesOrderMapper.updateById(order);
    }

    @Override
    public int updateStatus(Long orderId, String newStatus) {
        JnSalesOrder order = new JnSalesOrder();
        order.setOrderId(orderId);
        order.setStatus(newStatus);
        return jnSalesOrderMapper.updateById(order);
    }

    @Override
    public int deleteByIds(Long[] orderIds) {
        List<Long> ids = Arrays.asList(orderIds);
        LambdaQueryWrapper<JnSalesOrder> wrapper = Wrappers.lambdaQuery();
        wrapper.in(JnSalesOrder::getOrderId, ids);
        JnSalesOrder updateEntity = new JnSalesOrder();
        updateEntity.setDelFlag("1");
        return jnSalesOrderMapper.update(updateEntity, wrapper);
    }

    @Override
    public String generateOrderNo() {
        return sequenceUtils.generate("SALES_ORDER_NO");
    }
}
