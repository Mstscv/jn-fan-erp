package com.jn.erp.warehouse.outstock.service;

import com.jn.erp.warehouse.outstock.domain.JnOutStock;
import com.jn.erp.warehouse.outstock.domain.JnOutStockLine;

import java.util.List;

public interface IJnOutStockService {

    JnOutStock salesOut(Long deliveryId, String user);

    JnOutStock issueOut(Long woId, List<JnOutStockLine> materialList, String user);

    List<JnOutStock> selectList(JnOutStock query);

    JnOutStock getById(Long outStockId);
}
