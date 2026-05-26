package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysDictType;
import com.ruoyi.system.mapper.SysDictTypeMapper;
import com.ruoyi.system.service.ISysDictTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements ISysDictTypeService {

    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        if (dictType.getDictName() != null && !dictType.getDictName().isEmpty()) {
            wrapper.like(SysDictType::getDictName, dictType.getDictName());
        }
        if (dictType.getDictType() != null && !dictType.getDictType().isEmpty()) {
            wrapper.eq(SysDictType::getDictType, dictType.getDictType());
        }
        if (dictType.getStatus() != null && !dictType.getStatus().isEmpty()) {
            wrapper.eq(SysDictType::getStatus, dictType.getStatus());
        }
        wrapper.orderByAsc(SysDictType::getDictId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public SysDictType selectDictTypeById(Long dictId) {
        return baseMapper.selectById(dictId);
    }

    @Override
    public SysDictType selectDictTypeByType(String dictType) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictType, dictType);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public boolean checkDictTypeUnique(SysDictType dictType) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictType, dictType.getDictType());
        if (dictType.getDictId() != null) {
            wrapper.ne(SysDictType::getDictId, dictType.getDictId());
        }
        return baseMapper.selectCount(wrapper) == 0;
    }

    @Override
    @Transactional
    public int insertDictType(SysDictType dictType) {
        return baseMapper.insert(dictType);
    }

    @Override
    @Transactional
    public int updateDictType(SysDictType dictType) {
        return baseMapper.updateById(dictType);
    }

    @Override
    @Transactional
    public int deleteDictTypeByIds(Long[] dictIds) {
        int count = 0;
        for (Long dictId : dictIds) {
            count += baseMapper.deleteById(dictId);
        }
        return count;
    }

    @Override
    public void resetDictCache() {
    }

    @Override
    public List<SysDictType> loadDictData() {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getStatus, "0");
        return baseMapper.selectList(wrapper);
    }
}
