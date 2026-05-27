package com.jn.erp.production.outsource.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.outsource.domain.JnOutsourceDispatchLine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnOutsourceDispatchLineMapper extends BaseMapperPlus<JnOutsourceDispatchLine> {

    List<JnOutsourceDispatchLine> selectByDispatchId(@Param("dispatchId") Long dispatchId);

    int deleteByDispatchId(@Param("dispatchId") Long dispatchId);

}
