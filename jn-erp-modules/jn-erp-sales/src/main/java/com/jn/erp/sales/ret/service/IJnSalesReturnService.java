package com.jn.erp.sales.ret.service;

import com.jn.erp.sales.ret.domain.JnSalesReturn;
import java.util.List;

public interface IJnSalesReturnService {

    List<JnSalesReturn> selectList(JnSalesReturn query);

    JnSalesReturn getById(Long returnId);

    int insert(JnSalesReturn salesReturn);

    int update(JnSalesReturn salesReturn);

    int deleteByIds(Long[] returnIds);

    int updateStatus(Long returnId, String newStatus);
}
