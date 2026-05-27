package com.jn.erp.production.picking.service;

import com.jn.erp.production.picking.domain.JnPicking;
import com.jn.erp.production.picking.domain.JnPickingLine;

import java.util.List;

public interface IJnPickingService {

    List<JnPicking> selectList(JnPicking query);

    JnPicking selectById(Long id);

    JnPicking selectWithLines(Long id);

    int insert(JnPicking picking, List<JnPickingLine> lines);

    int update(JnPicking picking, List<JnPickingLine> lines);

    int deleteByIds(Long[] ids);

    void approve(Long pickingId);

    void reject(Long pickingId);
}
