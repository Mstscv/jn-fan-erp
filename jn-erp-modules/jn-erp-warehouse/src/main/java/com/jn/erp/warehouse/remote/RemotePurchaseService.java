package com.jn.erp.warehouse.remote;

import com.jn.erp.warehouse.remote.dto.PoLineDTO;
import com.ruoyi.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(contextId = "remotePurchaseService", value = "jn-erp-purchase", path = "/erp/purchase")
public interface RemotePurchaseService {

    @GetMapping("/po/lines/{poId}")
    R<List<PoLineDTO>> getPoLines(@PathVariable("poId") Long poId);
}
