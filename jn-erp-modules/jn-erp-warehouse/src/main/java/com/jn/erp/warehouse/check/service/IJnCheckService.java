package com.jn.erp.warehouse.check.service;

import com.jn.erp.warehouse.check.domain.JnInventoryCheck;

import java.util.List;

public interface IJnCheckService {

    List<JnInventoryCheck> selectList(JnInventoryCheck check);

    JnInventoryCheck getById(Long checkId);

    JnInventoryCheck createCheck(JnInventoryCheck check, List<com.jn.erp.warehouse.check.domain.JnCheckLine> lines);

    JnInventoryCheck updateCheck(JnInventoryCheck check, List<com.jn.erp.warehouse.check.domain.JnCheckLine> lines);

    int deleteByIds(Long[] checkIds);

    void approve(Long checkId, String user);
}
