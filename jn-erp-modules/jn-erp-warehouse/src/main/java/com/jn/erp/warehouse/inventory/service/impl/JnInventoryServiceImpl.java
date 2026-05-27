package com.jn.erp.warehouse.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.warehouse.inventory.domain.JnInventory;
import com.jn.erp.warehouse.inventory.mapper.JnInventoryMapper;
import com.jn.erp.warehouse.inventory.service.IJnInventoryService;
import com.jn.erp.warehouse.log.domain.JnInventoryLog;
import com.jn.erp.warehouse.log.mapper.JnInventoryLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JnInventoryServiceImpl extends ServiceImpl<JnInventoryMapper, JnInventory> implements IJnInventoryService {

    @Autowired
    private JnInventoryMapper inventoryMapper;

    @Autowired
    private JnInventoryLogMapper inventoryLogMapper;

    @Override
    public List<JnInventory> selectList(JnInventory query) {
        LambdaQueryWrapper<JnInventory> wrapper = Wrappers.lambdaQuery();
        if (query != null) {
            if (query.getMaterialId() != null) {
                wrapper.eq(JnInventory::getMaterialId, query.getMaterialId());
            }
            if (query.getWhId() != null) {
                wrapper.eq(JnInventory::getWhId, query.getWhId());
            }
            if (query.getMaterialCode() != null && !query.getMaterialCode().isEmpty()) {
                wrapper.like(JnInventory::getMaterialCode, query.getMaterialCode());
            }
            if (query.getMaterialName() != null && !query.getMaterialName().isEmpty()) {
                wrapper.like(JnInventory::getMaterialName, query.getMaterialName());
            }
            if (query.getBatchNo() != null && !query.getBatchNo().isEmpty()) {
                wrapper.eq(JnInventory::getBatchNo, query.getBatchNo());
            }
            if (query.getStatus() != null && !query.getStatus().isEmpty()) {
                wrapper.eq(JnInventory::getStatus, query.getStatus());
            }
        }
        wrapper.orderByDesc(JnInventory::getUpdateTime);
        return inventoryMapper.selectList(wrapper);
    }

    @Override
    public JnInventory getById(Long inventoryId) {
        return inventoryMapper.selectById(inventoryId);
    }

    @Override
    public JnInventory getInventory(Long materialId, Long whId, String batchNo) {
        return inventoryMapper.getInventory(materialId, whId, batchNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addStock(Long materialId, Long whId, Integer qty, String refNo, String refType, String user) {
        if (qty == null || qty <= 0) {
            throw new RuntimeException("入库数量必须大于0");
        }
        JnInventory inventory = inventoryMapper.getInventory(materialId, whId, null);
        int beforeQty = 0;
        if (inventory == null) {
            inventory = new JnInventory();
            inventory.setMaterialId(materialId);
            inventory.setWhId(whId);
            inventory.setQuantity(qty);
            inventory.setLockedQty(0);
            inventory.setAvailableQty(qty);
            inventory.setSafetyStock(0);
            inventory.setStatus("0");
            inventory.setUpdateTime(LocalDateTime.now());
            inventoryMapper.insert(inventory);
            beforeQty = 0;
        } else {
            beforeQty = inventory.getQuantity();
            int updated = inventoryMapper.addStock(materialId, whId, null, qty);
            if (updated == 0) {
                throw new RuntimeException("入库更新库存失败，物料ID: " + materialId + ", 仓库ID: " + whId);
            }
            inventory = inventoryMapper.getInventory(materialId, whId, null);
        }
        int afterQty = inventory.getQuantity();
        updateInventoryStatus(inventory);
        saveLog(materialId, inventory.getMaterialCode(), whId, refType, qty, beforeQty, afterQty, refNo, refType, user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void subtractStock(Long materialId, Long whId, Integer qty, String refNo, String refType, String user) {
        if (qty == null || qty <= 0) {
            throw new RuntimeException("出库数量必须大于0");
        }
        JnInventory inventory = inventoryMapper.getInventory(materialId, whId, null);
        if (inventory == null) {
            throw new RuntimeException("库存记录不存在，物料ID: " + materialId + ", 仓库ID: " + whId);
        }
        if (inventory.getAvailableQty() < qty) {
            throw new RuntimeException("可用库存不足，当前可用: " + inventory.getAvailableQty() + ", 需要: " + qty);
        }
        int beforeQty = inventory.getQuantity();
        int updated = inventoryMapper.subtractStock(materialId, whId, null, qty);
        if (updated == 0) {
            throw new RuntimeException("出库更新库存失败，物料ID: " + materialId + ", 仓库ID: " + whId);
        }
        inventory = inventoryMapper.getInventory(materialId, whId, null);
        int afterQty = inventory.getQuantity();
        updateInventoryStatus(inventory);
        saveLog(materialId, inventory.getMaterialCode(), whId, refType, -qty, beforeQty, afterQty, refNo, refType, user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferStock(Long fromWhId, Long toWhId, Long materialId, Integer qty, String user) {
        if (qty == null || qty <= 0) {
            throw new RuntimeException("调拨数量必须大于0");
        }
        JnInventory fromInventory = inventoryMapper.getInventory(materialId, fromWhId, null);
        if (fromInventory == null || fromInventory.getAvailableQty() < qty) {
            throw new RuntimeException("调出仓库可用库存不足");
        }
        int fromBeforeQty = fromInventory.getQuantity();
        int updated = inventoryMapper.subtractStock(materialId, fromWhId, null, qty);
        if (updated == 0) {
            throw new RuntimeException("调出扣减库存失败");
        }
        fromInventory = inventoryMapper.getInventory(materialId, fromWhId, null);
        updateInventoryStatus(fromInventory);
        saveLog(materialId, fromInventory.getMaterialCode(), fromWhId, "TRANSFER_OUT", -qty,
                fromBeforeQty, fromInventory.getQuantity(), null, "TRANSFER", user);

        JnInventory toInventory = inventoryMapper.getInventory(materialId, toWhId, null);
        int toBeforeQty = toInventory != null ? toInventory.getQuantity() : 0;
        if (toInventory == null) {
            toInventory = new JnInventory();
            toInventory.setMaterialId(materialId);
            toInventory.setWhId(toWhId);
            toInventory.setQuantity(qty);
            toInventory.setLockedQty(0);
            toInventory.setAvailableQty(qty);
            toInventory.setSafetyStock(0);
            toInventory.setStatus("0");
            toInventory.setUpdateTime(LocalDateTime.now());
            inventoryMapper.insert(toInventory);
        } else {
            inventoryMapper.addStock(materialId, toWhId, null, qty);
        }
        toInventory = inventoryMapper.getInventory(materialId, toWhId, null);
        updateInventoryStatus(toInventory);
        saveLog(materialId, toInventory.getMaterialCode(), toWhId, "TRANSFER_IN", qty,
                toBeforeQty, toInventory.getQuantity(), null, "TRANSFER", user);
    }

    @Override
    public List<JnInventory> checkSafetyStock() {
        List<JnInventory> list = inventoryMapper.selectBelowSafetyStockList();
        for (JnInventory inv : list) {
            if (inv.getAvailableQty() == null || inv.getAvailableQty() <= 0) {
                inv.setStatus("2");
            } else if (inv.getSafetyStock() != null && inv.getAvailableQty() < inv.getSafetyStock()) {
                inv.setStatus("1");
            }
        }
        return list;
    }

    private void updateInventoryStatus(JnInventory inventory) {
        String status = "0";
        if (inventory.getAvailableQty() == null || inventory.getAvailableQty() <= 0) {
            status = "2";
        } else if (inventory.getSafetyStock() != null && inventory.getAvailableQty() < inventory.getSafetyStock()) {
            status = "1";
        }
        inventory.setStatus(status);
        inventory.setUpdateTime(LocalDateTime.now());
        inventoryMapper.updateById(inventory);
    }

    private void saveLog(Long materialId, String materialCode, Long whId, String changeType,
                         Integer changeQty, Integer beforeQty, Integer afterQty,
                         String refNo, String refType, String user) {
        JnInventoryLog log = new JnInventoryLog();
        log.setMaterialId(materialId);
        log.setMaterialCode(materialCode);
        log.setWhId(whId);
        log.setChangeType(changeType);
        log.setChangeQty(changeQty);
        log.setBeforeQty(beforeQty);
        log.setAfterQty(afterQty);
        log.setRefNo(refNo);
        log.setRefType(refType);
        log.setCreateBy(user);
        log.setCreateTime(LocalDateTime.now());
        inventoryLogMapper.insert(log);
    }
}
