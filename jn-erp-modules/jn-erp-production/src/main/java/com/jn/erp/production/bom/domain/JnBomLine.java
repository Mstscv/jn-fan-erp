package com.jn.erp.production.bom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@TableName("jn_bom_line")
public class JnBomLine extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long lineId;

    private Long bomId;

    private Long parentLineId;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private String spec;

    private BigDecimal quantity;

    private String unit;

    private String positionNo;

    private BigDecimal scrapRate;

    private String isOptional;

    private String optionalGroup;

    private String optionalRule;

    private String defaultSelected;

    private Long alternativeId;

    private LocalDate effectiveDate;

    private LocalDate expireDate;

    private Integer sortOrder;

    private String remark;

    @TableField(exist = false)
    private List<JnBomLine> children;

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

    public Long getParentLineId() {
        return parentLineId;
    }

    public void setParentLineId(Long parentLineId) {
        this.parentLineId = parentLineId;
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

    public String getPositionNo() {
        return positionNo;
    }

    public void setPositionNo(String positionNo) {
        this.positionNo = positionNo;
    }

    public BigDecimal getScrapRate() {
        return scrapRate;
    }

    public void setScrapRate(BigDecimal scrapRate) {
        this.scrapRate = scrapRate;
    }

    public String getIsOptional() {
        return isOptional;
    }

    public void setIsOptional(String isOptional) {
        this.isOptional = isOptional;
    }

    public String getOptionalGroup() {
        return optionalGroup;
    }

    public void setOptionalGroup(String optionalGroup) {
        this.optionalGroup = optionalGroup;
    }

    public String getOptionalRule() {
        return optionalRule;
    }

    public void setOptionalRule(String optionalRule) {
        this.optionalRule = optionalRule;
    }

    public String getDefaultSelected() {
        return defaultSelected;
    }

    public void setDefaultSelected(String defaultSelected) {
        this.defaultSelected = defaultSelected;
    }

    public Long getAlternativeId() {
        return alternativeId;
    }

    public void setAlternativeId(Long alternativeId) {
        this.alternativeId = alternativeId;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<JnBomLine> getChildren() {
        return children;
    }

    public void setChildren(List<JnBomLine> children) {
        this.children = children;
    }
}
