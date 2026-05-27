package com.jn.erp.purchase.receive.controller;

import com.jn.erp.purchase.receive.domain.JnPurchaseReceive;
import com.jn.erp.purchase.receive.service.IJnPurchaseReceiveService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
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
@RequestMapping("/erp/purchase-receive")
public class JnPurchaseReceiveController extends BaseController {

    @Autowired
    private IJnPurchaseReceiveService purchaseReceiveService;

    @GetMapping("/list")
    public TableDataInfo list(JnPurchaseReceive receive) {
        startPage();
        List<JnPurchaseReceive> list = purchaseReceiveService.selectList(receive);
        return getDataTable(list);
    }

    @GetMapping("/{receiveId}")
    public AjaxResult getInfo(@PathVariable Long receiveId) {
        return success(purchaseReceiveService.getById(receiveId));
    }

    @Log(title = "采购入库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Map<String, Object> params) {
        Long poId = Long.valueOf(params.get("poId").toString());
        Integer totalQty = params.get("totalQty") != null ? Integer.valueOf(params.get("totalQty").toString()) : 0;
        Integer okQty = params.get("okQty") != null ? Integer.valueOf(params.get("okQty").toString()) : 0;
        Integer badQty = params.get("badQty") != null ? Integer.valueOf(params.get("badQty").toString()) : 0;
        String remark = (String) params.get("remark");

        JnPurchaseReceive receive = purchaseReceiveService.createFromPo(poId, totalQty, okQty, badQty, remark);
        return success(receive);
    }

    @Log(title = "采购入库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnPurchaseReceive receive) {
        return toAjax(purchaseReceiveService.update(receive));
    }

    @Log(title = "采购入库", businessType = BusinessType.DELETE)
    @DeleteMapping("/{receiveIds}")
    public AjaxResult remove(@PathVariable Long[] receiveIds) {
        return toAjax(purchaseReceiveService.deleteByIds(receiveIds));
    }

    @Log(title = "采购入库质检", businessType = BusinessType.UPDATE)
    @PutMapping("/{receiveId}/qc")
    public AjaxResult updateQc(@PathVariable Long receiveId, @RequestBody Map<String, String> body) {
        String qcResult = body.get("qcResult");
        return toAjax(purchaseReceiveService.updateQcResult(receiveId, qcResult));
    }
}
