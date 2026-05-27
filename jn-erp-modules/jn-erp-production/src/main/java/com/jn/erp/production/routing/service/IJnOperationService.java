package com.jn.erp.production.routing.service;

import com.jn.erp.production.routing.domain.JnOperation;

import java.util.List;

public interface IJnOperationService {

    List<JnOperation> selectList(JnOperation query);

    JnOperation selectById(Long id);

    int insert(JnOperation operation);

    int update(JnOperation operation);

    int deleteByIds(Long[] ids);
}
