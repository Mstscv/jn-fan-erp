package com.jn.erp.sales.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jn.erp.common.core.utils.SequenceUtils;
import com.jn.erp.sales.customer.domain.JnCustomer;
import com.jn.erp.sales.customer.mapper.JnCustomerMapper;
import com.jn.erp.sales.customer.service.IJnCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class JnCustomerServiceImpl implements IJnCustomerService {

    @Autowired
    private JnCustomerMapper jnCustomerMapper;

    @Autowired
    private SequenceUtils sequenceUtils;

    @Override
    public List<JnCustomer> selectList(JnCustomer customer) {
        LambdaQueryWrapper<JnCustomer> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(JnCustomer::getDelFlag, "0");
        if (customer != null) {
            if (customer.getCustomerCode() != null && !customer.getCustomerCode().isEmpty()) {
                wrapper.like(JnCustomer::getCustomerCode, customer.getCustomerCode());
            }
            if (customer.getCustomerName() != null && !customer.getCustomerName().isEmpty()) {
                wrapper.like(JnCustomer::getCustomerName, customer.getCustomerName());
            }
            if (customer.getContact() != null && !customer.getContact().isEmpty()) {
                wrapper.like(JnCustomer::getContact, customer.getContact());
            }
            if (customer.getPhone() != null && !customer.getPhone().isEmpty()) {
                wrapper.like(JnCustomer::getPhone, customer.getPhone());
            }
            if (customer.getRegion() != null && !customer.getRegion().isEmpty()) {
                wrapper.like(JnCustomer::getRegion, customer.getRegion());
            }
            if (customer.getLevel() != null && !customer.getLevel().isEmpty()) {
                wrapper.eq(JnCustomer::getLevel, customer.getLevel());
            }
            if (customer.getStatus() != null && !customer.getStatus().isEmpty()) {
                wrapper.eq(JnCustomer::getStatus, customer.getStatus());
            }
        }
        wrapper.orderByDesc(JnCustomer::getCreateTime);
        return jnCustomerMapper.selectList(wrapper);
    }

    @Override
    public JnCustomer getById(Long customerId) {
        return jnCustomerMapper.selectById(customerId);
    }

    @Override
    public int insert(JnCustomer customer) {
        customer.setCustomerCode(generateCustomerCode());
        customer.setDelFlag("0");
        if (customer.getLevel() == null) {
            customer.setLevel("3");
        }
        if (customer.getStatus() == null) {
            customer.setStatus("0");
        }
        return jnCustomerMapper.insert(customer);
    }

    @Override
    public int update(JnCustomer customer) {
        return jnCustomerMapper.updateById(customer);
    }

    @Override
    public int deleteByIds(Long[] customerIds) {
        List<Long> ids = Arrays.asList(customerIds);
        LambdaQueryWrapper<JnCustomer> wrapper = Wrappers.lambdaQuery();
        wrapper.in(JnCustomer::getCustomerId, ids);
        JnCustomer updateEntity = new JnCustomer();
        updateEntity.setDelFlag("1");
        return jnCustomerMapper.update(updateEntity, wrapper);
    }

    private String generateCustomerCode() {
        return sequenceUtils.generate("CUSTOMER_CODE");
    }
}
