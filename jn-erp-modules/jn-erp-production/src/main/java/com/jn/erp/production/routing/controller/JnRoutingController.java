package com.jn.erp.production.routing.controller;

import com.jn.erp.production.routing.domain.JnRouting;
import com.jn.erp.production.routing.domain.JnRoutingLine;
import com.jn.erp.production.routing.service.IJnRoutingService;
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
@RequestMapping("/erp/routing")
public class JnRoutingController extends BaseController {

    @Autowired
    private IJnRoutingService routingService;

    @RequiresPermissions("erp:routing:list")
    @GetMapping("/list")
    public TableDataInfo list(JnRouting routing) {
        startPage();
        List<JnRouting> list = routingService.selectList(routing);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:routing:query")
    @GetMapping("/{routingId}")
    public AjaxResult getInfo(@PathVariable Long routingId) {
        return success(routingService.selectById(routingId));
    }

    @RequiresPermissions("erp:routing:query")
    @GetMapping("/{routingId}/lines")
    public AjaxResult getWithLines(@PathVariable Long routingId) {
        return success(routingService.selectWithLines(routingId));
    }

    @RequiresPermissions("erp:routing:add")
    @Log(title = "工艺路线管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Map<String, Object> body) {
        JnRouting routing = new JnRouting();
        if (body.get("routingName") != null) {
            routing.setRoutingName((String) body.get("routingName"));
        }
        if (body.get("productId") != null) {
            routing.setProductId(Long.valueOf(body.get("productId").toString()));
        }
        if (body.get("productCode") != null) {
            routing.setProductCode((String) body.get("productCode"));
        }
        if (body.get("productName") != null) {
            routing.setProductName((String) body.get("productName"));
        }
        if (body.get("productSpec") != null) {
            routing.setProductSpec((String) body.get("productSpec"));
        }
        if (body.get("version") != null) {
            routing.setVersion((String) body.get("version"));
        }
        if (body.get("remark") != null) {
            routing.setRemark((String) body.get("remark"));
        }

        @SuppressWarnings("unchecked")
        List<JnRoutingLine> lines = body.get("lines") != null
                ? (List<JnRoutingLine>) body.get("lines") : null;

        return toAjax(routingService.insert(routing, lines));
    }

    @RequiresPermissions("erp:routing:edit")
    @Log(title = "工艺路线管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Map<String, Object> body) {
        JnRouting routing = new JnRouting();
        if (body.get("routingId") != null) {
            routing.setRoutingId(Long.valueOf(body.get("routingId").toString()));
        }
        if (body.get("routingName") != null) {
            routing.setRoutingName((String) body.get("routingName"));
        }
        if (body.get("productId") != null) {
            routing.setProductId(Long.valueOf(body.get("productId").toString()));
        }
        if (body.get("productCode") != null) {
            routing.setProductCode((String) body.get("productCode"));
        }
        if (body.get("productName") != null) {
            routing.setProductName((String) body.get("productName"));
        }
        if (body.get("productSpec") != null) {
            routing.setProductSpec((String) body.get("productSpec"));
        }
        if (body.get("version") != null) {
            routing.setVersion((String) body.get("version"));
        }
        if (body.get("remark") != null) {
            routing.setRemark((String) body.get("remark"));
        }

        @SuppressWarnings("unchecked")
        List<JnRoutingLine> lines = body.get("lines") != null
                ? (List<JnRoutingLine>) body.get("lines") : null;

        return toAjax(routingService.update(routing, lines));
    }

    @RequiresPermissions("erp:routing:remove")
    @Log(title = "工艺路线管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{routingIds}")
    public AjaxResult remove(@PathVariable Long[] routingIds) {
        return toAjax(routingService.deleteByIds(routingIds));
    }

    @RequiresPermissions("erp:routing:edit")
    @Log(title = "工艺路线管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{routingId}/status")
    public AjaxResult updateStatus(@PathVariable Long routingId, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        routingService.updateStatus(routingId, newStatus);
        return success();
    }
}
