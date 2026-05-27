package com.jn.erp.finance.costing.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("jn_work_order_cost")
public class JnWorkOrderCost extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long costId;

    private Long orderId;

    private String orderNo;

    private Long materialCostId;

    private String costType;

    private BigDecimal costAmount;

    private LocalDate costDate;

    private String status;

    private String remark;

    public Long getCostId() {
        return costId;
    }

    public void setCostId(Long costId) {
        this.costId = costId;
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

    public Long getMaterialCostId() {
        return materialCostId;
    }

    public void setMaterialCostId(Long materialCostId) {
        this.materialCostId = materialCostId;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public BigDecimal getCostAmount() {
        return costAmount;
    }

    public void setCostAmount(BigDecimal costAmount) {
        this.costAmount = costAmount;
    }

    public LocalDate getCostDate() {
        return costDate;
    }

    public void setCostDate(LocalDate costDate) {
        this.costDate = costDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
