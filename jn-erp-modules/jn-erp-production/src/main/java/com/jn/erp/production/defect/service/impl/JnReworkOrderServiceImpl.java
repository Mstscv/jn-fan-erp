package com.jn.erp.production.defect.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.defect.domain.JnDefect;
import com.jn.erp.production.defect.domain.JnReworkOrder;
import com.jn.erp.production.defect.mapper.JnDefectMapper;
import com.jn.erp.production.defect.mapper.JnReworkOrderMapper;
import com.jn.erp.production.defect.service.IJnReworkOrderService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class JnReworkOrderServiceImpl extends ServiceImpl<JnReworkOrderMapper, JnReworkOrder> implements IJnReworkOrderService {

    @Autowired
    private JnReworkOrderMapper reworkOrderMapper;

    @Autowired
    private JnDefectMapper defectMapper;

    @Override
    public List<JnReworkOrder> selectList(JnReworkOrder query) {
        LambdaQueryWrapper<JnReworkOrder> wrapper = new LambdaQueryWrapper<>();
        if (query.getReworkNo() != null && !query.getReworkNo().isEmpty()) {
            wrapper.like(JnReworkOrder::getReworkNo, query.getReworkNo());
        }
        if (query.getOrderId() != null) {
            wrapper.eq(JnReworkOrder::getOrderId, query.getOrderId());
        }
        if (query.getOrderNo() != null && !query.getOrderNo().isEmpty()) {
            wrapper.like(JnReworkOrder::getOrderNo, query.getOrderNo());
        }
        if (query.getDefectId() != null) {
            wrapper.eq(JnReworkOrder::getDefectId, query.getDefectId());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(JnReworkOrder::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(JnReworkOrder::getCreateTime);
        return reworkOrderMapper.selectList(wrapper);
    }

    @Override
    public JnReworkOrder selectById(Long id) {
        return reworkOrderMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnReworkOrder rework) {
        rework.setReworkNo(generateReworkNo());
        if (rework.getStatus() == null) {
            rework.setStatus("PENDING");
        }
        rework.setCreateBy(SecurityUtils.getUsername());
        return reworkOrderMapper.insert(rework);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnReworkOrder rework) {
        rework.setUpdateBy(SecurityUtils.getUsername());
        return reworkOrderMapper.updateById(rework);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] ids) {
        int count = 0;
        for (Long id : ids) {
            count += reworkOrderMapper.deleteById(id);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JnReworkOrder createFromDefect(Long defectId) {
        JnDefect defect = defectMapper.selectById(defectId);
        if (defect == null) {
            throw new RuntimeException("不良品记录不存在: " + defectId);
        }
        JnReworkOrder rework = new JnReworkOrder();
        rework.setReworkNo(generateReworkNo());
        rework.setDefectId(defectId);
        rework.setOrderId(defect.getOrderId());
        rework.setOrderNo(defect.getOrderNo());
        rework.setOperationId(defect.getOperationId());
        rework.setOriginOperationId(defect.getOperationId());
        rework.setOriginOperationName(defect.getOperationName());
        rework.setReworkQty(defect.getQty());
        rework.setStatus("PENDING");
        rework.setCreateBy(SecurityUtils.getUsername());
        reworkOrderMapper.insert(rework);
        return rework;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void start(Long reworkId) {
        JnReworkOrder rework = reworkOrderMapper.selectById(reworkId);
        if (rework == null) {
            throw new RuntimeException("返工单不存在: " + reworkId);
        }
        rework.setStatus("IN_PROGRESS");
        rework.setStartTime(LocalDateTime.now());
        rework.setUpdateBy(SecurityUtils.getUsername());
        reworkOrderMapper.updateById(rework);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(Long reworkId, Integer actualQty) {
        JnReworkOrder rework = reworkOrderMapper.selectById(reworkId);
        if (rework == null) {
            throw new RuntimeException("返工单不存在: " + reworkId);
        }
        rework.setStatus("COMPLETED");
        rework.setEndTime(LocalDateTime.now());
        if (actualQty != null) {
            rework.setReworkQty(actualQty);
        }
        rework.setUpdateBy(SecurityUtils.getUsername());
        reworkOrderMapper.updateById(rework);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long reworkId, String reason) {
        JnReworkOrder rework = reworkOrderMapper.selectById(reworkId);
        if (rework == null) {
            throw new RuntimeException("返工单不存在: " + reworkId);
        }
        rework.setStatus("CANCELLED");
        String existingRemark = rework.getRemark();
        if (existingRemark != null && !existingRemark.isEmpty()) {
            rework.setRemark(existingRemark + "; " + reason);
        } else {
            rework.setRemark(reason);
        }
        rework.setUpdateBy(SecurityUtils.getUsername());
        reworkOrderMapper.updateById(rework);
    }

    private String generateReworkNo() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "RW-" + datePart + "-";
        LambdaQueryWrapper<JnReworkOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(JnReworkOrder::getReworkNo, prefix);
        wrapper.orderByDesc(JnReworkOrder::getReworkNo);
        wrapper.last("LIMIT 1");
        JnReworkOrder last = reworkOrderMapper.selectOne(wrapper);
        int seq = 1;
        if (last != null && last.getReworkNo() != null) {
            String lastCode = last.getReworkNo();
            String seqStr = lastCode.substring(lastCode.lastIndexOf("-") + 1);
            try {
                seq = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException e) {
                seq = 1;
            }
        }
        return prefix + String.format("%04d", seq);
    }
}
