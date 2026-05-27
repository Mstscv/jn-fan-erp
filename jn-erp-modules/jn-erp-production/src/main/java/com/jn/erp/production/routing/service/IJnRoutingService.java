package com.jn.erp.production.routing.service;

import com.jn.erp.production.routing.domain.JnRouting;
import com.jn.erp.production.routing.domain.JnRoutingLine;

import java.util.List;

public interface IJnRoutingService {

    List<JnRouting> selectList(JnRouting query);

    JnRouting selectById(Long id);

    JnRouting selectWithLines(Long id);

    int insert(JnRouting routing, List<JnRoutingLine> lines);

    int update(JnRouting routing, List<JnRoutingLine> lines);

    int deleteByIds(Long[] ids);

    void updateStatus(Long routingId, String newStatus);
}
