package com.jn.erp.production.outsource.service;

import com.jn.erp.production.outsource.domain.JnOutsourceOrder;
import com.jn.erp.production.outsource.domain.JnOutsourceOrderLine;

import java.util.List;

public interface IJnOutsourceOrderService {

    List<JnOutsourceOrder> selectList(JnOutsourceOrder query);

    JnOutsourceOrder selectById(Long id);

    JnOutsourceOrder selectWithLines(Long id);

    int insert(JnOutsourceOrder order, List<JnOutsourceOrderLine> lines);

    int update(JnOutsourceOrder order, List<JnOutsourceOrderLine> lines);

    int deleteByIds(Long[] ids);

    void submit(Long orderId);

    void approve(Long orderId);

    void reject(Long orderId, String reason);

    void complete(Long orderId);

}
