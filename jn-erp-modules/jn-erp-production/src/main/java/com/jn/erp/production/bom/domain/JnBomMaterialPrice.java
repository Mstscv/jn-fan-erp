package com.jn.erp.production.bom.domain;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;

@TableName("jn_material")
public class JnBomMaterialPrice implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long materialId;

    private BigDecimal unitPrice;

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
