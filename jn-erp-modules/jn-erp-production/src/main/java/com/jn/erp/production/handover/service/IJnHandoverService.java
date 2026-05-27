package com.jn.erp.production.handover.service;

import com.jn.erp.production.handover.domain.JnHandover;

import java.util.List;

public interface IJnHandoverService {

    List<JnHandover> selectList(JnHandover query);

    JnHandover selectById(Long id);

    List<JnHandover> selectByOrderId(Long orderId);

    int insert(JnHandover handover);

    int update(JnHandover handover);

    int deleteByIds(Long[] ids);

    void confirm(Long handoverId, Integer actualQty, Integer defectQty);

    void reject(Long handoverId, String reason);
}
