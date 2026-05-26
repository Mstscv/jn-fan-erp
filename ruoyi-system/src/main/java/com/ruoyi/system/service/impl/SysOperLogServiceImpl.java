package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.mapper.SysOperLogMapper;
import com.ruoyi.system.service.ISysOperLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements ISysOperLogService {

    @Override
    public List<SysOperLog> selectOperLogList(SysOperLog operLog) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        if (operLog.getTitle() != null && !operLog.getTitle().isEmpty()) {
            wrapper.like(SysOperLog::getTitle, operLog.getTitle());
        }
        if (operLog.getOperName() != null && !operLog.getOperName().isEmpty()) {
            wrapper.like(SysOperLog::getOperName, operLog.getOperName());
        }
        if (operLog.getStatus() != null && !operLog.getStatus().isEmpty()) {
            wrapper.eq(SysOperLog::getStatus, operLog.getStatus());
        }
        if (operLog.getBusinessType() != null) {
            wrapper.eq(SysOperLog::getBusinessType, operLog.getBusinessType());
        }
        wrapper.orderByDesc(SysOperLog::getOperTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public int insertOperLog(SysOperLog operLog) {
        return baseMapper.insert(operLog);
    }

    @Override
    @Transactional
    public int deleteOperLogByIds(Long[] ids) {
        int count = 0;
        for (Long id : ids) {
            count += baseMapper.deleteById(id);
        }
        return count;
    }

    @Override
    @Transactional
    public void cleanOperLog() {
        baseMapper.delete(new LambdaQueryWrapper<>());
    }
}
