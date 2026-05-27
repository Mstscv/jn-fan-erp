package com.jn.erp.warehouse.outstock.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;

import java.util.List;

@TableName("jn_out_stock")
public class JnOutStock extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long outStockId;

    private String outStockNo;

    private String outStockType;

    private Long whId;

    private String refNo;

    private String refType;

    private Integer totalQty;

    private String status;

    @TableField(exist = false)
    private List<JnOutStockLine> lines;

    public Long getOutStockId() {
        return outStockId;
    }

    public void setOutStockId(Long outStockId) {
        this.outStockId = outStockId;
    }

    public String getOutStockNo() {
        return outStockNo;
    }

    public void setOutStockNo(String outStockNo) {
        this.outStockNo = outStockNo;
    }

    public String getOutStockType() {
        return outStockType;
    }

    public void setOutStockType(String outStockType) {
        this.outStockType = outStockType;
    }

    public Long getWhId() {
        return whId;
    }

    public void setWhId(Long whId) {
        this.whId = whId;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public Integer getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(Integer totalQty) {
        this.totalQty = totalQty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<JnOutStockLine> getLines() {
        return lines;
    }

    public void setLines(List<JnOutStockLine> lines) {
        this.lines = lines;
    }
}
