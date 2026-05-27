package com.jn.erp.production.quality.controller;

import com.jn.erp.production.quality.domain.JnInspectionStandard;
import com.jn.erp.production.quality.service.IJnInspectionStandardService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/erp/inspection-standard")
public class JnInspectionStandardController extends BaseController {

    @Autowired
    private IJnInspectionStandardService inspectionStandardService;

    @RequiresPermissions("erp:inspection:std:list")
    @GetMapping("/list")
    public TableDataInfo list(JnInspectionStandard standard) {
        startPage();
        List<JnInspectionStandard> list = inspectionStandardService.selectList(standard);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:inspection:std:query")
    @GetMapping("/{standardId}")
    public AjaxResult getInfo(@PathVariable Long standardId) {
        return success(inspectionStandardService.selectById(standardId));
    }

    @RequiresPermissions("erp:inspection:std:add")
    @Log(title = "检验标准", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnInspectionStandard standard) {
        return toAjax(inspectionStandardService.insert(standard));
    }

    @RequiresPermissions("erp:inspection:std:edit")
    @Log(title = "检验标准", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnInspectionStandard standard) {
        return toAjax(inspectionStandardService.update(standard));
    }

    @RequiresPermissions("erp:inspection:std:query")
    @GetMapping("/suggest")
    public AjaxResult suggest(@RequestParam(required = false) Long materialId,
                              @RequestParam(required = false) Long operationId,
                              @RequestParam(required = false) String inspectionType) {
        return success(inspectionStandardService.selectByMaterialOrOperation(materialId, operationId, inspectionType));
    }

    @RequiresPermissions("erp:inspection:std:remove")
    @Log(title = "检验标准", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(inspectionStandardService.deleteByIds(ids));
    }
}
