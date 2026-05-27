package com.jn.erp.production.equipment.service;

import com.jn.erp.production.equipment.domain.JnEquipment;

import java.util.List;

public interface IJnEquipmentService {

    List<JnEquipment> selectList(JnEquipment query);

    JnEquipment selectById(Long id);

    int insert(JnEquipment equipment);

    int update(JnEquipment equipment);

    int deleteByIds(Long[] ids);
}
