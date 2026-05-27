package com.jn.erp.purchase.supplier.service;

import com.jn.erp.purchase.supplier.domain.JnSupplier;

import java.util.List;

public interface IJnSupplierService {

    List<JnSupplier> selectList(JnSupplier supplier);

    JnSupplier getById(Long supplierId);

    int insert(JnSupplier supplier);

    int update(JnSupplier supplier);

    int deleteByIds(Long[] supplierIds);
}
