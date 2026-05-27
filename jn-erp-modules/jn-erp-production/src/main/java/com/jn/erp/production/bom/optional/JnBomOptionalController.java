package com.jn.erp.production.bom.optional;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/erp/bom-optional")
public class JnBomOptionalController extends BaseController {

    @Autowired
    private JnOptionalRuleService optionalRuleService;

    @RequiresPermissions("erp:bom:optional:groups")
    @GetMapping("/{bomId}/groups")
    public AjaxResult getOptionGroups(@PathVariable Long bomId) {
        List<Map<String, Object>> groups = optionalRuleService.getOptionGroups(bomId);
        return success(groups);
    }

    @Log(title = "BOM选配规则评估", businessType = BusinessType.OTHER)
    @RequiresPermissions("erp:bom:optional:evaluate")
    @PostMapping("/evaluate")
    public AjaxResult evaluate(@RequestBody Map<String, Object> body) {
        Long bomId = Long.valueOf(body.get("bomId").toString());
        Map<String, Object> materialParams = (Map<String, Object>) body.get("materialParams");
        List<Map<String, Object>> results = optionalRuleService.getOptionGroups(bomId);
        return success(results);
    }

    @Log(title = "BOM默认选配设置", businessType = BusinessType.UPDATE)
    @RequiresPermissions("erp:bom:optional:default")
    @PutMapping("/default")
    public AjaxResult setDefault(@RequestBody Map<String, Object> body) {
        Long bomId = Long.valueOf(body.get("bomId").toString());
        String groupName = body.get("groupName").toString();
        Long lineId = Long.valueOf(body.get("lineId").toString());
        optionalRuleService.setDefaultSelection(bomId, groupName, lineId);
        return success();
    }
}
