package com.jn.erp.production.defect.service;

import com.jn.erp.production.defect.domain.JnReworkOrder;

import java.util.List;

public interface IJnReworkOrderService {

    List<JnReworkOrder> selectList(JnReworkOrder query);

    JnReworkOrder selectById(Long id);

    int insert(JnReworkOrder rework);

    int update(JnReworkOrder rework);

    int deleteByIds(Long[] ids);

    JnReworkOrder createFromDefect(Long defectId);

    void start(Long reworkId);

    void complete(Long reworkId, Integer actualQty);

    void cancel(Long reworkId, String reason);
}
