package com.jn.erp.warehouse.outstock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.common.core.utils.SequenceUtils;
import com.jn.erp.warehouse.inventory.service.IJnInventoryService;
import com.jn.erp.warehouse.outstock.domain.JnOutStock;
import com.jn.erp.warehouse.outstock.domain.JnOutStockLine;
import com.jn.erp.warehouse.outstock.mapper.JnOutStockLineMapper;
import com.jn.erp.warehouse.outstock.mapper.JnOutStockMapper;
import com.jn.erp.warehouse.outstock.service.IJnOutStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JnOutStockServiceImpl extends ServiceImpl<JnOutStockMapper, JnOutStock> implements IJnOutStockService {

    @Autowired
    private JnOutStockMapper outStockMapper;

    @Autowired
    private JnOutStockLineMapper outStockLineMapper;

    @Autowired
    private IJnInventoryService inventoryService;

    @Autowired
    private SequenceUtils sequenceUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JnOutStock salesOut(Long deliveryId, String user) {
        JnOutStock outStock = new JnOutStock();
        outStock.setOutStockNo(sequenceUtils.generate("OUT_STOCK_NO"));
        outStock.setOutStockType("SALE_OUT");
        outStock.setRefNo(String.valueOf(deliveryId));
        outStock.setRefType("DELIVERY_NOTE");
        outStock.setStatus("0");
        outStock.setTotalQty(0);
        outStock.setCreateBy(user);
        outStockMapper.insert(outStock);
        return outStock;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JnOutStock issueOut(Long woId, List<JnOutStockLine> materialList, String user) {
        JnOutStock outStock = new JnOutStock();
        outStock.setOutStockNo(sequenceUtils.generate("OUT_STOCK_NO"));
        outStock.setOutStockType("ISSUE_OUT");
        outStock.setRefNo(String.valueOf(woId));
        outStock.setRefType("WORK_ORDER");
        outStock.setStatus("0");
        outStock.setTotalQty(0);
        outStock.setCreateBy(user);
        outStockMapper.insert(outStock);

        int totalQty = 0;
        for (JnOutStockLine line : materialList) {
            line.setOutStockId(outStock.getOutStockId());
            outStockLineMapper.insert(line);

            inventoryService.subtractStock(line.getMaterialId(), outStock.getWhId(), line.getQuantity(),
                    outStock.getOutStockNo(), outStock.getOutStockType(), user);
            totalQty += line.getQuantity();
        }
        outStock.setTotalQty(totalQty);
        outStockMapper.updateById(outStock);
        return outStock;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addLineAndUpdateStock(JnOutStock outStock, JnOutStockLine line, Long whId, String user) {
        line.setOutStockId(outStock.getOutStockId());
        outStockLineMapper.insert(line);

        inventoryService.subtractStock(line.getMaterialId(), whId, line.getQuantity(),
                outStock.getOutStockNo(), outStock.getOutStockType(), user);

        int total = outStock.getTotalQty() != null ? outStock.getTotalQty() : 0;
        outStock.setTotalQty(total + line.getQuantity());
        outStock.setWhId(whId);
        outStockMapper.updateById(outStock);
    }

    @Override
    public List<JnOutStock> selectList(JnOutStock query) {
        LambdaQueryWrapper<JnOutStock> wrapper = Wrappers.lambdaQuery();
        if (query != null) {
            if (query.getOutStockNo() != null && !query.getOutStockNo().isEmpty()) {
                wrapper.like(JnOutStock::getOutStockNo, query.getOutStockNo());
            }
            if (query.getOutStockType() != null && !query.getOutStockType().isEmpty()) {
                wrapper.eq(JnOutStock::getOutStockType, query.getOutStockType());
            }
            if (query.getWhId() != null) {
                wrapper.eq(JnOutStock::getWhId, query.getWhId());
            }
            if (query.getStatus() != null && !query.getStatus().isEmpty()) {
                wrapper.eq(JnOutStock::getStatus, query.getStatus());
            }
        }
        wrapper.orderByDesc(JnOutStock::getCreateTime);
        return outStockMapper.selectList(wrapper);
    }

    @Override
    public JnOutStock getById(Long outStockId) {
        JnOutStock outStock = outStockMapper.selectById(outStockId);
        if (outStock != null) {
            LambdaQueryWrapper<JnOutStockLine> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(JnOutStockLine::getOutStockId, outStockId);
            outStock.setLines(outStockLineMapper.selectList(wrapper));
        }
        return outStock;
    }
}
