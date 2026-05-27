package com.jn.erp.production.team.service;

import com.jn.erp.production.team.domain.JnWorker;

import java.util.List;

public interface IJnWorkerService {

    List<JnWorker> selectList(JnWorker query);

    JnWorker selectById(Long id);

    int insert(JnWorker worker);

    int update(JnWorker worker);

    int deleteByIds(Long[] ids);

    List<JnWorker> selectByTeamId(Long teamId);
}
