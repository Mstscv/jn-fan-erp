package com.jn.erp.purchase.po.service;

import com.jn.erp.purchase.po.domain.JnPoLine;
import com.jn.erp.purchase.po.domain.JnPurchaseOrder;

import java.util.List;

public interface IJnPurchaseOrderService {

    List<JnPurchaseOrder> selectList(JnPurchaseOrder order);

    JnPurchaseOrder getById(Long poId);

    JnPurchaseOrder getByIdWithLines(Long poId);

    int insertWithLines(JnPurchaseOrder order, List<JnPoLine> lines);

    int updateWithLines(JnPurchaseOrder order, List<JnPoLine> lines);

    int deleteByIds(Long[] poIds);

    int updateStatus(Long poId, String newStatus);

    String generatePoNo();

    int approve(Long poId, String username);

    int reject(Long poId, String username);
}
