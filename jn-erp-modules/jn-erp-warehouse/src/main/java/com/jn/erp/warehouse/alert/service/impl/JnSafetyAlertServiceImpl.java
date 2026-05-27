package com.jn.erp.warehouse.alert.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jn.erp.warehouse.alert.service.IJnSafetyAlertService;
import com.jn.erp.warehouse.inventory.domain.JnInventory;
import com.jn.erp.warehouse.inventory.mapper.JnInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JnSafetyAlertServiceImpl implements IJnSafetyAlertService {

    @Autowired
    private JnInventoryMapper inventoryMapper;

    @Override
    public List<Map<String, Object>> getAlerts() {
        LambdaQueryWrapper<JnInventory> wrapper = Wrappers.lambdaQuery();
        wrapper.lt(JnInventory::getAvailableQty, JnInventory::getSafetyStock);
        wrapper.gt(JnInventory::getSafetyStock, 0);
        List<JnInventory> list = inventoryMapper.selectList(wrapper);
        return buildAlertList(list);
    }

    @Override
    public List<Map<String, Object>> getAlertsByWarehouse(Long whId) {
        LambdaQueryWrapper<JnInventory> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(JnInventory::getWhId, whId);
        wrapper.lt(JnInventory::getAvailableQty, JnInventory::getSafetyStock);
        wrapper.gt(JnInventory::getSafetyStock, 0);
        List<JnInventory> list = inventoryMapper.selectList(wrapper);
        return buildAlertList(list);
    }

    @Override
    public List<Map<String, Object>> getCriticalAlerts() {
        LambdaQueryWrapper<JnInventory> wrapper = Wrappers.lambdaQuery();
        wrapper.lt(JnInventory::getAvailableQty, JnInventory::getSafetyStock);
        wrapper.gt(JnInventory::getSafetyStock, 0);
        wrapper.apply("available_qty <= 0");
        List<JnInventory> list = inventoryMapper.selectList(wrapper);
        return buildAlertList(list);
    }

    private List<Map<String, Object>> buildAlertList(List<JnInventory> inventoryList) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (JnInventory inv : inventoryList) {
            Map<String, Object> item = new HashMap<>();
            item.put("inventoryId", inv.getInventoryId());
            item.put("whId", inv.getWhId());
            item.put("materialId", inv.getMaterialId());
            item.put("materialCode", inv.getMaterialCode());
            item.put("materialName", inv.getMaterialName());
            item.put("spec", inv.getSpec());
            item.put("batchNo", inv.getBatchNo());
            item.put("quantity", inv.getQuantity());
            item.put("availableQty", inv.getAvailableQty());
            item.put("lockedQty", inv.getLockedQty());
            item.put("safetyStock", inv.getSafetyStock());
            item.put("shortageQty", inv.getSafetyStock() - inv.getAvailableQty());
            result.add(item);
        }
        return result;
    }
}
