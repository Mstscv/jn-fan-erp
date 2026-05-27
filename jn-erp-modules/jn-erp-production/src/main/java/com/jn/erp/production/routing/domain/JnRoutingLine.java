package com.jn.erp.production.routing.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;

import java.math.BigDecimal;

@TableName("jn_routing_line")
public class JnRoutingLine extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long lineId;

    private Long routingId;

    private Long operationId;

    private String operationCode;

    private String operationName;

    private Integer seqNo;

    private Long workCenterId;

    private String workCenterCode;

    private String workCenterName;

    private Long nextOperationId;

    private BigDecimal standardTime;

    private BigDecimal setupTime;

    private BigDecimal minLeadTime;

    private BigDecimal maxLeadTime;

    private String parallelFlag;

    private String isKeyOperation;

    private Integer transferBatchQty;

    private String description;

    private Integer sortOrder;

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public Long getRoutingId() {
        return routingId;
    }

    public void setRoutingId(Long routingId) {
        this.routingId = routingId;
    }

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    public String getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public Integer getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }

    public Long getWorkCenterId() {
        return workCenterId;
    }

    public void setWorkCenterId(Long workCenterId) {
        this.workCenterId = workCenterId;
    }

    public String getWorkCenterCode() {
        return workCenterCode;
    }

    public void setWorkCenterCode(String workCenterCode) {
        this.workCenterCode = workCenterCode;
    }

    public String getWorkCenterName() {
        return workCenterName;
    }

    public void setWorkCenterName(String workCenterName) {
        this.workCenterName = workCenterName;
    }

    public Long getNextOperationId() {
        return nextOperationId;
    }

    public void setNextOperationId(Long nextOperationId) {
        this.nextOperationId = nextOperationId;
    }

    public BigDecimal getStandardTime() {
        return standardTime;
    }

    public void setStandardTime(BigDecimal standardTime) {
        this.standardTime = standardTime;
    }

    public BigDecimal getSetupTime() {
        return setupTime;
    }

    public void setSetupTime(BigDecimal setupTime) {
        this.setupTime = setupTime;
    }

    public BigDecimal getMinLeadTime() {
        return minLeadTime;
    }

    public void setMinLeadTime(BigDecimal minLeadTime) {
        this.minLeadTime = minLeadTime;
    }

    public BigDecimal getMaxLeadTime() {
        return maxLeadTime;
    }

    public void setMaxLeadTime(BigDecimal maxLeadTime) {
        this.maxLeadTime = maxLeadTime;
    }

    public String getParallelFlag() {
        return parallelFlag;
    }

    public void setParallelFlag(String parallelFlag) {
        this.parallelFlag = parallelFlag;
    }

    public String getIsKeyOperation() {
        return isKeyOperation;
    }

    public void setIsKeyOperation(String isKeyOperation) {
        this.isKeyOperation = isKeyOperation;
    }

    public Integer getTransferBatchQty() {
        return transferBatchQty;
    }

    public void setTransferBatchQty(Integer transferBatchQty) {
        this.transferBatchQty = transferBatchQty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
