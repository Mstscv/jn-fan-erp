package com.jn.erp.production.quality.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.quality.domain.JnInspectionLine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnInspectionLineMapper extends BaseMapperPlus<JnInspectionLine> {

    List<JnInspectionLine> selectByInspectionId(@Param("inspectionId") Long inspectionId);

    int deleteByInspectionId(@Param("inspectionId") Long inspectionId);
}
