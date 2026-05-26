package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysOperLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ISysOperLogService extends IService<SysOperLog> {

    List<SysOperLog> selectOperLogList(SysOperLog operLog);

    int insertOperLog(SysOperLog operLog);

    int deleteOperLogByIds(Long[] ids);

    void cleanOperLog();
}
