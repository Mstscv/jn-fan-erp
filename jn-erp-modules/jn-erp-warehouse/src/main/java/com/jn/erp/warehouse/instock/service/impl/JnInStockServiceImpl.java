package com.jn.erp.warehouse.instock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.common.core.utils.SequenceUtils;
import com.jn.erp.warehouse.instock.domain.JnInStock;
import com.jn.erp.warehouse.instock.domain.JnInStockLine;
import com.jn.erp.warehouse.instock.mapper.JnInStockLineMapper;
import com.jn.erp.warehouse.instock.mapper.JnInStockMapper;
import com.jn.erp.warehouse.instock.service.IJnInStockService;
import com.jn.erp.warehouse.inventory.service.IJnInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JnInStockServiceImpl extends ServiceImpl<JnInStockMapper, JnInStock> implements IJnInStockService {

    @Autowired
    private JnInStockMapper inStockMapper;

    @Autowired
    private JnInStockLineMapper inStockLineMapper;

    @Autowired
    private IJnInventoryService inventoryService;

    @Autowired
    private SequenceUtils sequenceUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JnInStock purchaseIn(Long poId, Long receiveId, String user) {
        JnInStock inStock = new JnInStock();
        inStock.setInStockNo(sequenceUtils.generate("IN_STOCK_NO"));
        inStock.setInStockType("PURCHASE_IN");
        inStock.setRefNo(String.valueOf(receiveId));
        inStock.setRefType("PURCHASE_RECEIVE");
        inStock.setStatus("0");
        inStock.setTotalQty(0);
        inStock.setCreateBy(user);
        inStockMapper.insert(inStock);
        return inStock;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JnInStock productionIn(Long woId, String user) {
        JnInStock inStock = new JnInStock();
        inStock.setInStockNo(sequenceUtils.generate("IN_STOCK_NO"));
        inStock.setInStockType("PRODUCTION_IN");
        inStock.setRefNo(String.valueOf(woId));
        inStock.setRefType("WORK_ORDER");
        inStock.setStatus("0");
        inStock.setTotalQty(0);
        inStock.setCreateBy(user);
        inStockMapper.insert(inStock);
        return inStock;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addLineAndUpdateStock(JnInStock inStock, JnInStockLine line, Long whId, String user) {
        line.setInStockId(inStock.getInStockId());
        inStockLineMapper.insert(line);

        inventoryService.addStock(line.getMaterialId(), whId, line.getQuantity(),
                inStock.getInStockNo(), inStock.getInStockType(), user);

        int total = inStock.getTotalQty() != null ? inStock.getTotalQty() : 0;
        inStock.setTotalQty(total + line.getQuantity());
        inStock.setWhId(whId);
        inStockMapper.updateById(inStock);
    }

    @Override
    public List<JnInStock> selectList(JnInStock query) {
        LambdaQueryWrapper<JnInStock> wrapper = Wrappers.lambdaQuery();
        if (query != null) {
            if (query.getInStockNo() != null && !query.getInStockNo().isEmpty()) {
                wrapper.like(JnInStock::getInStockNo, query.getInStockNo());
            }
            if (query.getInStockType() != null && !query.getInStockType().isEmpty()) {
                wrapper.eq(JnInStock::getInStockType, query.getInStockType());
            }
            if (query.getWhId() != null) {
                wrapper.eq(JnInStock::getWhId, query.getWhId());
            }
            if (query.getStatus() != null && !query.getStatus().isEmpty()) {
                wrapper.eq(JnInStock::getStatus, query.getStatus());
            }
        }
        wrapper.orderByDesc(JnInStock::getCreateTime);
        return inStockMapper.selectList(wrapper);
    }

    @Override
    public JnInStock getById(Long inStockId) {
        JnInStock inStock = inStockMapper.selectById(inStockId);
        if (inStock != null) {
            LambdaQueryWrapper<JnInStockLine> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(JnInStockLine::getInStockId, inStockId);
            inStock.setLines(inStockLineMapper.selectList(wrapper));
        }
        return inStock;
    }
}
