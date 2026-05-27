package com.jn.erp.production.defect.controller;

import com.jn.erp.production.defect.domain.JnDefect;
import com.jn.erp.production.defect.service.IJnDefectService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/erp/defect")
public class JnDefectController extends BaseController {

    @Autowired
    private IJnDefectService defectService;

    @RequiresPermissions("erp:defect:list")
    @GetMapping("/list")
    public TableDataInfo list(JnDefect defect) {
        startPage();
        List<JnDefect> list = defectService.selectList(defect);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:defect:query")
    @GetMapping("/{defectId}")
    public AjaxResult getInfo(@PathVariable Long defectId) {
        return success(defectService.selectById(defectId));
    }

    @RequiresPermissions("erp:defect:add")
    @Log(title = "不良品记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnDefect defect) {
        return toAjax(defectService.insert(defect));
    }

    @RequiresPermissions("erp:defect:edit")
    @Log(title = "不良品记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnDefect defect) {
        return toAjax(defectService.update(defect));
    }

    @RequiresPermissions("erp:defect:edit")
    @Log(title = "不良品记录", businessType = BusinessType.UPDATE)
    @PutMapping("/{defectId}/process")
    public AjaxResult process(@PathVariable Long defectId, @RequestBody Map<String, String> body) {
        String disposition = body.get("disposition");
        String handler = body.get("handler");
        defectService.processDefect(defectId, disposition, handler);
        return success();
    }

    @RequiresPermissions("erp:defect:remove")
    @Log(title = "不良品记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(defectService.deleteByIds(ids));
    }
}
