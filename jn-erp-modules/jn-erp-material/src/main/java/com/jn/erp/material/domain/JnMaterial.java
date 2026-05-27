package com.jn.erp.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;

import java.math.BigDecimal;

@TableName("jn_material")
public class JnMaterial extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long materialId;

    private String materialCode;

    private String materialName;

    private String spec;

    private Long categoryId;

    private String categoryName;

    private String unit;

    private BigDecimal unitPrice;

    private Integer safetyStock;

    private String status;

    private String delFlag;

    private String fanType;

    private String fanModel;

    private BigDecimal airflowCfm;

    private BigDecimal pressurePa;

    private BigDecimal powerKw;

    private Integer rpm;

    private BigDecimal noiseDba;

    private Integer impellerDiameter;

    private BigDecimal weightKg;

    private String isConfigurable;

    private String configRules;

    private String drawingNo;

    private Integer version;

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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getSafetyStock() {
        return safetyStock;
    }

    public void setSafetyStock(Integer safetyStock) {
        this.safetyStock = safetyStock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getFanType() {
        return fanType;
    }

    public void setFanType(String fanType) {
        this.fanType = fanType;
    }

    public String getFanModel() {
        return fanModel;
    }

    public void setFanModel(String fanModel) {
        this.fanModel = fanModel;
    }

    public BigDecimal getAirflowCfm() {
        return airflowCfm;
    }

    public void setAirflowCfm(BigDecimal airflowCfm) {
        this.airflowCfm = airflowCfm;
    }

    public BigDecimal getPressurePa() {
        return pressurePa;
    }

    public void setPressurePa(BigDecimal pressurePa) {
        this.pressurePa = pressurePa;
    }

    public BigDecimal getPowerKw() {
        return powerKw;
    }

    public void setPowerKw(BigDecimal powerKw) {
        this.powerKw = powerKw;
    }

    public Integer getRpm() {
        return rpm;
    }

    public void setRpm(Integer rpm) {
        this.rpm = rpm;
    }

    public BigDecimal getNoiseDba() {
        return noiseDba;
    }

    public void setNoiseDba(BigDecimal noiseDba) {
        this.noiseDba = noiseDba;
    }

    public Integer getImpellerDiameter() {
        return impellerDiameter;
    }

    public void setImpellerDiameter(Integer impellerDiameter) {
        this.impellerDiameter = impellerDiameter;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public String getIsConfigurable() {
        return isConfigurable;
    }

    public void setIsConfigurable(String isConfigurable) {
        this.isConfigurable = isConfigurable;
    }

    public String getConfigRules() {
        return configRules;
    }

    public void setConfigRules(String configRules) {
        this.configRules = configRules;
    }

    public String getDrawingNo() {
        return drawingNo;
    }

    public void setDrawingNo(String drawingNo) {
        this.drawingNo = drawingNo;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
