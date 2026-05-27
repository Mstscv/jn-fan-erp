package com.jn.erp.warehouse.remote;

import com.jn.erp.warehouse.remote.dto.DeliveryLineDTO;
import com.ruoyi.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(contextId = "remoteSalesService", value = "jn-erp-sales", path = "/erp/sales")
public interface RemoteSalesService {

    @GetMapping("/delivery/lines/{deliveryId}")
    R<List<DeliveryLineDTO>> getDeliveryLines(@PathVariable("deliveryId") Long deliveryId);
}
