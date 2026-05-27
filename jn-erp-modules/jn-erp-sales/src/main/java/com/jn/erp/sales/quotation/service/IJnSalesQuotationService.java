package com.jn.erp.sales.quotation.service;

import com.jn.erp.sales.quotation.domain.JnQuotationLine;
import com.jn.erp.sales.quotation.domain.JnSalesQuotation;

import java.util.List;

public interface IJnSalesQuotationService {

    List<JnSalesQuotation> selectList(JnSalesQuotation query);

    JnSalesQuotation getById(Long quotationId);

    int insertWithLines(JnSalesQuotation quotation, List<JnQuotationLine> lines);

    int updateWithLines(JnSalesQuotation quotation, List<JnQuotationLine> lines);

    int deleteByIds(Long[] quotationIds);

    void approve(Long quotationId);

    String generateQuotationNo();
}
