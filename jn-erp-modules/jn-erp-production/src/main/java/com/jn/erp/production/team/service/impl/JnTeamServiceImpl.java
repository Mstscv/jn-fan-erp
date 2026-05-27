package com.jn.erp.production.team.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.team.domain.JnTeam;
import com.jn.erp.production.team.mapper.JnTeamMapper;
import com.jn.erp.production.team.service.IJnTeamService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class JnTeamServiceImpl extends ServiceImpl<JnTeamMapper, JnTeam> implements IJnTeamService {

    @Autowired
    private JnTeamMapper teamMapper;

    @Override
    public List<JnTeam> selectList(JnTeam query) {
        LambdaQueryWrapper<JnTeam> wrapper = new LambdaQueryWrapper<>();
        if (query.getTeamCode() != null && !query.getTeamCode().isEmpty()) {
            wrapper.like(JnTeam::getTeamCode, query.getTeamCode());
        }
        if (query.getTeamName() != null && !query.getTeamName().isEmpty()) {
            wrapper.like(JnTeam::getTeamName, query.getTeamName());
        }
        if (query.getLeader() != null && !query.getLeader().isEmpty()) {
            wrapper.like(JnTeam::getLeader, query.getLeader());
        }
        if (query.getIsActive() != null && !query.getIsActive().isEmpty()) {
            wrapper.eq(JnTeam::getIsActive, query.getIsActive());
        }
        wrapper.orderByDesc(JnTeam::getCreateTime);
        return teamMapper.selectList(wrapper);
    }

    @Override
    public JnTeam selectById(Long id) {
        return teamMapper.selectById(id);
    }

    @Override
    public int insert(JnTeam team) {
        team.setCreateBy(SecurityUtils.getUsername());
        return teamMapper.insert(team);
    }

    @Override
    public int update(JnTeam team) {
        team.setUpdateBy(SecurityUtils.getUsername());
        return teamMapper.updateById(team);
    }

    @Override
    public int deleteByIds(Long[] ids) {
        return teamMapper.deleteBatchIds(Arrays.asList(ids));
    }
}
