package com.jn.erp.warehouse.wh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.warehouse.wh.domain.JnWarehouse;
import com.jn.erp.warehouse.wh.mapper.JnWarehouseMapper;
import com.jn.erp.warehouse.wh.service.IJnWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JnWarehouseServiceImpl extends ServiceImpl<JnWarehouseMapper, JnWarehouse> implements IJnWarehouseService {

    @Autowired
    private JnWarehouseMapper warehouseMapper;

    @Override
    public List<JnWarehouse> selectList(JnWarehouse warehouse) {
        LambdaQueryWrapper<JnWarehouse> wrapper = Wrappers.lambdaQuery();
        if (warehouse != null) {
            if (warehouse.getWhCode() != null && !warehouse.getWhCode().isEmpty()) {
                wrapper.like(JnWarehouse::getWhCode, warehouse.getWhCode());
            }
            if (warehouse.getWhName() != null && !warehouse.getWhName().isEmpty()) {
                wrapper.like(JnWarehouse::getWhName, warehouse.getWhName());
            }
            if (warehouse.getWhType() != null && !warehouse.getWhType().isEmpty()) {
                wrapper.eq(JnWarehouse::getWhType, warehouse.getWhType());
            }
            if (warehouse.getStatus() != null && !warehouse.getStatus().isEmpty()) {
                wrapper.eq(JnWarehouse::getStatus, warehouse.getStatus());
            }
        }
        wrapper.orderByAsc(JnWarehouse::getWhCode);
        return warehouseMapper.selectList(wrapper);
    }

    @Override
    public JnWarehouse selectById(Long whId) {
        return warehouseMapper.selectById(whId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnWarehouse warehouse) {
        if (warehouse.getWhType() == null) {
            warehouse.setWhType("0");
        }
        if (warehouse.getStatus() == null) {
            warehouse.setStatus("0");
        }
        return warehouseMapper.insert(warehouse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnWarehouse warehouse) {
        return warehouseMapper.updateById(warehouse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] whIds) {
        int count = 0;
        for (Long id : whIds) {
            count += warehouseMapper.deleteById(id);
        }
        return count;
    }
}
