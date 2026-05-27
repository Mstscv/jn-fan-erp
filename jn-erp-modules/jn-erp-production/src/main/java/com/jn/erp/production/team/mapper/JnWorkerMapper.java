package com.jn.erp.production.team.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.team.domain.JnWorker;

import java.util.List;

public interface JnWorkerMapper extends BaseMapperPlus<JnWorker> {

    List<JnWorker> selectByTeamId(Long teamId);
}
