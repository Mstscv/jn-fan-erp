package com.jn.erp.purchase.demand.remote.domain;

import java.math.BigDecimal;

public class BomItemDTO {

    private Long bomItemId;

    private Long bomId;

    private Long materialId;

    private String materialName;

    private String materialSpec;

    private BigDecimal quantity;

    private String unit;

    public Long getBomItemId() {
        return bomItemId;
    }

    public void setBomItemId(Long bomItemId) {
        this.bomItemId = bomItemId;
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

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialSpec() {
        return materialSpec;
    }

    public void setMaterialSpec(String materialSpec) {
        this.materialSpec = materialSpec;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
