package com.jn.erp.purchase.demand.controller;

import com.jn.erp.purchase.demand.domain.JnPurchaseDemand;
import com.jn.erp.purchase.demand.service.IJnPurchaseDemandService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
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
@RequestMapping("/erp/purchase-demand")
public class JnPurchaseDemandController extends BaseController {

    @Autowired
    private IJnPurchaseDemandService jnPurchaseDemandService;

    @GetMapping("/list")
    public TableDataInfo list(JnPurchaseDemand demand) {
        startPage();
        List<JnPurchaseDemand> list = jnPurchaseDemandService.selectList(demand);
        return getDataTable(list);
    }

    @GetMapping("/{demandId}")
    public AjaxResult getInfo(@PathVariable Long demandId) {
        return success(jnPurchaseDemandService.getById(demandId));
    }

    @PostMapping
    public AjaxResult add(@RequestBody JnPurchaseDemand demand) {
        return toAjax(jnPurchaseDemandService.insertDemand(demand));
    }

    @PutMapping
    public AjaxResult edit(@RequestBody JnPurchaseDemand demand) {
        return toAjax(jnPurchaseDemandService.updateDemand(demand));
    }

    @DeleteMapping("/{demandIds}")
    public AjaxResult remove(@PathVariable Long[] demandIds) {
        return toAjax(jnPurchaseDemandService.deleteByIds(demandIds));
    }

    @PutMapping("/{demandId}/convert")
    public AjaxResult convert(@PathVariable Long demandId) {
        return toAjax(jnPurchaseDemandService.convertToPo(new Long[]{demandId}));
    }

    @PostMapping("/run-mrp")
    public AjaxResult runMrp() {
        List<JnPurchaseDemand> demands = jnPurchaseDemandService.runMrp();
        return success(demands);
    }
}
