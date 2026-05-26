package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.mapper.SysConfigMapper;
import com.ruoyi.system.service.ISysConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {

    @Override
    public SysConfig selectConfigById(Long configId) {
        return baseMapper.selectById(configId);
    }

    @Override
    public String selectConfigByKey(String configKey) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, configKey);
        SysConfig config = baseMapper.selectOne(wrapper);
        return config != null ? config.getConfigValue() : "";
    }

    @Override
    public List<SysConfig> selectConfigList(SysConfig config) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        if (config.getConfigName() != null && !config.getConfigName().isEmpty()) {
            wrapper.like(SysConfig::getConfigName, config.getConfigName());
        }
        if (config.getConfigKey() != null && !config.getConfigKey().isEmpty()) {
            wrapper.like(SysConfig::getConfigKey, config.getConfigKey());
        }
        if (config.getConfigType() != null && !config.getConfigType().isEmpty()) {
            wrapper.eq(SysConfig::getConfigType, config.getConfigType());
        }
        wrapper.orderByAsc(SysConfig::getConfigId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean checkConfigKeyUnique(SysConfig config) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, config.getConfigKey());
        if (config.getConfigId() != null) {
            wrapper.ne(SysConfig::getConfigId, config.getConfigId());
        }
        return baseMapper.selectCount(wrapper) == 0;
    }

    @Override
    @Transactional
    public int insertConfig(SysConfig config) {
        return baseMapper.insert(config);
    }

    @Override
    @Transactional
    public int updateConfig(SysConfig config) {
        return baseMapper.updateById(config);
    }

    @Override
    @Transactional
    public void deleteConfigByIds(Long[] configIds) {
        for (Long configId : configIds) {
            baseMapper.deleteById(configId);
        }
    }

    @Override
    public void resetConfigCache() {
    }

    @Override
    public void loadingConfigCache() {
    }
}
