package com.jn.erp.purchase.demand.remote;

import com.jn.erp.purchase.demand.remote.domain.BomItemDTO;
import com.jn.erp.purchase.demand.remote.domain.WorkOrderDTO;
import com.ruoyi.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteProductionService", value = "jn-erp-production", path = "/erp/production")
public interface RemoteProductionService {

    @GetMapping("/work-order/list-by-status")
    R<List<WorkOrderDTO>> listWorkOrdersByStatus(@RequestParam("statusList") List<String> statusList);

    @GetMapping("/bom/item-list/{materialId}")
    R<List<BomItemDTO>> getBomItemList(@PathVariable("materialId") Long materialId);
}
