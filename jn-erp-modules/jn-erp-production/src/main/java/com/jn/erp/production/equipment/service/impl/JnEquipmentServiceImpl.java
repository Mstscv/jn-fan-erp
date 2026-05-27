package com.jn.erp.production.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.equipment.domain.JnEquipment;
import com.jn.erp.production.equipment.mapper.JnEquipmentMapper;
import com.jn.erp.production.equipment.service.IJnEquipmentService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class JnEquipmentServiceImpl extends ServiceImpl<JnEquipmentMapper, JnEquipment> implements IJnEquipmentService {

    @Autowired
    private JnEquipmentMapper equipmentMapper;

    @Override
    public List<JnEquipment> selectList(JnEquipment query) {
        LambdaQueryWrapper<JnEquipment> wrapper = new LambdaQueryWrapper<>();
        if (query.getEquipmentCode() != null && !query.getEquipmentCode().isEmpty()) {
            wrapper.like(JnEquipment::getEquipmentCode, query.getEquipmentCode());
        }
        if (query.getEquipmentName() != null && !query.getEquipmentName().isEmpty()) {
            wrapper.like(JnEquipment::getEquipmentName, query.getEquipmentName());
        }
        if (query.getEquipmentType() != null && !query.getEquipmentType().isEmpty()) {
            wrapper.eq(JnEquipment::getEquipmentType, query.getEquipmentType());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(JnEquipment::getStatus, query.getStatus());
        }
        if (query.getWorkCenterId() != null) {
            wrapper.eq(JnEquipment::getWorkCenterId, query.getWorkCenterId());
        }
        if (query.getIsActive() != null && !query.getIsActive().isEmpty()) {
            wrapper.eq(JnEquipment::getIsActive, query.getIsActive());
        }
        wrapper.orderByDesc(JnEquipment::getCreateTime);
        return equipmentMapper.selectList(wrapper);
    }

    @Override
    public JnEquipment selectById(Long id) {
        return equipmentMapper.selectById(id);
    }

    @Override
    public int insert(JnEquipment equipment) {
        equipment.setCreateBy(SecurityUtils.getUsername());
        return equipmentMapper.insert(equipment);
    }

    @Override
    public int update(JnEquipment equipment) {
        equipment.setUpdateBy(SecurityUtils.getUsername());
        return equipmentMapper.updateById(equipment);
    }

    @Override
    public int deleteByIds(Long[] ids) {
        return equipmentMapper.deleteBatchIds(Arrays.asList(ids));
    }
}
