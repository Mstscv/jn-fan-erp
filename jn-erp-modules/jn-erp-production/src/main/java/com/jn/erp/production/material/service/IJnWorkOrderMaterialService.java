package com.jn.erp.production.material.service;

import com.jn.erp.production.material.domain.JnWorkOrderMaterial;

import java.util.List;

public interface IJnWorkOrderMaterialService {

    List<JnWorkOrderMaterial> selectByOrderId(Long orderId);

    List<JnWorkOrderMaterial> explodeBom(Long orderId);

    int allocateMaterial(Long demandId, java.math.BigDecimal qty);

    int batchAllocate(Long orderId);

}
