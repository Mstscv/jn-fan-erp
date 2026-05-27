package com.jn.erp.purchase.price.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 供应商报价表 jn_supplier_price
 *
 * 该表不存在于初始 DDL 中，请通过以下 SQL 创建：
 *
 * CREATE TABLE jn_supplier_price (
 *     price_id       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '报价ID',
 *     supplier_id    BIGINT       NOT NULL COMMENT '供应商ID',
 *     supplier_name  VARCHAR(100) DEFAULT NULL COMMENT '供应商名称',
 *     material_id    BIGINT       NOT NULL COMMENT '物料ID',
 *     material_code  VARCHAR(100) DEFAULT NULL COMMENT '物料编码',
 *     material_name  VARCHAR(200) DEFAULT NULL COMMENT '物料名称',
 *     spec           VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
 *     unit_price     DECIMAL(12,2) DEFAULT NULL COMMENT '单价',
 *     min_order_qty  INT          DEFAULT 1 COMMENT '最小起订量',
 *     lead_time_days INT          DEFAULT 30 COMMENT '交货周期（天）',
 *     effective_date DATE         DEFAULT NULL COMMENT '生效日期',
 *     expire_date    DATE         DEFAULT NULL COMMENT '失效日期',
 *     status         CHAR(1)      DEFAULT '0' COMMENT '状态（0正常 1停用）',
 *     del_flag       CHAR(1)      DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
 *     create_by      VARCHAR(64)  DEFAULT '' COMMENT '创建者',
 *     create_time    DATETIME     DEFAULT NULL COMMENT '创建时间',
 *     update_by      VARCHAR(64)  DEFAULT '' COMMENT '更新者',
 *     update_time    DATETIME     DEFAULT NULL COMMENT '更新时间',
 *     remark         VARCHAR(500) DEFAULT NULL COMMENT '备注',
 *     PRIMARY KEY (price_id)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商报价表';
 */
@TableName("jn_supplier_price")
public class JnSupplierPrice extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long priceId;

    private Long supplierId;

    private String supplierName;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private String spec;

    private BigDecimal unitPrice;

    private Integer minOrderQty;

    private Integer leadTimeDays;

    private LocalDate effectiveDate;

    private LocalDate expireDate;

    private String status;

    private String delFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public JnSupplierPrice() {
    }

    public Long getPriceId() {
        return priceId;
    }

    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getMinOrderQty() {
        return minOrderQty;
    }

    public void setMinOrderQty(Integer minOrderQty) {
        this.minOrderQty = minOrderQty;
    }

    public Integer getLeadTimeDays() {
        return leadTimeDays;
    }

    public void setLeadTimeDays(Integer leadTimeDays) {
        this.leadTimeDays = leadTimeDays;
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
}
