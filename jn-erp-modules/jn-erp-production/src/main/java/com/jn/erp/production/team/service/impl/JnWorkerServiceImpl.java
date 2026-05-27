package com.jn.erp.production.team.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.team.domain.JnWorker;
import com.jn.erp.production.team.mapper.JnWorkerMapper;
import com.jn.erp.production.team.service.IJnWorkerService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class JnWorkerServiceImpl extends ServiceImpl<JnWorkerMapper, JnWorker> implements IJnWorkerService {

    @Autowired
    private JnWorkerMapper workerMapper;

    @Override
    public List<JnWorker> selectList(JnWorker query) {
        LambdaQueryWrapper<JnWorker> wrapper = new LambdaQueryWrapper<>();
        if (query.getWorkerCode() != null && !query.getWorkerCode().isEmpty()) {
            wrapper.like(JnWorker::getWorkerCode, query.getWorkerCode());
        }
        if (query.getWorkerName() != null && !query.getWorkerName().isEmpty()) {
            wrapper.like(JnWorker::getWorkerName, query.getWorkerName());
        }
        if (query.getTeamId() != null) {
            wrapper.eq(JnWorker::getTeamId, query.getTeamId());
        }
        if (query.getPosition() != null && !query.getPosition().isEmpty()) {
            wrapper.eq(JnWorker::getPosition, query.getPosition());
        }
        if (query.getIsActive() != null && !query.getIsActive().isEmpty()) {
            wrapper.eq(JnWorker::getIsActive, query.getIsActive());
        }
        wrapper.orderByDesc(JnWorker::getCreateTime);
        return workerMapper.selectList(wrapper);
    }

    @Override
    public JnWorker selectById(Long id) {
        return workerMapper.selectById(id);
    }

    @Override
    public int insert(JnWorker worker) {
        worker.setCreateBy(SecurityUtils.getUsername());
        return workerMapper.insert(worker);
    }

    @Override
    public int update(JnWorker worker) {
        worker.setUpdateBy(SecurityUtils.getUsername());
        return workerMapper.updateById(worker);
    }

    @Override
    public int deleteByIds(Long[] ids) {
        return workerMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public List<JnWorker> selectByTeamId(Long teamId) {
        LambdaQueryWrapper<JnWorker> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JnWorker::getTeamId, teamId);
        wrapper.orderByDesc(JnWorker::getCreateTime);
        return workerMapper.selectList(wrapper);
    }
}
