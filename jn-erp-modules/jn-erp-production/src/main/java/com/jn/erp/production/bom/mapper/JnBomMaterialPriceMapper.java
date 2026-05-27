package com.jn.erp.production.bom.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.bom.domain.JnBomMaterialPrice;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface JnBomMaterialPriceMapper extends BaseMapperPlus<JnBomMaterialPrice> {

    BigDecimal selectUnitPriceByMaterialId(@Param("materialId") Long materialId);

    List<JnBomMaterialPrice> selectBatchUnitPrice(@Param("materialIds") List<Long> materialIds);
}
