package com.jn.erp.warehouse.transfer.service.impl;

import com.jn.erp.warehouse.inventory.service.IJnInventoryService;
import com.jn.erp.warehouse.transfer.service.IJnTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JnTransferServiceImpl implements IJnTransferService {

    @Autowired
    private IJnInventoryService inventoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transfer(Long fromWhId, Long toWhId, Long materialId, String materialCode, Integer qty, String user) {
        if (qty == null || qty <= 0) {
            throw new RuntimeException("调拨数量必须大于0");
        }
        if (fromWhId.equals(toWhId)) {
            throw new RuntimeException("调出仓库和调入仓库不能相同");
        }
        inventoryService.transferStock(fromWhId, toWhId, materialId, qty, user);
    }
}
