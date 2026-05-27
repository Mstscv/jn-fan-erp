package com.jn.erp.production.quality.service;

import com.jn.erp.production.quality.domain.JnInspection;
import com.jn.erp.production.quality.domain.JnInspectionLine;

import java.util.List;

public interface IJnInspectionService {

    List<JnInspection> selectList(JnInspection query);

    JnInspection selectById(Long id);

    JnInspection selectWithLines(Long id);

    int insert(JnInspection inspection, List<JnInspectionLine> lines);

    int update(JnInspection inspection, List<JnInspectionLine> lines);

    int deleteByIds(Long[] ids);

    void submit(Long inspectionId);

    void approve(Long inspectionId, String result);

    void reject(Long inspectionId, String defectDesc);
}
