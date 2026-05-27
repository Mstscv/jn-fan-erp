package com.jn.erp.production.equipment.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("jn_equipment")
public class JnEquipment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long equipmentId;

    private String equipmentCode;

    private String equipmentName;

    private String equipmentType;

    private String model;

    private Long workCenterId;

    private String workCenterName;

    private String status;

    private Integer dailyCapacity;

    private BigDecimal runningHours;

    private BigDecimal idleHours;

    private BigDecimal downtimeHours;

    private BigDecimal maintainHours;

    private LocalDate lastMaintainDate;

    private LocalDate nextMaintainDate;

    private String isActive;

    private String remark;

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentCode() {
        return equipmentCode;
    }

    public void setEquipmentCode(String equipmentCode) {
        this.equipmentCode = equipmentCode;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getWorkCenterId() {
        return workCenterId;
    }

    public void setWorkCenterId(Long workCenterId) {
        this.workCenterId = workCenterId;
    }

    public String getWorkCenterName() {
        return workCenterName;
    }

    public void setWorkCenterName(String workCenterName) {
        this.workCenterName = workCenterName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDailyCapacity() {
        return dailyCapacity;
    }

    public void setDailyCapacity(Integer dailyCapacity) {
        this.dailyCapacity = dailyCapacity;
    }

    public BigDecimal getRunningHours() {
        return runningHours;
    }

    public void setRunningHours(BigDecimal runningHours) {
        this.runningHours = runningHours;
    }

    public BigDecimal getIdleHours() {
        return idleHours;
    }

    public void setIdleHours(BigDecimal idleHours) {
        this.idleHours = idleHours;
    }

    public BigDecimal getDowntimeHours() {
        return downtimeHours;
    }

    public void setDowntimeHours(BigDecimal downtimeHours) {
        this.downtimeHours = downtimeHours;
    }

    public BigDecimal getMaintainHours() {
        return maintainHours;
    }

    public void setMaintainHours(BigDecimal maintainHours) {
        this.maintainHours = maintainHours;
    }

    public LocalDate getLastMaintainDate() {
        return lastMaintainDate;
    }

    public void setLastMaintainDate(LocalDate lastMaintainDate) {
        this.lastMaintainDate = lastMaintainDate;
    }

    public LocalDate getNextMaintainDate() {
        return nextMaintainDate;
    }

    public void setNextMaintainDate(LocalDate nextMaintainDate) {
        this.nextMaintainDate = nextMaintainDate;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
