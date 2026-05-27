package com.jn.erp.production.quality.service;

import com.jn.erp.production.quality.domain.JnInspectionStandard;

import java.util.List;

public interface IJnInspectionStandardService {

    List<JnInspectionStandard> selectList(JnInspectionStandard query);

    JnInspectionStandard selectById(Long id);

    int insert(JnInspectionStandard standard);

    int update(JnInspectionStandard standard);

    int deleteByIds(Long[] ids);

    List<JnInspectionStandard> selectByMaterialOrOperation(Long materialId, Long operationId, String inspectionType);
}
