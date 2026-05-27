package com.jn.erp.material.domain.dto;

import com.ruoyi.common.core.annotation.Excel;

import java.math.BigDecimal;

public class JnMaterialExportVo {

    @Excel(name = "物料编码", width = 20)
    private String materialCode;

    @Excel(name = "物料名称", width = 30)
    private String materialName;

    @Excel(name = "规格型号", width = 20)
    private String spec;

    @Excel(name = "物料分类", width = 15)
    private String categoryName;

    @Excel(name = "单位", width = 8)
    private String unit;

    @Excel(name = "单价", width = 12)
    private BigDecimal unitPrice;

    @Excel(name = "安全库存", width = 10)
    private Integer safetyStock;

    @Excel(name = "风机类型", width = 15)
    private String fanType;

    @Excel(name = "型号", width = 15)
    private String fanModel;

    @Excel(name = "风量(CFM)", width = 12)
    private BigDecimal airflowCfm;

    @Excel(name = "风压(Pa)", width = 12)
    private BigDecimal pressurePa;

    @Excel(name = "功率(kW)", width = 12)
    private BigDecimal powerKw;

    @Excel(name = "转速(rpm)", width = 10)
    private Integer rpm;

    @Excel(name = "噪音(dBA)", width = 10)
    private BigDecimal noiseDba;

    @Excel(name = "叶轮直径(mm)", width = 12)
    private Integer impellerDiameter;

    @Excel(name = "重量(kg)", width = 10)
    private BigDecimal weightKg;

    @Excel(name = "图号", width = 20)
    private String drawingNo;

    @Excel(name = "状态", readConverterExp = "0=启用,1=停用", width = 8)
    private String status;

    @Excel(name = "可选配", readConverterExp = "Y=是,N=否", width = 8)
    private String isConfigurable;

    @Excel(name = "备注", width = 25)
    private String remark;

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

    public String getDrawingNo() {
        return drawingNo;
    }

    public void setDrawingNo(String drawingNo) {
        this.drawingNo = drawingNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsConfigurable() {
        return isConfigurable;
    }

    public void setIsConfigurable(String isConfigurable) {
        this.isConfigurable = isConfigurable;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
