package com.jn.erp.production.quality.controller;

import com.jn.erp.production.quality.domain.JnInspection;
import com.jn.erp.production.quality.domain.JnInspectionLine;
import com.jn.erp.production.quality.service.IJnInspectionService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/erp/inspection")
public class JnInspectionController extends BaseController {

    @Autowired
    private IJnInspectionService inspectionService;

    @RequiresPermissions("erp:inspection:list")
    @GetMapping("/list")
    public TableDataInfo list(JnInspection inspection) {
        startPage();
        List<JnInspection> list = inspectionService.selectList(inspection);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:inspection:query")
    @GetMapping("/{inspectionId}")
    public AjaxResult getInfo(@PathVariable Long inspectionId) {
        return success(inspectionService.selectWithLines(inspectionId));
    }

    @RequiresPermissions("erp:inspection:add")
    @Log(title = "来料检验", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Map<String, Object> body) {
        JnInspection inspection = new JnInspection();
        if (body.get("inspectionType") != null) {
            inspection.setInspectionType((String) body.get("inspectionType"));
        }
        if (body.get("sourceOrderId") != null) {
            inspection.setSourceOrderId(Long.valueOf(body.get("sourceOrderId").toString()));
        }
        if (body.get("sourceOrderNo") != null) {
            inspection.setSourceOrderNo((String) body.get("sourceOrderNo"));
        }
        if (body.get("sourceOrderLineId") != null) {
            inspection.setSourceOrderLineId(Long.valueOf(body.get("sourceOrderLineId").toString()));
        }
        if (body.get("materialId") != null) {
            inspection.setMaterialId(Long.valueOf(body.get("materialId").toString()));
        }
        if (body.get("materialCode") != null) {
            inspection.setMaterialCode((String) body.get("materialCode"));
        }
        if (body.get("materialName") != null) {
            inspection.setMaterialName((String) body.get("materialName"));
        }
        if (body.get("spec") != null) {
            inspection.setSpec((String) body.get("spec"));
        }
        if (body.get("batchNo") != null) {
            inspection.setBatchNo((String) body.get("batchNo"));
        }
        if (body.get("sampleQty") != null) {
            inspection.setSampleQty(Integer.valueOf(body.get("sampleQty").toString()));
        }
        if (body.get("rejectQty") != null) {
            inspection.setRejectQty(Integer.valueOf(body.get("rejectQty").toString()));
        }
        if (body.get("remark") != null) {
            inspection.setRemark((String) body.get("remark"));
        }

        @SuppressWarnings("unchecked")
        List<JnInspectionLine> lines = body.get("lines") != null
                ? (List<JnInspectionLine>) body.get("lines") : null;

        return toAjax(inspectionService.insert(inspection, lines));
    }

    @RequiresPermissions("erp:inspection:edit")
    @Log(title = "来料检验", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Map<String, Object> body) {
        JnInspection inspection = new JnInspection();
        if (body.get("inspectionId") != null) {
            inspection.setInspectionId(Long.valueOf(body.get("inspectionId").toString()));
        }
        if (body.get("inspectionType") != null) {
            inspection.setInspectionType((String) body.get("inspectionType"));
        }
        if (body.get("sourceOrderId") != null) {
            inspection.setSourceOrderId(Long.valueOf(body.get("sourceOrderId").toString()));
        }
        if (body.get("sourceOrderNo") != null) {
            inspection.setSourceOrderNo((String) body.get("sourceOrderNo"));
        }
        if (body.get("sourceOrderLineId") != null) {
            inspection.setSourceOrderLineId(Long.valueOf(body.get("sourceOrderLineId").toString()));
        }
        if (body.get("materialId") != null) {
            inspection.setMaterialId(Long.valueOf(body.get("materialId").toString()));
        }
        if (body.get("materialCode") != null) {
            inspection.setMaterialCode((String) body.get("materialCode"));
        }
        if (body.get("materialName") != null) {
            inspection.setMaterialName((String) body.get("materialName"));
        }
        if (body.get("spec") != null) {
            inspection.setSpec((String) body.get("spec"));
        }
        if (body.get("batchNo") != null) {
            inspection.setBatchNo((String) body.get("batchNo"));
        }
        if (body.get("sampleQty") != null) {
            inspection.setSampleQty(Integer.valueOf(body.get("sampleQty").toString()));
        }
        if (body.get("rejectQty") != null) {
            inspection.setRejectQty(Integer.valueOf(body.get("rejectQty").toString()));
        }
        if (body.get("remark") != null) {
            inspection.setRemark((String) body.get("remark"));
        }

        @SuppressWarnings("unchecked")
        List<JnInspectionLine> lines = body.get("lines") != null
                ? (List<JnInspectionLine>) body.get("lines") : null;

        return toAjax(inspectionService.update(inspection, lines));
    }

    @RequiresPermissions("erp:inspection:edit")
    @Log(title = "来料检验", businessType = BusinessType.UPDATE)
    @PutMapping("/{inspectionId}/submit")
    public AjaxResult submit(@PathVariable Long inspectionId) {
        inspectionService.submit(inspectionId);
        return success();
    }

    @RequiresPermissions("erp:inspection:edit")
    @Log(title = "来料检验", businessType = BusinessType.UPDATE)
    @PutMapping("/{inspectionId}/approve")
    public AjaxResult approve(@PathVariable Long inspectionId) {
        inspectionService.approve(inspectionId, "PASS");
        return success();
    }

    @RequiresPermissions("erp:inspection:edit")
    @Log(title = "来料检验", businessType = BusinessType.UPDATE)
    @PutMapping("/{inspectionId}/reject")
    public AjaxResult reject(@PathVariable Long inspectionId, @RequestBody Map<String, Object> body) {
        String defectDesc = body.get("defectDesc") != null ? (String) body.get("defectDesc") : null;
        if (body.get("rejectQty") != null) {
            JnInspection inspection = new JnInspection();
            inspection.setInspectionId(inspectionId);
            inspection.setRejectQty(Integer.valueOf(body.get("rejectQty").toString()));
            inspectionService.update(inspection, null);
        }
        inspectionService.reject(inspectionId, defectDesc);
        return success();
    }

    @RequiresPermissions("erp:inspection:query")
    @GetMapping("/by-source/{sourceOrderId}")
    public AjaxResult getBySourceOrder(@PathVariable Long sourceOrderId) {
        return success(inspectionService.selectWithLines(sourceOrderId));
    }

    @RequiresPermissions("erp:inspection:list")
    @GetMapping("/by-type")
    public TableDataInfo getByType(@RequestParam String type, @RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate) {
        startPage();
        JnInspection query = new JnInspection();
        query.setInspectionType(type);
        List<JnInspection> list = inspectionService.selectList(query);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:inspection:remove")
    @Log(title = "来料检验", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(inspectionService.deleteByIds(ids));
    }
}
