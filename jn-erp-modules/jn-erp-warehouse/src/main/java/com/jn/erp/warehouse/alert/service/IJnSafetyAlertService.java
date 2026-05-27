package com.jn.erp.warehouse.alert.service;

import java.util.List;
import java.util.Map;

public interface IJnSafetyAlertService {

    List<Map<String, Object>> getAlerts();

    List<Map<String, Object>> getAlertsByWarehouse(Long whId);

    List<Map<String, Object>> getCriticalAlerts();
}
