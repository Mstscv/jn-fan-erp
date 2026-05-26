package com.ruoyi.system.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.R;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.system.domain.SysDictData;
import com.ruoyi.system.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController extends BaseController {

    @Autowired
    private ISysDictDataService dictDataService;

    @GetMapping("/list")
    public TableDataInfo list(SysDictData dictData) {
        startPage();
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        return getDataTable(list);
    }

    @GetMapping("/{dictCode}")
    public R<SysDictData> getInfo(@PathVariable Long dictCode) {
        return success(dictDataService.getById(dictCode));
    }

    @GetMapping("/type/{dictType}")
    public R<List<SysDictData>> dictType(@PathVariable String dictType) {
        List<SysDictData> data = dictDataService.selectDictDataByType(dictType);
        return success(data);
    }

    @PostMapping
    public R<Void> add(@RequestBody SysDictData dictData) {
        if (!dictDataService.checkDictDataUnique(dictData)) {
            return error("新增字典数据'" + dictData.getDictLabel() + "'失败，字典键值已存在");
        }
        return toAjax(dictDataService.insertDictData(dictData));
    }

    @PutMapping
    public R<Void> edit(@RequestBody SysDictData dictData) {
        if (!dictDataService.checkDictDataUnique(dictData)) {
            return error("修改字典数据'" + dictData.getDictLabel() + "'失败，字典键值已存在");
        }
        return toAjax(dictDataService.updateDictData(dictData));
    }

    @DeleteMapping("/{dictCodes}")
    public R<Void> remove(@PathVariable Long[] dictCodes) {
        dictDataService.deleteDictDataByIds(dictCodes);
        return success();
    }

    private R<Void> toAjax(int rows) {
        return rows > 0 ? success() : error();
    }
}
