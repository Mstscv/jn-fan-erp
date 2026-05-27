package com.jn.erp.production.team.service;

import com.jn.erp.production.team.domain.JnTeam;

import java.util.List;

public interface IJnTeamService {

    List<JnTeam> selectList(JnTeam query);

    JnTeam selectById(Long id);

    int insert(JnTeam team);

    int update(JnTeam team);

    int deleteByIds(Long[] ids);
}
