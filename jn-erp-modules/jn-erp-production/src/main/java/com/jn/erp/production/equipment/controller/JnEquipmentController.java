package com.jn.erp.production.equipment.controller;

import com.jn.erp.production.equipment.domain.JnEquipment;
import com.jn.erp.production.equipment.service.IJnEquipmentService;
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

@RestController
@RequestMapping("/erp/equipment")
public class JnEquipmentController extends BaseController {

    @Autowired
    private IJnEquipmentService equipmentService;

    @RequiresPermissions("erp:equipment:list")
    @GetMapping("/list")
    public TableDataInfo list(JnEquipment equipment) {
        startPage();
        List<JnEquipment> list = equipmentService.selectList(equipment);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:equipment:query")
    @GetMapping("/{equipmentId}")
    public AjaxResult getInfo(@PathVariable Long equipmentId) {
        return success(equipmentService.selectById(equipmentId));
    }

    @RequiresPermissions("erp:equipment:add")
    @Log(title = "设备管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnEquipment equipment) {
        return toAjax(equipmentService.insert(equipment));
    }

    @RequiresPermissions("erp:equipment:edit")
    @Log(title = "设备管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnEquipment equipment) {
        return toAjax(equipmentService.update(equipment));
    }

    @RequiresPermissions("erp:equipment:remove")
    @Log(title = "设备管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{equipmentIds}")
    public AjaxResult remove(@PathVariable Long[] equipmentIds) {
        return toAjax(equipmentService.deleteByIds(equipmentIds));
    }
}
