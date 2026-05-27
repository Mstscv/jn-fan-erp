package com.jn.erp.warehouse.check.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

@TableName("jn_check_line")
public class JnCheckLine {

    @TableId(type = IdType.AUTO)
    private Long lineId;

    private Long checkId;

    private Long materialId;

    private Integer bookQty;

    private Integer actualQty;

    private Integer diffQty;

    private BigDecimal unitPrice;

    private BigDecimal diffAmount;

    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public JnCheckLine() {
    }

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public Long getCheckId() {
        return checkId;
    }

    public void setCheckId(Long checkId) {
        this.checkId = checkId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Integer getBookQty() {
        return bookQty;
    }

    public void setBookQty(Integer bookQty) {
        this.bookQty = bookQty;
    }

    public Integer getActualQty() {
        return actualQty;
    }

    public void setActualQty(Integer actualQty) {
        this.actualQty = actualQty;
    }

    public Integer getDiffQty() {
        return diffQty;
    }

    public void setDiffQty(Integer diffQty) {
        this.diffQty = diffQty;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getDiffAmount() {
        return diffAmount;
    }

    public void setDiffAmount(BigDecimal diffAmount) {
        this.diffAmount = diffAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
