package com.jn.erp.purchase.receive.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;

import java.time.LocalDate;

@TableName("jn_purchase_receive")
public class JnPurchaseReceive extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long receiveId;

    private String receiveNo;

    private Long poId;

    private Long supplierId;

    private LocalDate receiveDate;

    private Integer totalQty;

    private Integer okQty;

    private Integer badQty;

    private String qcResult;

    private String status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public JnPurchaseReceive() {
    }

    public Long getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(Long receiveId) {
        this.receiveId = receiveId;
    }

    public String getReceiveNo() {
        return receiveNo;
    }

    public void setReceiveNo(String receiveNo) {
        this.receiveNo = receiveNo;
    }

    public Long getPoId() {
        return poId;
    }

    public void setPoId(Long poId) {
        this.poId = poId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public LocalDate getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(LocalDate receiveDate) {
        this.receiveDate = receiveDate;
    }

    public Integer getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(Integer totalQty) {
        this.totalQty = totalQty;
    }

    public Integer getOkQty() {
        return okQty;
    }

    public void setOkQty(Integer okQty) {
        this.okQty = okQty;
    }

    public Integer getBadQty() {
        return badQty;
    }

    public void setBadQty(Integer badQty) {
        this.badQty = badQty;
    }

    public String getQcResult() {
        return qcResult;
    }

    public void setQcResult(String qcResult) {
        this.qcResult = qcResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
