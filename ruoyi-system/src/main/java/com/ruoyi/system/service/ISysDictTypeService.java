package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysDictType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ISysDictTypeService extends IService<SysDictType> {

    List<SysDictType> selectDictTypeList(SysDictType dictType);

    SysDictType selectDictTypeById(Long dictId);

    SysDictType selectDictTypeByType(String dictType);

    boolean checkDictTypeUnique(SysDictType dictType);

    int insertDictType(SysDictType dictType);

    int updateDictType(SysDictType dictType);

    int deleteDictTypeByIds(Long[] dictIds);

    void resetDictCache();

    List<SysDictType> loadDictData();
}
