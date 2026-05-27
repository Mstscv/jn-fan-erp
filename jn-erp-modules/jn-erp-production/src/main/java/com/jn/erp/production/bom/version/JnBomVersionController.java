package com.jn.erp.production.bom.version;

import com.jn.erp.production.bom.domain.JnBomVersion;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/erp/bom-version")
public class JnBomVersionController extends BaseController {

    @Autowired
    private IJnBomVersionService bomVersionService;

    @RequiresPermissions("erp:bom:version:list")
    @GetMapping("/{bomId}/versions")
    public AjaxResult listVersions(@PathVariable Long bomId) {
        List<JnBomVersion> list = bomVersionService.getVersionsByBomId(bomId);
        return success(list);
    }

    @Log(title = "BOM版本创建", businessType = BusinessType.INSERT)
    @RequiresPermissions("erp:bom:version:create")
    @PostMapping("/{bomId}/versions")
    public AjaxResult createVersion(@PathVariable Long bomId, @RequestBody Map<String, String> body) {
        String changeLog = body.getOrDefault("changeLog", "");
        JnBomVersion version = bomVersionService.createVersion(bomId, changeLog);
        return success(version);
    }

    @Log(title = "BOM版本回滚", businessType = BusinessType.UPDATE)
    @RequiresPermissions("erp:bom:version:rollback")
    @PostMapping("/{bomId}/rollback/{versionId}")
    public AjaxResult rollback(@PathVariable Long bomId, @PathVariable Long versionId) {
        bomVersionService.rollback(bomId, versionId);
        return success();
    }

    @Log(title = "BOM版本删除", businessType = BusinessType.DELETE)
    @RequiresPermissions("erp:bom:version:delete")
    @DeleteMapping("/{versionId}")
    public AjaxResult deleteVersion(@PathVariable Long versionId) {
        bomVersionService.deleteVersion(versionId);
        return success();
    }
}
