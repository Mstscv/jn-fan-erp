package com.ruoyi.system.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.R;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.service.ISysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/operlog")
public class SysOperLogController extends BaseController {

    @Autowired
    private ISysOperLogService operLogService;

    @GetMapping("/list")
    public TableDataInfo list(SysOperLog operLog) {
        startPage();
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        return getDataTable(list);
    }

    @PostMapping
    public R<Void> add(@RequestBody SysOperLog operLog) {
        return toAjax(operLogService.insertOperLog(operLog));
    }

    @DeleteMapping("/{ids}")
    public R<Void> remove(@PathVariable Long[] ids) {
        return toAjax(operLogService.deleteOperLogByIds(ids));
    }

    @DeleteMapping("/clean")
    public R<Void> clean() {
        operLogService.cleanOperLog();
        return success();
    }

    private R<Void> toAjax(int rows) {
        return rows > 0 ? success() : error();
    }
}
