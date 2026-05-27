package com.jn.erp.warehouse.instock.service;

import com.jn.erp.warehouse.instock.domain.JnInStock;

import java.util.List;

public interface IJnInStockService {

    JnInStock purchaseIn(Long poId, Long receiveId, String user);

    JnInStock productionIn(Long woId, String user);

    List<JnInStock> selectList(JnInStock query);

    JnInStock getById(Long inStockId);
}
