package com.jn.erp.production.outsource.service;

import com.jn.erp.production.outsource.domain.JnOutsourceDispatch;
import com.jn.erp.production.outsource.domain.JnOutsourceDispatchLine;

import java.util.List;

public interface IJnOutsourceDispatchService {

    List<JnOutsourceDispatch> selectList(JnOutsourceDispatch query);

    JnOutsourceDispatch selectById(Long id);

    JnOutsourceDispatch selectWithLines(Long id);

    int insert(JnOutsourceDispatch dispatch, List<JnOutsourceDispatchLine> lines);

    int update(JnOutsourceDispatch dispatch, List<JnOutsourceDispatchLine> lines);

    int deleteByIds(Long[] ids);

    void approve(Long dispatchId);

}
