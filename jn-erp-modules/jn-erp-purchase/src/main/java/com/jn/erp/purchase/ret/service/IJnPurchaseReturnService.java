package com.jn.erp.purchase.ret.service;

import com.jn.erp.purchase.ret.domain.JnPurchaseReturn;

import java.util.List;

public interface IJnPurchaseReturnService {

    List<JnPurchaseReturn> selectList(JnPurchaseReturn ret);

    JnPurchaseReturn getById(Long returnId);

    int insert(JnPurchaseReturn ret);

    int update(JnPurchaseReturn ret);

    int deleteByIds(Long[] returnIds);

    int updateStatus(Long returnId, String status);
}
