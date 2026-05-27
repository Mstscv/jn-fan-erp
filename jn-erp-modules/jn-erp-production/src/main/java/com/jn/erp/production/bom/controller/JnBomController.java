package com.jn.erp.production.bom.controller;

import com.jn.erp.production.bom.domain.JnBom;
import com.jn.erp.production.bom.domain.JnBomLine;
import com.jn.erp.production.bom.service.IJnBomService;
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
@RequestMapping("/erp/bom")
public class JnBomController extends BaseController {

    @Autowired
    private IJnBomService bomService;

    @RequiresPermissions("erp:bom:list")
    @GetMapping("/list")
    public TableDataInfo list(JnBom bom) {
        startPage();
        List<JnBom> list = bomService.selectList(bom);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:bom:query")
    @GetMapping("/{bomId}")
    public AjaxResult getInfo(@PathVariable Long bomId) {
        JnBom bom = bomService.getById(bomId);
        return success(bom);
    }

    @RequiresPermissions("erp:bom:query")
    @GetMapping("/tree")
    public AjaxResult tree(Long bomId) {
        JnBom bom = bomService.getBomTree(bomId);
        return success(bom);
    }

    @RequiresPermissions("erp:bom:add")
    @Log(title = "BOM管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Map<String, Object> body) {
        JnBom bom = new JnBom();
        if (body.get("bomName") != null) {
            bom.setBomName((String) body.get("bomName"));
        }
        if (body.get("productId") != null) {
            bom.setProductId(Long.valueOf(body.get("productId").toString()));
        }
        if (body.get("productCode") != null) {
            bom.setProductCode((String) body.get("productCode"));
        }
        if (body.get("productName") != null) {
            bom.setProductName((String) body.get("productName"));
        }
        if (body.get("parentBomId") != null) {
            bom.setParentBomId(Long.valueOf(body.get("parentBomId").toString()));
        }
        if (body.get("version") != null) {
            bom.setVersion((String) body.get("version"));
        }
        if (body.get("effectiveDate") != null) {
            bom.setEffectiveDate(java.time.LocalDate.parse((String) body.get("effectiveDate")));
        }
        if (body.get("expireDate") != null) {
            bom.setExpireDate(java.time.LocalDate.parse((String) body.get("expireDate")));
        }
        if (body.get("remark") != null) {
            bom.setRemark((String) body.get("remark"));
        }

        @SuppressWarnings("unchecked")
        List<JnBomLine> lines = body.get("lines") != null
                ? (List<JnBomLine>) body.get("lines") : null;

        return toAjax(bomService.insertBom(bom, lines));
    }

    @RequiresPermissions("erp:bom:edit")
    @Log(title = "BOM管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Map<String, Object> body) {
        JnBom bom = new JnBom();
        if (body.get("bomId") != null) {
            bom.setBomId(Long.valueOf(body.get("bomId").toString()));
        }
        if (body.get("bomName") != null) {
            bom.setBomName((String) body.get("bomName"));
        }
        if (body.get("productId") != null) {
            bom.setProductId(Long.valueOf(body.get("productId").toString()));
        }
        if (body.get("productCode") != null) {
            bom.setProductCode((String) body.get("productCode"));
        }
        if (body.get("productName") != null) {
            bom.setProductName((String) body.get("productName"));
        }
        if (body.get("version") != null) {
            bom.setVersion((String) body.get("version"));
        }
        if (body.get("effectiveDate") != null) {
            bom.setEffectiveDate(java.time.LocalDate.parse((String) body.get("effectiveDate")));
        }
        if (body.get("expireDate") != null) {
            bom.setExpireDate(java.time.LocalDate.parse((String) body.get("expireDate")));
        }
        if (body.get("remark") != null) {
            bom.setRemark((String) body.get("remark"));
        }

        @SuppressWarnings("unchecked")
        List<JnBomLine> lines = body.get("lines") != null
                ? (List<JnBomLine>) body.get("lines") : null;

        return toAjax(bomService.updateBom(bom, lines));
    }

    @RequiresPermissions("erp:bom:remove")
    @Log(title = "BOM管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{bomIds}")
    public AjaxResult remove(@PathVariable Long[] bomIds) {
        return toAjax(bomService.deleteByIds(bomIds));
    }

    @RequiresPermissions("erp:bom:edit")
    @Log(title = "BOM管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{bomId}/status")
    public AjaxResult updateStatus(@PathVariable Long bomId, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        bomService.updateStatus(bomId, newStatus);
        return success();
    }

    @RequiresPermissions("erp:bom:edit")
    @Log(title = "BOM管理", businessType = BusinessType.UPDATE)
    @PostMapping("/{bomId}/calculate-cost")
    public AjaxResult calculateCost(@PathVariable Long bomId) {
        java.math.BigDecimal cost = bomService.calculateCost(bomId);
        return success(cost);
    }

    @RequiresPermissions("erp:bom:add")
    @Log(title = "BOM管理", businessType = BusinessType.INSERT)
    @PostMapping("/{bomId}/copy")
    public AjaxResult copyBom(@PathVariable Long bomId, @RequestBody Map<String, String> body) {
        String newName = body.get("name");
        JnBom newBom = bomService.copyBom(bomId, newName);
        return success(newBom);
    }

    @RequiresPermissions("erp:bom:edit")
    @Log(title = "BOM管理", businessType = BusinessType.UPDATE)
    @PostMapping("/replace-material")
    public AjaxResult replaceMaterial(@RequestBody Map<String, Object> body) {
        Long bomId = Long.valueOf(body.get("bomId").toString());
        Long oldMaterialId = Long.valueOf(body.get("oldMaterialId").toString());
        Long newMaterialId = Long.valueOf(body.get("newMaterialId").toString());
        bomService.replaceMaterial(bomId, oldMaterialId, newMaterialId);
        return success();
    }
}
