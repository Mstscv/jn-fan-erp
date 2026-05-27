package com.jn.erp.production.picking.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@TableName("jn_picking")
public class JnPicking extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long pickingId;

    private String pickingNo;

    private Long orderId;

    private String orderNo;

    private String pickingType;

    private String status;

    private Integer totalQty;

    private Long warehouseId;

    private String applyBy;

    private LocalDate applyDate;

    private String approveBy;

    private LocalDateTime approveTime;

    private String remark;

    @TableField(exist = false)
    private List<JnPickingLine> lines;

    public Long getPickingId() {
        return pickingId;
    }

    public void setPickingId(Long pickingId) {
        this.pickingId = pickingId;
    }

    public String getPickingNo() {
        return pickingNo;
    }

    public void setPickingNo(String pickingNo) {
        this.pickingNo = pickingNo;
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

    public String getPickingType() {
        return pickingType;
    }

    public void setPickingType(String pickingType) {
        this.pickingType = pickingType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(Integer totalQty) {
        this.totalQty = totalQty;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getApplyBy() {
        return applyBy;
    }

    public void setApplyBy(String applyBy) {
        this.applyBy = applyBy;
    }

    public LocalDate getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(LocalDate applyDate) {
        this.applyDate = applyDate;
    }

    public String getApproveBy() {
        return approveBy;
    }

    public void setApproveBy(String approveBy) {
        this.approveBy = approveBy;
    }

    public LocalDateTime getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(LocalDateTime approveTime) {
        this.approveTime = approveTime;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<JnPickingLine> getLines() {
        return lines;
    }

    public void setLines(List<JnPickingLine> lines) {
        this.lines = lines;
    }
}
