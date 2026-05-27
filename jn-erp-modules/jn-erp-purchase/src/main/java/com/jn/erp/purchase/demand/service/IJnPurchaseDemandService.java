package com.jn.erp.purchase.demand.service;

import com.jn.erp.purchase.demand.domain.JnPurchaseDemand;

import java.util.List;

public interface IJnPurchaseDemandService {

    List<JnPurchaseDemand> selectList(JnPurchaseDemand query);

    JnPurchaseDemand getById(Long demandId);

    int insertDemand(JnPurchaseDemand demand);

    int updateDemand(JnPurchaseDemand demand);

    int deleteByIds(Long[] demandIds);

    int convertToPo(Long[] demandIds);

    String generateDemandNo();

    List<JnPurchaseDemand> runMrp();
}
