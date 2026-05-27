package com.jn.erp.material.controller;

import com.jn.erp.material.domain.JnMaterial;
import com.jn.erp.material.domain.dto.JnMaterialExportVo;
import com.jn.erp.material.service.IJnMaterialService;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.common.security.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/erp/material")
public class JnMaterialController extends BaseController {

    @Autowired
    private IJnMaterialService materialService;

    @RequiresPermissions("erp:material:list")
    @GetMapping("/list")
    public TableDataInfo list(JnMaterial material) {
        startPage();
        List<JnMaterial> list = materialService.selectList(material);
        return getDataTable(list);
    }

    @Log(title = "物料管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("erp:material:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, JnMaterial material) {
        List<JnMaterial> list = materialService.selectList(material);
        List<JnMaterialExportVo> voList = new ArrayList<>();
        for (JnMaterial item : list) {
            JnMaterialExportVo vo = new JnMaterialExportVo();
            BeanUtils.copyProperties(item, vo);
            voList.add(vo);
        }
        ExcelUtil<JnMaterialExportVo> util = new ExcelUtil<>(JnMaterialExportVo.class);
        util.exportExcel(response, voList, "物料数据");
    }

    @RequiresPermissions("erp:material:query")
    @GetMapping("/{materialId}")
    public AjaxResult getInfo(@PathVariable Long materialId) {
        return success(materialService.selectById(materialId));
    }

    @RequiresPermissions("erp:material:add")
    @Log(title = "物料管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnMaterial material) {
        if (!materialService.checkCodeUnique(material.getMaterialCode())) {
            return error("新增物料'" + material.getMaterialName() + "'失败，物料编码已存在");
        }
        material.setCreateBy(SecurityUtils.getUsername());
        return toAjax(materialService.insert(material));
    }

    @RequiresPermissions("erp:material:edit")
    @Log(title = "物料管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnMaterial material) {
        material.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(materialService.update(material));
    }

    @RequiresPermissions("erp:material:remove")
    @Log(title = "物料管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{materialIds}")
    public AjaxResult remove(@PathVariable Long[] materialIds) {
        return toAjax(materialService.deleteByIds(materialIds));
    }

    @GetMapping("/fanTypes")
    public AjaxResult fanTypes() {
        List<Map<String, Object>> dictList = new ArrayList<>();
        addDictItem(dictList, "离心风机", "CENTRIFUGAL");
        addDictItem(dictList, "轴流风机", "AXIAL");
        addDictItem(dictList, "混流风机", "MIXED_FLOW");
        return success(dictList);
    }

    @RequiresPermissions("erp:material:add")
    @GetMapping("/nextCode")
    public AjaxResult nextCode(String fanType, String fanModel, String category) {
        String code = materialService.getNextCode(fanType, fanModel, category);
        return success(code);
    }

    private void addDictItem(List<Map<String, Object>> list, String label, String value) {
        Map<String, Object> item = new HashMap<>();
        item.put("label", label);
        item.put("value", value);
        list.add(item);
    }
}
