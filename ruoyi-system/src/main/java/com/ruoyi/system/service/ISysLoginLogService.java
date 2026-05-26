package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysLoginLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ISysLoginLogService extends IService<SysLoginLog> {

    List<SysLoginLog> selectLoginLogList(SysLoginLog loginLog);

    int insertLoginLog(SysLoginLog loginLog);

    int deleteLoginLogByIds(Long[] ids);

    void cleanLoginLog();
}
