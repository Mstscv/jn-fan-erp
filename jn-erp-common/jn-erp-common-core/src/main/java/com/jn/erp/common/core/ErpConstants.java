package com.jn.erp.common.core;

import org.springframework.stereotype.Component;

@Component
public class ErpConstants {

    public static final String ERP_NAME = "精恩风机ERP系统";
    public static final String ERP_VERSION = "1.0.0";
    public static final String COMPANY_NAME = "无锡精恩风机有限公司";

    public static final String FAN_TYPE_CENTRIFUGAL = "CENTRIFUGAL";
    public static final String FAN_TYPE_AXIAL = "AXIAL";
    public static final String FAN_TYPE_MIXED_FLOW = "MIXED_FLOW";

    public static final String CENTRIFUGAL = "CENTRIFUGAL";
    public static final String AXIAL = "AXIAL";
    public static final String MIXED_FLOW = "MIXED_FLOW";

    public static final String WORK_ORDER_STATUS_PENDING = "PENDING";
    public static final String WORK_ORDER_STATUS_SCHEDULED = "SCHEDULED";
    public static final String WORK_ORDER_STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String WORK_ORDER_STATUS_COMPLETED = "COMPLETED";
    public static final String WORK_ORDER_STATUS_CLOSED = "CLOSED";

    public static final String BOM_STATUS_DRAFT = "DRAFT";
    public static final String BOM_STATUS_APPROVED = "APPROVED";
    public static final String BOM_STATUS_DEPRECATED = "DEPRECATED";

    public static final String QC_TYPE_IQC = "IQC";
    public static final String QC_TYPE_IPQC = "IPQC";
    public static final String QC_TYPE_OQC = "OQC";

    public static final String QC_IQC = "IQC";
    public static final String QC_IPQC = "IPQC";
    public static final String QC_OQC = "OQC";

    public static final String INVENTORY_CHANGE_TYPE_IN = "IN";
    public static final String INVENTORY_CHANGE_TYPE_OUT = "OUT";
    public static final String INVENTORY_CHANGE_TYPE_TRANSFER = "TRANSFER";
    public static final String INVENTORY_CHANGE_TYPE_ADJUSTMENT = "ADJUSTMENT";
    public static final String INVENTORY_CHANGE_TYPE_RETURN = "RETURN";

    public static final String RESET_MODE_DAILY = "DAILY";
    public static final String RESET_MODE_MONTHLY = "MONTHLY";
    public static final String RESET_MODE_YEARLY = "YEARLY";
}
