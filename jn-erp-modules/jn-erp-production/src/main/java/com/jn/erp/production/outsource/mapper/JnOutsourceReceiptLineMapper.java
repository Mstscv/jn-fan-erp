package com.jn.erp.production.outsource.mapper;

import com.jn.erp.common.core.BaseMapperPlus;
import com.jn.erp.production.outsource.domain.JnOutsourceReceiptLine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnOutsourceReceiptLineMapper extends BaseMapperPlus<JnOutsourceReceiptLine> {

    List<JnOutsourceReceiptLine> selectByReceiptId(@Param("receiptId") Long receiptId);

    int deleteByReceiptId(@Param("receiptId") Long receiptId);

}
