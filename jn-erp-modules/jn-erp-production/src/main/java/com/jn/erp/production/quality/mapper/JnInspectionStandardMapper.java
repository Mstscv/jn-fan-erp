package com.jn.erp.production.quality.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.quality.domain.JnInspectionStandard;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnInspectionStandardMapper extends BaseMapperPlus<JnInspectionStandard> {

    List<JnInspectionStandard> selectByMaterialOrOperation(@Param("materialId") Long materialId, @Param("operationId") Long operationId, @Param("inspectionType") String inspectionType);
}
