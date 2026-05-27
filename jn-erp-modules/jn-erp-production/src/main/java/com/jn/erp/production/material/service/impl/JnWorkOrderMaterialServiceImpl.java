package com.jn.erp.production.material.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.bom.domain.JnBomLine;
import com.jn.erp.production.bom.mapper.JnBomLineMapper;
import com.jn.erp.production.material.domain.JnWorkOrderMaterial;
import com.jn.erp.production.material.mapper.JnWorkOrderMaterialMapper;
import com.jn.erp.production.material.service.IJnWorkOrderMaterialService;
import com.jn.erp.production.workorder.domain.JnWorkOrder;
import com.jn.erp.production.workorder.mapper.JnWorkOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class JnWorkOrderMaterialServiceImpl extends ServiceImpl<JnWorkOrderMaterialMapper, JnWorkOrderMaterial> implements IJnWorkOrderMaterialService {

    @Autowired
    private JnWorkOrderMaterialMapper materialMapper;

    @Autowired
    private JnWorkOrderMapper workOrderMapper;

    @Autowired
    private JnBomLineMapper bomLineMapper;

    @Override
    public List<JnWorkOrderMaterial> selectByOrderId(Long orderId) {
        List<JnWorkOrderMaterial> list = materialMapper.selectByOrderId(orderId);
        for (JnWorkOrderMaterial item : list) {
            BigDecimal shortage = item.getRequiredQty().subtract(item.getAllocatedQty());
            if (shortage.compareTo(BigDecimal.ZERO) < 0) {
                shortage = BigDecimal.ZERO;
            }
            item.setShortageQty(shortage);
            item.setAvailableStock(BigDecimal.ZERO);
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<JnWorkOrderMaterial> explodeBom(Long orderId) {
        JnWorkOrder order = workOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("工单不存在: " + orderId);
        }

        Long bomId = order.getBomId();
        if (bomId == null) {
            throw new RuntimeException("工单未关联BOM: " + orderId);
        }

        materialMapper.deleteByOrderId(orderId);

        List<JnBomLine> bomLines = bomLineMapper.selectByBomId(bomId);
        if (bomLines == null || bomLines.isEmpty()) {
            throw new RuntimeException("BOM无物料行: " + bomId);
        }

        for (JnBomLine bomLine : bomLines) {
            if ("Y".equals(bomLine.getIsOptional())) {
                continue;
            }

            JnWorkOrderMaterial material = new JnWorkOrderMaterial();
            material.setOrderId(orderId);
            material.setOrderNo(order.getOrderNo());
            material.setBomId(bomId);
            material.setMaterialId(bomLine.getMaterialId());
            material.setMaterialCode(bomLine.getMaterialCode());
            material.setMaterialName(bomLine.getMaterialName());
            material.setSpec(bomLine.getSpec());
            material.setUnit(bomLine.getUnit());

            BigDecimal bomQty = bomLine.getQuantity() != null ? bomLine.getQuantity() : BigDecimal.ONE;
            BigDecimal workQty = BigDecimal.valueOf(order.getQuantity() != null ? order.getQuantity() : 1);
            BigDecimal requiredQty = bomQty.multiply(workQty).setScale(2, RoundingMode.HALF_UP);
            material.setRequiredQty(requiredQty);

            if (material.getAllocatedQty() == null) {
                material.setAllocatedQty(BigDecimal.ZERO);
            }
            if (material.getPickedQty() == null) {
                material.setPickedQty(BigDecimal.ZERO);
            }
            material.setSourceType("BOM_EXPLOSION");
            material.setStatus("PENDING");
            material.setAvailableStock(BigDecimal.ZERO);

            BigDecimal shortage = requiredQty.subtract(material.getAllocatedQty());
            if (shortage.compareTo(BigDecimal.ZERO) < 0) {
                shortage = BigDecimal.ZERO;
            }
            material.setShortageQty(shortage);

            materialMapper.insert(material);
        }

        return materialMapper.selectByOrderId(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int allocateMaterial(Long demandId, BigDecimal qty) {
        JnWorkOrderMaterial material = materialMapper.selectById(demandId);
        if (material == null) {
            throw new RuntimeException("物料需求不存在: " + demandId);
        }

        BigDecimal newAllocated = material.getAllocatedQty().add(qty);
        if (newAllocated.compareTo(material.getRequiredQty()) > 0) {
            throw new RuntimeException("分配数量超出需求量");
        }

        material.setAllocatedQty(newAllocated);

        if (newAllocated.compareTo(material.getRequiredQty()) >= 0) {
            material.setStatus("ALLOCATED");
        } else if (newAllocated.compareTo(BigDecimal.ZERO) > 0) {
            material.setStatus("PARTIAL");
        }

        return materialMapper.updateById(material);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAllocate(Long orderId) {
        List<JnWorkOrderMaterial> list = materialMapper.selectByOrderId(orderId);
        int count = 0;
        for (JnWorkOrderMaterial item : list) {
            if (!"PENDING".equals(item.getStatus()) && !"PARTIAL".equals(item.getStatus())) {
                continue;
            }

            BigDecimal shortage = item.getRequiredQty().subtract(item.getAllocatedQty());
            if (shortage.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            item.setAllocatedQty(item.getRequiredQty());
            item.setStatus("ALLOCATED");
            count += materialMapper.updateById(item);
        }
        return count;
    }

}
