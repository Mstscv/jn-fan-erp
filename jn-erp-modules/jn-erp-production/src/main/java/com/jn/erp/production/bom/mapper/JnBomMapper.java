package com.jn.erp.production.bom.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.bom.domain.JnBom;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnBomMapper extends BaseMapperPlus<JnBom> {

    List<JnBom> selectBomTree(@Param("productId") Long productId);

    JnBom selectByBomCode(@Param("bomCode") String bomCode);
}
