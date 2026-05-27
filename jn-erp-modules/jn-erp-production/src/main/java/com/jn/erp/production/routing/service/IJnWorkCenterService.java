package com.jn.erp.production.routing.service;

import com.jn.erp.production.routing.domain.JnWorkCenter;

import java.util.List;

public interface IJnWorkCenterService {

    List<JnWorkCenter> selectList(JnWorkCenter query);

    JnWorkCenter selectById(Long id);

    int insert(JnWorkCenter center);

    int update(JnWorkCenter center);

    int deleteByIds(Long[] ids);
}
