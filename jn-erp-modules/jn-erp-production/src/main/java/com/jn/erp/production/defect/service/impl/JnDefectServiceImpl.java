package com.jn.erp.production.defect.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.defect.domain.JnDefect;
import com.jn.erp.production.defect.mapper.JnDefectMapper;
import com.jn.erp.production.defect.service.IJnDefectService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JnDefectServiceImpl extends ServiceImpl<JnDefectMapper, JnDefect> implements IJnDefectService {

    @Autowired
    private JnDefectMapper defectMapper;

    @Override
    public List<JnDefect> selectList(JnDefect query) {
        LambdaQueryWrapper<JnDefect> wrapper = new LambdaQueryWrapper<>();
        if (query.getOrderId() != null) {
            wrapper.eq(JnDefect::getOrderId, query.getOrderId());
        }
        if (query.getOrderNo() != null && !query.getOrderNo().isEmpty()) {
            wrapper.like(JnDefect::getOrderNo, query.getOrderNo());
        }
        if (query.getReportId() != null) {
            wrapper.eq(JnDefect::getReportId, query.getReportId());
        }
        if (query.getDefectType() != null && !query.getDefectType().isEmpty()) {
            wrapper.eq(JnDefect::getDefectType, query.getDefectType());
        }
        if (query.getSeverity() != null && !query.getSeverity().isEmpty()) {
            wrapper.eq(JnDefect::getSeverity, query.getSeverity());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(JnDefect::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(JnDefect::getCreateTime);
        return defectMapper.selectList(wrapper);
    }

    @Override
    public JnDefect selectById(Long id) {
        return defectMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnDefect defect) {
        if (defect.getSeverity() == null) {
            defect.setSeverity("MINOR");
        }
        if (defect.getDisposition() == null) {
            defect.setDisposition("REWORK");
        }
        if (defect.getStatus() == null) {
            defect.setStatus("OPEN");
        }
        defect.setCreateBy(SecurityUtils.getUsername());
        return defectMapper.insert(defect);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnDefect defect) {
        defect.setUpdateBy(SecurityUtils.getUsername());
        return defectMapper.updateById(defect);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] ids) {
        int count = 0;
        for (Long id : ids) {
            count += defectMapper.deleteById(id);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processDefect(Long defectId, String disposition, String handler) {
        JnDefect defect = defectMapper.selectById(defectId);
        if (defect == null) {
            throw new RuntimeException("不良品记录不存在: " + defectId);
        }
        defect.setDisposition(disposition);
        defect.setHandler(handler);
        defect.setHandleTime(LocalDateTime.now());
        defect.setStatus("CLOSED");
        defect.setUpdateBy(SecurityUtils.getUsername());
        defectMapper.updateById(defect);
    }
}
