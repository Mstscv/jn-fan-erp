package com.jn.erp.production.bom.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.bom.domain.JnBomLine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnBomLineMapper extends BaseMapperPlus<JnBomLine> {

    List<JnBomLine> selectByBomId(@Param("bomId") Long bomId);

    List<JnBomLine> selectByParentLineId(@Param("parentLineId") Long parentLineId);

    int deleteByBomId(@Param("bomId") Long bomId);

    int deleteByBomIdList(@Param("bomIds") List<Long> bomIds);
}
