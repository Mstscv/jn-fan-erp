package com.jn.erp.production.defect.service;

import com.jn.erp.production.defect.domain.JnDefect;

import java.util.List;

public interface IJnDefectService {

    List<JnDefect> selectList(JnDefect query);

    JnDefect selectById(Long id);

    int insert(JnDefect defect);

    int update(JnDefect defect);

    int deleteByIds(Long[] ids);

    void processDefect(Long defectId, String disposition, String handler);
}
