package com.jn.erp.warehouse.check.controller;

import com.jn.erp.warehouse.check.domain.JnCheckLine;
import com.jn.erp.warehouse.check.domain.JnInventoryCheck;
import com.jn.erp.warehouse.check.service.IJnCheckService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.utils.SecurityUtils;
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
@RequestMapping("/erp/inventory-check")
public class JnCheckController extends BaseController {

    @Autowired
    private IJnCheckService checkService;

    @GetMapping("/list")
    public TableDataInfo list(JnInventoryCheck check) {
        startPage();
        List<JnInventoryCheck> list = checkService.selectList(check);
        return getDataTable(list);
    }

    @GetMapping("/{checkId}")
    public AjaxResult getInfo(@PathVariable Long checkId) {
        return success(checkService.getById(checkId));
    }

    @Log(title = "库存盘点", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Map<String, Object> params) {
        JnInventoryCheck check = new JnInventoryCheck();
        if (params.get("whId") != null) {
            check.setWhId(Long.valueOf(params.get("whId").toString()));
        }
        if (params.get("checkDate") != null) {
            check.setCheckDate(java.time.LocalDate.parse(params.get("checkDate").toString()));
        }
        if (params.get("remark") != null) {
            check.setRemark(params.get("remark").toString());
        }
        check.setCreateBy(SecurityUtils.getUsername());

        List<JnCheckLine> lines = null;
        if (params.get("lines") != null) {
            lines = parseLines(params.get("lines"));
        }

        JnInventoryCheck result = checkService.createCheck(check, lines);
        return success(result);
    }

    @Log(title = "库存盘点", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Map<String, Object> params) {
        if (params.get("checkId") == null) {
            return error("盘点单ID不能为空");
        }

        JnInventoryCheck check = new JnInventoryCheck();
        check.setCheckId(Long.valueOf(params.get("checkId").toString()));
        if (params.get("whId") != null) {
            check.setWhId(Long.valueOf(params.get("whId").toString()));
        }
        if (params.get("checkDate") != null) {
            check.setCheckDate(java.time.LocalDate.parse(params.get("checkDate").toString()));
        }
        if (params.get("remark") != null) {
            check.setRemark(params.get("remark").toString());
        }
        check.setUpdateBy(SecurityUtils.getUsername());

        List<JnCheckLine> lines = null;
        if (params.get("lines") != null) {
            lines = parseLines(params.get("lines"));
        }

        JnInventoryCheck result = checkService.updateCheck(check, lines);
        return success(result);
    }

    @Log(title = "库存盘点", businessType = BusinessType.DELETE)
    @DeleteMapping("/{checkIds}")
    public AjaxResult remove(@PathVariable Long[] checkIds) {
        return toAjax(checkService.deleteByIds(checkIds));
    }

    @Log(title = "库存盘点", businessType = BusinessType.UPDATE)
    @PutMapping("/{checkId}/approve")
    public AjaxResult approve(@PathVariable Long checkId) {
        checkService.approve(checkId, SecurityUtils.getUsername());
        return success();
    }

    @SuppressWarnings("unchecked")
    private List<JnCheckLine> parseLines(Object linesObj) {
        if (linesObj instanceof List) {
            List<Map<String, Object>> lineMaps = (List<Map<String, Object>>) linesObj;
            return lineMaps.stream().map(m -> {
                JnCheckLine line = new JnCheckLine();
                if (m.get("materialId") != null) {
                    line.setMaterialId(Long.valueOf(m.get("materialId").toString()));
                }
                if (m.get("actualQty") != null) {
                    line.setActualQty(Integer.valueOf(m.get("actualQty").toString()));
                }
                if (m.get("unitPrice") != null) {
                    line.setUnitPrice(new java.math.BigDecimal(m.get("unitPrice").toString()));
                }
                if (m.get("remark") != null) {
                    line.setRemark(m.get("remark").toString());
                }
                return line;
            }).toList();
        }
        return null;
    }
}
