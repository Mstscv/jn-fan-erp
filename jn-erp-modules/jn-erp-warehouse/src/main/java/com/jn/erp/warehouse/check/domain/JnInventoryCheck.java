package com.jn.erp.warehouse.check.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.web.domain.BaseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@TableName("jn_inventory_check")
public class JnInventoryCheck extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long checkId;

    private String checkNo;

    private Long whId;

    private LocalDate checkDate;

    private String status;

    private Integer diffCount;

    private String approveBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approveTime;

    @TableField(exist = false)
    private List<JnCheckLine> lines;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public JnInventoryCheck() {
    }

    public Long getCheckId() {
        return checkId;
    }

    public void setCheckId(Long checkId) {
        this.checkId = checkId;
    }

    public String getCheckNo() {
        return checkNo;
    }

    public void setCheckNo(String checkNo) {
        this.checkNo = checkNo;
    }

    public Long getWhId() {
        return whId;
    }

    public void setWhId(Long whId) {
        this.whId = whId;
    }

    public LocalDate getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(LocalDate checkDate) {
        this.checkDate = checkDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDiffCount() {
        return diffCount;
    }

    public void setDiffCount(Integer diffCount) {
        this.diffCount = diffCount;
    }

    public String getApproveBy() {
        return approveBy;
    }

    public void setApproveBy(String approveBy) {
        this.approveBy = approveBy;
    }

    public LocalDateTime getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(LocalDateTime approveTime) {
        this.approveTime = approveTime;
    }

    public List<JnCheckLine> getLines() {
        return lines;
    }

    public void setLines(List<JnCheckLine> lines) {
        this.lines = lines;
    }
}
