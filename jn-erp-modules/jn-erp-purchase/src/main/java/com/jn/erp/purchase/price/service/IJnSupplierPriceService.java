package com.jn.erp.purchase.price.service;

import com.jn.erp.purchase.price.domain.JnSupplierPrice;

import java.util.List;

public interface IJnSupplierPriceService {

    List<JnSupplierPrice> selectList(JnSupplierPrice price);

    JnSupplierPrice getById(Long priceId);

    int insert(JnSupplierPrice price);

    int update(JnSupplierPrice price);

    int deleteByIds(Long[] priceIds);

    JnSupplierPrice getBestPrice(Long supplierId, Long materialId);

    List<JnSupplierPrice> getPriceHistory(Long materialId, Long supplierId);
}
