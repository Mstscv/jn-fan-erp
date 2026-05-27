package com.jn.erp.sales.order.service;

import com.jn.erp.sales.order.domain.JnOrderLine;
import com.jn.erp.sales.order.domain.JnSalesOrder;
import java.util.List;

public interface IJnSalesOrderService {

    List<JnSalesOrder> selectList(JnSalesOrder query);

    JnSalesOrder getById(Long orderId);

    List<JnOrderLine> getLinesByOrderId(Long orderId);

    int insertWithLines(JnSalesOrder order, List<JnOrderLine> lines);

    int update(JnSalesOrder order);

    int updateStatus(Long orderId, String newStatus);

    int deleteByIds(Long[] orderIds);

    String generateOrderNo();
}
