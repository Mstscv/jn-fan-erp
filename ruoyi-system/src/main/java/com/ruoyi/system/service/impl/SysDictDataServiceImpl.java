package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysDictData;
import com.ruoyi.system.mapper.SysDictDataMapper;
import com.ruoyi.system.service.ISysDictDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements ISysDictDataService {

    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        if (dictData.getDictType() != null && !dictData.getDictType().isEmpty()) {
            wrapper.eq(SysDictData::getDictType, dictData.getDictType());
        }
        if (dictData.getDictLabel() != null && !dictData.getDictLabel().isEmpty()) {
            wrapper.like(SysDictData::getDictLabel, dictData.getDictLabel());
        }
        if (dictData.getStatus() != null && !dictData.getStatus().isEmpty()) {
            wrapper.eq(SysDictData::getStatus, dictData.getStatus());
        }
        wrapper.orderByAsc(SysDictData::getDictSort);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType);
        wrapper.eq(SysDictData::getStatus, "0");
        wrapper.orderByAsc(SysDictData::getDictSort);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType);
        wrapper.eq(SysDictData::getDictValue, dictValue);
        SysDictData dictData = baseMapper.selectOne(wrapper);
        return dictData != null ? dictData.getDictLabel() : "";
    }

    @Override
    public boolean checkDictDataUnique(SysDictData dictData) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictData.getDictType());
        wrapper.eq(SysDictData::getDictValue, dictData.getDictValue());
        if (dictData.getDictCode() != null) {
            wrapper.ne(SysDictData::getDictCode, dictData.getDictCode());
        }
        return baseMapper.selectCount(wrapper) == 0;
    }

    @Override
    @Transactional
    public int insertDictData(SysDictData dictData) {
        return baseMapper.insert(dictData);
    }

    @Override
    @Transactional
    public int updateDictData(SysDictData dictData) {
        return baseMapper.updateById(dictData);
    }

    @Override
    @Transactional
    public int deleteDictDataByIds(Long[] dictCodes) {
        int count = 0;
        for (Long dictCode : dictCodes) {
            count += baseMapper.deleteById(dictCode);
        }
        return count;
    }
}
