package com.jn.erp.warehouse.instock.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;

import java.util.List;

@TableName("jn_in_stock")
public class JnInStock extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long inStockId;

    private String inStockNo;

    private String inStockType;

    private Long whId;

    private String refNo;

    private String refType;

    private Integer totalQty;

    private String status;

    @TableField(exist = false)
    private List<JnInStockLine> lines;

    public Long getInStockId() {
        return inStockId;
    }

    public void setInStockId(Long inStockId) {
        this.inStockId = inStockId;
    }

    public String getInStockNo() {
        return inStockNo;
    }

    public void setInStockNo(String inStockNo) {
        this.inStockNo = inStockNo;
    }

    public String getInStockType() {
        return inStockType;
    }

    public void setInStockType(String inStockType) {
        this.inStockType = inStockType;
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

    public List<JnInStockLine> getLines() {
        return lines;
    }

    public void setLines(List<JnInStockLine> lines) {
        this.lines = lines;
    }
}
