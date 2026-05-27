package com.jn.erp.finance.ap.service;

import com.jn.erp.finance.ap.domain.JnPayable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IJnPayableService {

    List<JnPayable> selectList(JnPayable query);

    JnPayable selectById(Long payableId);

    int insert(JnPayable payable);

    int update(JnPayable payable);

    int deleteByIds(Long[] payableIds);

    void pay(Long payableId, BigDecimal amount);

    void cancel(Long payableId);

    void generateFromPurchase(Long purchaseId, String purchaseNo, Long supplierId, String supplierName, BigDecimal totalAmount);

    Map<String, Object> getAgingAnalysis(Long supplierId);

}
