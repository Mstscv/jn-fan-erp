package com.ruoyi.system.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.R;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.system.domain.SysLoginLog;
import com.ruoyi.system.service.ISysLoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/loginlog")
public class SysLoginLogController extends BaseController {

    @Autowired
    private ISysLoginLogService loginLogService;

    @GetMapping("/list")
    public TableDataInfo list(SysLoginLog loginLog) {
        startPage();
        List<SysLoginLog> list = loginLogService.selectLoginLogList(loginLog);
        return getDataTable(list);
    }

    @PostMapping
    public R<Void> add(@RequestBody SysLoginLog loginLog) {
        return toAjax(loginLogService.insertLoginLog(loginLog));
    }

    @DeleteMapping("/{ids}")
    public R<Void> remove(@PathVariable Long[] ids) {
        return toAjax(loginLogService.deleteLoginLogByIds(ids));
    }

    @DeleteMapping("/clean")
    public R<Void> clean() {
        loginLogService.cleanLoginLog();
        return success();
    }

    private R<Void> toAjax(int rows) {
        return rows > 0 ? success() : error();
    }
}
