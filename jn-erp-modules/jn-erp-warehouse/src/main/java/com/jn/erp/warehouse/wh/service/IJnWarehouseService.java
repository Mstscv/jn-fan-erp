package com.jn.erp.warehouse.wh.service;

import com.jn.erp.warehouse.wh.domain.JnWarehouse;

import java.util.List;

public interface IJnWarehouseService {

    List<JnWarehouse> selectList(JnWarehouse warehouse);

    JnWarehouse selectById(Long whId);

    int insert(JnWarehouse warehouse);

    int update(JnWarehouse warehouse);

    int deleteByIds(Long[] whIds);
}
