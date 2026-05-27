package com.jn.erp.warehouse.alert.controller;

import com.jn.erp.warehouse.alert.service.IJnSafetyAlertService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/erp/safety-alert")
public class JnSafetyAlertController extends BaseController {

    @Autowired
    private IJnSafetyAlertService safetyAlertService;

    @GetMapping("/list")
    public AjaxResult list() {
        List<Map<String, Object>> alerts = safetyAlertService.getAlerts();
        return success(alerts);
    }

    @GetMapping("/critical")
    public AjaxResult critical() {
        List<Map<String, Object>> alerts = safetyAlertService.getCriticalAlerts();
        return success(alerts);
    }

    @GetMapping("/warehouse/{whId}")
    public AjaxResult byWarehouse(@PathVariable Long whId) {
        List<Map<String, Object>> alerts = safetyAlertService.getAlertsByWarehouse(whId);
        return success(alerts);
    }
}
