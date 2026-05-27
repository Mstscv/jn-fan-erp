package com.jn.erp.finance.ar.service;

import com.jn.erp.finance.ar.domain.JnReceivable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IJnReceivableService {

    List<JnReceivable> selectList(JnReceivable query);

    JnReceivable selectById(Long receivableId);

    int insert(JnReceivable receivable);

    int update(JnReceivable receivable);

    int deleteByIds(Long[] receivableIds);

    void writeOff(Long receivableId, BigDecimal amount);

    void cancel(Long receivableId);

    void generateFromDelivery(Long deliveryId, String deliveryNo, Long customerId, String customerName, BigDecimal totalAmount);

    Map<String, Object> getAgingAnalysis(Long customerId);

}
