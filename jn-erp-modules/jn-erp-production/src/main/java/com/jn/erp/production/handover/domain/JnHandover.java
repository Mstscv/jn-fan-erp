package com.jn.erp.production.handover.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;

@TableName("jn_handover")
public class JnHandover extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long handoverId;

    private String handoverNo;

    private Long orderId;

    private String orderNo;

    private Long fromOperationId;

    private String fromOperationCode;

    private String fromOperationName;

    private Long toOperationId;

    private String toOperationCode;

    private String toOperationName;

    private Long fromWorkCenterId;

    private Long toWorkCenterId;

    private String fromWorker;

    private String toWorker;

    private Integer quantity;

    private Integer defectQty;

    private String status;

    private String remark;

    public Long getHandoverId() {
        return handoverId;
    }

    public void setHandoverId(Long handoverId) {
        this.handoverId = handoverId;
    }

    public String getHandoverNo() {
        return handoverNo;
    }

    public void setHandoverNo(String handoverNo) {
        this.handoverNo = handoverNo;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getFromOperationId() {
        return fromOperationId;
    }

    public void setFromOperationId(Long fromOperationId) {
        this.fromOperationId = fromOperationId;
    }

    public String getFromOperationCode() {
        return fromOperationCode;
    }

    public void setFromOperationCode(String fromOperationCode) {
        this.fromOperationCode = fromOperationCode;
    }

    public String getFromOperationName() {
        return fromOperationName;
    }

    public void setFromOperationName(String fromOperationName) {
        this.fromOperationName = fromOperationName;
    }

    public Long getToOperationId() {
        return toOperationId;
    }

    public void setToOperationId(Long toOperationId) {
        this.toOperationId = toOperationId;
    }

    public String getToOperationCode() {
        return toOperationCode;
    }

    public void setToOperationCode(String toOperationCode) {
        this.toOperationCode = toOperationCode;
    }

    public String getToOperationName() {
        return toOperationName;
    }

    public void setToOperationName(String toOperationName) {
        this.toOperationName = toOperationName;
    }

    public Long getFromWorkCenterId() {
        return fromWorkCenterId;
    }

    public void setFromWorkCenterId(Long fromWorkCenterId) {
        this.fromWorkCenterId = fromWorkCenterId;
    }

    public Long getToWorkCenterId() {
        return toWorkCenterId;
    }

    public void setToWorkCenterId(Long toWorkCenterId) {
        this.toWorkCenterId = toWorkCenterId;
    }

    public String getFromWorker() {
        return fromWorker;
    }

    public void setFromWorker(String fromWorker) {
        this.fromWorker = fromWorker;
    }

    public String getToWorker() {
        return toWorker;
    }

    public void setToWorker(String toWorker) {
        this.toWorker = toWorker;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getDefectQty() {
        return defectQty;
    }

    public void setDefectQty(Integer defectQty) {
        this.defectQty = defectQty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
