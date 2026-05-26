package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysDictData;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ISysDictDataService extends IService<SysDictData> {

    List<SysDictData> selectDictDataList(SysDictData dictData);

    List<SysDictData> selectDictDataByType(String dictType);

    String selectDictLabel(String dictType, String dictValue);

    boolean checkDictDataUnique(SysDictData dictData);

    int insertDictData(SysDictData dictData);

    int updateDictData(SysDictData dictData);

    int deleteDictDataByIds(Long[] dictCodes);
}
