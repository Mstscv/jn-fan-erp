package com.jn.erp.production.material.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.material.domain.JnWorkOrderMaterial;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnWorkOrderMaterialMapper extends BaseMapperPlus<JnWorkOrderMaterial> {

    List<JnWorkOrderMaterial> selectByOrderId(@Param("orderId") Long orderId);

    int deleteByOrderId(@Param("orderId") Long orderId);

}
