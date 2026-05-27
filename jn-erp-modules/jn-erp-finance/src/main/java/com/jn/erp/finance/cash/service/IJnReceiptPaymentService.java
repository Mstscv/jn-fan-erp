package com.jn.erp.finance.cash.service;

import com.jn.erp.finance.cash.domain.JnReceiptPayment;

import java.util.List;

public interface IJnReceiptPaymentService {

    List<JnReceiptPayment> selectList(JnReceiptPayment query);

    JnReceiptPayment selectById(Long slipId);

    int insert(JnReceiptPayment receiptPayment);

    int update(JnReceiptPayment receiptPayment);

    int deleteByIds(Long[] slipIds);

    void approve(Long slipId);

    void cancel(Long slipId);
}
