package com.jn.erp.purchase.demand.remote;

import com.jn.erp.purchase.demand.remote.domain.InventoryDTO;
import com.ruoyi.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteWarehouseService", value = "jn-erp-warehouse", path = "/erp/warehouse")
public interface RemoteWarehouseService {

    @GetMapping("/inventory/by-material")
    R<InventoryDTO> getInventoryByMaterial(@RequestParam("materialId") Long materialId);
}
