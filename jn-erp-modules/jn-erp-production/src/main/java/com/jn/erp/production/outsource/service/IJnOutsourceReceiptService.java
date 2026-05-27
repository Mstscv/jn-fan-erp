package com.jn.erp.production.outsource.service;

import com.jn.erp.production.outsource.domain.JnOutsourceReceipt;
import com.jn.erp.production.outsource.domain.JnOutsourceReceiptLine;

import java.util.List;

public interface IJnOutsourceReceiptService {

    List<JnOutsourceReceipt> selectList(JnOutsourceReceipt query);

    JnOutsourceReceipt selectById(Long id);

    JnOutsourceReceipt selectWithLines(Long id);

    int insert(JnOutsourceReceipt receipt, List<JnOutsourceReceiptLine> lines);

    int update(JnOutsourceReceipt receipt, List<JnOutsourceReceiptLine> lines);

    int deleteByIds(Long[] ids);

    void approve(Long receiptId);

    void reject(Long receiptId, String reason);

}
