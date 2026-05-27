package com.jn.erp.production.bom.service;

import com.jn.erp.production.bom.domain.JnBomLine;

import java.util.List;

public interface IJnBomLineService {

    List<JnBomLine> selectByBomId(Long bomId);

    JnBomLine selectById(Long lineId);

    int insert(JnBomLine line);

    int update(JnBomLine line);

    int deleteByIds(Long[] lineIds);

    int deleteByBomId(Long bomId);

    int batchInsert(List<JnBomLine> lines);
}
