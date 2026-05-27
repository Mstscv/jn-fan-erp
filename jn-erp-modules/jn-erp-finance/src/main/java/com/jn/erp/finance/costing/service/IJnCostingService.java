package com.jn.erp.finance.costing.service;

import com.jn.erp.finance.costing.domain.JnWorkOrderCost;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IJnCostingService {

    int calculateWorkOrderCost(Long orderId);

    int batchCalculateWorkOrderCosts(List<Long> orderIds);

    int calculateProductCost(Long productId);

    List<Map<String, Object>> getProductCostSummary(Long productId, LocalDate startDate, LocalDate endDate);

    JnWorkOrderCost selectById(Long id);

    List<JnWorkOrderCost> selectList(JnWorkOrderCost query);

    int approve(Long costId);

    int deleteByIds(Long[] ids);
}
