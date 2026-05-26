package com.ruoyi.system.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.R;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.system.domain.SysDictType;
import com.ruoyi.system.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController extends BaseController {

    @Autowired
    private ISysDictTypeService dictTypeService;

    @GetMapping("/list")
    public TableDataInfo list(SysDictType dictType) {
        startPage();
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        return getDataTable(list);
    }

    @GetMapping("/{dictId}")
    public R<SysDictType> getInfo(@PathVariable Long dictId) {
        return success(dictTypeService.selectDictTypeById(dictId));
    }

    @PostMapping
    public R<Void> add(@RequestBody SysDictType dictType) {
        if (!dictTypeService.checkDictTypeUnique(dictType)) {
            return error("新增字典'" + dictType.getDictName() + "'失败，字典类型已存在");
        }
        return toAjax(dictTypeService.insertDictType(dictType));
    }

    @PutMapping
    public R<Void> edit(@RequestBody SysDictType dictType) {
        if (!dictTypeService.checkDictTypeUnique(dictType)) {
            return error("修改字典'" + dictType.getDictName() + "'失败，字典类型已存在");
        }
        return toAjax(dictTypeService.updateDictType(dictType));
    }

    @DeleteMapping("/{dictIds}")
    public R<Void> remove(@PathVariable Long[] dictIds) {
        dictTypeService.deleteDictTypeByIds(dictIds);
        return success();
    }

    @GetMapping("/optionselect")
    public R<List<SysDictType>> optionselect() {
        List<SysDictType> dictTypes = dictTypeService.selectDictTypeList(new SysDictType());
        return success(dictTypes);
    }

    private R<Void> toAjax(int rows) {
        return rows > 0 ? success() : error();
    }
}
