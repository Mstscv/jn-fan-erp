package com.jn.erp.production.bom.copy;

import com.jn.erp.production.bom.domain.JnBom;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/erp/bom-copy")
public class JnBomCopyController extends BaseController {

    @Autowired
    private IJnBomCopyService bomCopyService;

    @Log(title = "BOM复制", businessType = BusinessType.INSERT)
    @RequiresPermissions("erp:bom:copy:copy")
    @PostMapping("/copy")
    public AjaxResult copyBom(@RequestBody Map<String, Object> body) {
        Long sourceBomId = Long.valueOf(body.get("sourceBomId").toString());
        String newBomName = body.get("newBomName").toString();
        String newBomCode = body.get("newBomCode").toString();
        JnBom newBom = bomCopyService.copyBom(sourceBomId, newBomName, newBomCode);
        return success(newBom);
    }

    @Log(title = "BOM引用", businessType = BusinessType.INSERT)
    @RequiresPermissions("erp:bom:copy:reference")
    @PostMapping("/reference")
    public AjaxResult quickReference(@RequestBody Map<String, Object> body) {
        Long sourceBomId = Long.valueOf(body.get("sourceBomId").toString());
        Long targetBomId = Long.valueOf(body.get("targetBomId").toString());
        bomCopyService.quickReference(sourceBomId, targetBomId);
        return success();
    }
}
