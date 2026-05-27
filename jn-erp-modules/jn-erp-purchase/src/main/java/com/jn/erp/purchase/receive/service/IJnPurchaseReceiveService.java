package com.jn.erp.purchase.receive.service;

import com.jn.erp.purchase.receive.domain.JnPurchaseReceive;

import java.util.List;

public interface IJnPurchaseReceiveService {

    List<JnPurchaseReceive> selectList(JnPurchaseReceive receive);

    JnPurchaseReceive getById(Long receiveId);

    int insert(JnPurchaseReceive receive);

    int update(JnPurchaseReceive receive);

    int deleteByIds(Long[] receiveIds);

    int updateQcResult(Long receiveId, String qcResult);

    JnPurchaseReceive createFromPo(Long poId, Integer totalQty, Integer okQty, Integer badQty, String remark);
}
