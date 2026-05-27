package com.jn.erp.sales.customer.service;

import com.jn.erp.sales.customer.domain.JnCustomer;

import java.util.List;

public interface IJnCustomerService {

    List<JnCustomer> selectList(JnCustomer customer);

    JnCustomer getById(Long customerId);

    int insert(JnCustomer customer);

    int update(JnCustomer customer);

    int deleteByIds(Long[] customerIds);
}
