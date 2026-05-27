package com.jn.erp.finance.ar.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.finance.ar.domain.JnReceivableWriteOff;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface JnReceivableWriteOffMapper extends BaseMapperPlus<JnReceivableWriteOff> {

    List<JnReceivableWriteOff> selectByReceivableId(@Param("receivableId") Long receivableId);

}
