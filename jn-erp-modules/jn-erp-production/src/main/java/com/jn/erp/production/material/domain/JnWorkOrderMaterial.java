package com.jn.erp.production.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;

import java.math.BigDecimal;

@TableName("jn_work_order_material")
public class JnWorkOrderMaterial extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long demandId;

    private Long orderId;

    private String orderNo;

    private Long lineId;

    private Long bomId;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private String spec;

    private String unit;

    private BigDecimal requiredQty;

    private BigDecimal allocatedQty;

    private BigDecimal pickedQty;

    @TableField(exist = false)
    private BigDecimal availableStock;

    @TableField(exist = false)
    private BigDecimal shortageQty;

    private String sourceType;

    private String status;

    public Long getDemandId() {
        return demandId;
    }

    public void setDemandId(Long demandId) {
        this.demandId = demandId;
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

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public Long getBomId() {
        return bomId;
    }

    public void setBomId(Long bomId) {
        this.bomId = bomId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getRequiredQty() {
        return requiredQty;
    }

    public void setRequiredQty(BigDecimal requiredQty) {
        this.requiredQty = requiredQty;
    }

    public BigDecimal getAllocatedQty() {
        return allocatedQty;
    }

    public void setAllocatedQty(BigDecimal allocatedQty) {
        this.allocatedQty = allocatedQty;
    }

    public BigDecimal getPickedQty() {
        return pickedQty;
    }

    public void setPickedQty(BigDecimal pickedQty) {
        this.pickedQty = pickedQty;
    }

    public BigDecimal getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(BigDecimal availableStock) {
        this.availableStock = availableStock;
    }

    public BigDecimal getShortageQty() {
        return shortageQty;
    }

    public void setShortageQty(BigDecimal shortageQty) {
        this.shortageQty = shortageQty;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
