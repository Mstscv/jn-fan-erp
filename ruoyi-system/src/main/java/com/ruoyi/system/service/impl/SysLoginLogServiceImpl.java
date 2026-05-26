package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysLoginLog;
import com.ruoyi.system.mapper.SysLoginLogMapper;
import com.ruoyi.system.service.ISysLoginLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements ISysLoginLogService {

    @Override
    public List<SysLoginLog> selectLoginLogList(SysLoginLog loginLog) {
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        if (loginLog.getUserName() != null && !loginLog.getUserName().isEmpty()) {
            wrapper.like(SysLoginLog::getUserName, loginLog.getUserName());
        }
        if (loginLog.getIpaddr() != null && !loginLog.getIpaddr().isEmpty()) {
            wrapper.like(SysLoginLog::getIpaddr, loginLog.getIpaddr());
        }
        if (loginLog.getStatus() != null && !loginLog.getStatus().isEmpty()) {
            wrapper.eq(SysLoginLog::getStatus, loginLog.getStatus());
        }
        wrapper.orderByDesc(SysLoginLog::getLoginTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public int insertLoginLog(SysLoginLog loginLog) {
        return baseMapper.insert(loginLog);
    }

    @Override
    @Transactional
    public int deleteLoginLogByIds(Long[] ids) {
        int count = 0;
        for (Long id : ids) {
            count += baseMapper.deleteById(id);
        }
        return count;
    }

    @Override
    @Transactional
    public void cleanLoginLog() {
        baseMapper.delete(new LambdaQueryWrapper<>());
    }
}
