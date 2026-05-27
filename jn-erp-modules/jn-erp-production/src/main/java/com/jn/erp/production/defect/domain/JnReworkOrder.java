package com.jn.erp.production.defect.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;

import java.time.LocalDateTime;

@TableName("jn_rework_order")
public class JnReworkOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long reworkId;

    private String reworkNo;

    private Long defectId;

    private Long orderId;

    private String orderNo;

    private Long operationId;

    private Long originOperationId;

    private String originOperationName;

    private Integer reworkQty;

    private String status;

    private String assignee;

    private String assigneeName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String remark;

    public Long getReworkId() {
        return reworkId;
    }

    public void setReworkId(Long reworkId) {
        this.reworkId = reworkId;
    }

    public String getReworkNo() {
        return reworkNo;
    }

    public void setReworkNo(String reworkNo) {
        this.reworkNo = reworkNo;
    }

    public Long getDefectId() {
        return defectId;
    }

    public void setDefectId(Long defectId) {
        this.defectId = defectId;
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

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    public Long getOriginOperationId() {
        return originOperationId;
    }

    public void setOriginOperationId(Long originOperationId) {
        this.originOperationId = originOperationId;
    }

    public String getOriginOperationName() {
        return originOperationName;
    }

    public void setOriginOperationName(String originOperationName) {
        this.originOperationName = originOperationName;
    }

    public Integer getReworkQty() {
        return reworkQty;
    }

    public void setReworkQty(Integer reworkQty) {
        this.reworkQty = reworkQty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
