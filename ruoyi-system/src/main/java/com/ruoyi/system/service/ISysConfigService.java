package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ISysConfigService extends IService<SysConfig> {

    SysConfig selectConfigById(Long configId);

    String selectConfigByKey(String configKey);

    List<SysConfig> selectConfigList(SysConfig config);

    boolean checkConfigKeyUnique(SysConfig config);

    int insertConfig(SysConfig config);

    int updateConfig(SysConfig config);

    void deleteConfigByIds(Long[] configIds);

    void resetConfigCache();

    void loadingConfigCache();
}
