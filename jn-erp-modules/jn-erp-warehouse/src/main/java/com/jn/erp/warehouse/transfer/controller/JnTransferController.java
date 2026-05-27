package com.jn.erp.warehouse.transfer.controller;

import com.jn.erp.warehouse.transfer.service.IJnTransferService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/erp/inventory-transfer")
public class JnTransferController extends BaseController {

    @Autowired
    private IJnTransferService transferService;

    @Log(title = "库存调拨", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult transfer(@RequestBody Map<String, Object> params) {
        Long fromWhId = params.get("fromWhId") != null ? Long.valueOf(params.get("fromWhId").toString()) : null;
        Long toWhId = params.get("toWhId") != null ? Long.valueOf(params.get("toWhId").toString()) : null;
        Long materialId = params.get("materialId") != null ? Long.valueOf(params.get("materialId").toString()) : null;
        String materialCode = params.get("materialCode") != null ? params.get("materialCode").toString() : null;
        Integer qty = params.get("qty") != null ? Integer.valueOf(params.get("qty").toString()) : null;

        if (fromWhId == null || toWhId == null || materialId == null || qty == null) {
            return error("参数不完整: fromWhId, toWhId, materialId, qty 为必填项");
        }

        transferService.transfer(fromWhId, toWhId, materialId, materialCode, qty, SecurityUtils.getUsername());
        return success();
    }
}
