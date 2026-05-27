package com.jn.erp.warehouse.inventory.service;

import com.jn.erp.warehouse.inventory.domain.JnInventory;

import java.util.List;

public interface IJnInventoryService {

    List<JnInventory> selectList(JnInventory query);

    JnInventory getById(Long inventoryId);

    JnInventory getInventory(Long materialId, Long whId, String batchNo);

    void addStock(Long materialId, Long whId, Integer qty, String refNo, String refType, String user);

    void subtractStock(Long materialId, Long whId, Integer qty, String refNo, String refType, String user);

    void transferStock(Long fromWhId, Long toWhId, Long materialId, Integer qty, String user);

    List<JnInventory> checkSafetyStock();
}
