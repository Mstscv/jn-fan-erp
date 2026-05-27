package com.jn.erp.production.workorder.service;

import com.jn.erp.production.workorder.domain.JnWorkOrder;
import com.jn.erp.production.workorder.domain.JnWorkOrderLine;

import java.util.List;
import java.util.Map;

public interface IJnWorkOrderService {

    List<JnWorkOrder> selectList(JnWorkOrder query);

    JnWorkOrder selectById(Long id);

    JnWorkOrder selectWithLines(Long id);

    int insert(JnWorkOrder order, List<JnWorkOrderLine> lines);

    int update(JnWorkOrder order, List<JnWorkOrderLine> lines);

    int deleteByIds(Long[] ids);

    void updateStatus(Long orderId, String newStatus);

    List<JnWorkOrder> getPendingScheduling();

    Map<String, Object> getKanbanData();

}
