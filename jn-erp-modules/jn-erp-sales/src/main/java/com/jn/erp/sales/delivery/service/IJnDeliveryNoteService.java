package com.jn.erp.sales.delivery.service;

import com.jn.erp.sales.delivery.domain.JnDeliveryNote;
import java.util.List;

public interface IJnDeliveryNoteService {

    List<JnDeliveryNote> selectList(JnDeliveryNote query);

    JnDeliveryNote getById(Long deliveryId);

    int insert(JnDeliveryNote deliveryNote);

    int update(JnDeliveryNote deliveryNote);

    int deleteByIds(Long[] deliveryIds);

    int markShipped(Long deliveryId);
}
