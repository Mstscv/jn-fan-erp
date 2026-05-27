package com.jn.erp.production.picking.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.picking.domain.JnPickingLine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnPickingLineMapper extends BaseMapperPlus<JnPickingLine> {

    List<JnPickingLine> selectByPickingId(@Param("pickingId") Long pickingId);

    int deleteByPickingId(@Param("pickingId") Long pickingId);
}
