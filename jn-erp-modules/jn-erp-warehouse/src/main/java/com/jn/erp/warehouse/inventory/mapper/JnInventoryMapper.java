package com.jn.erp.warehouse.inventory.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.warehouse.inventory.domain.JnInventory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnInventoryMapper extends BaseMapperPlus<JnInventory> {

    JnInventory getInventory(@Param("materialId") Long materialId, @Param("whId") Long whId, @Param("batchNo") String batchNo);

    int addStock(@Param("materialId") Long materialId, @Param("whId") Long whId,
                 @Param("batchNo") String batchNo, @Param("qty") Integer qty);

    int subtractStock(@Param("materialId") Long materialId, @Param("whId") Long whId,
                      @Param("batchNo") String batchNo, @Param("qty") Integer qty);

    List<JnInventory> selectBelowSafetyStockList();
}
