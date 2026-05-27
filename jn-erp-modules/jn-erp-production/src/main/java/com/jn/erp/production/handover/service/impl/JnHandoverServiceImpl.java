package com.jn.erp.production.handover.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.handover.domain.JnHandover;
import com.jn.erp.production.handover.mapper.JnHandoverMapper;
import com.jn.erp.production.handover.service.IJnHandoverService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class JnHandoverServiceImpl extends ServiceImpl<JnHandoverMapper, JnHandover> implements IJnHandoverService {

    @Autowired
    private JnHandoverMapper handoverMapper;

    @Override
    public List<JnHandover> selectList(JnHandover query) {
        LambdaQueryWrapper<JnHandover> wrapper = new LambdaQueryWrapper<>();
        if (query.getHandoverNo() != null && !query.getHandoverNo().isEmpty()) {
            wrapper.like(JnHandover::getHandoverNo, query.getHandoverNo());
        }
        if (query.getOrderNo() != null && !query.getOrderNo().isEmpty()) {
            wrapper.like(JnHandover::getOrderNo, query.getOrderNo());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(JnHandover::getStatus, query.getStatus());
        }
        if (query.getFromOperationId() != null) {
            wrapper.eq(JnHandover::getFromOperationId, query.getFromOperationId());
        }
        if (query.getToOperationId() != null) {
            wrapper.eq(JnHandover::getToOperationId, query.getToOperationId());
        }
        wrapper.orderByDesc(JnHandover::getCreateTime);
        return handoverMapper.selectList(wrapper);
    }

    @Override
    public JnHandover selectById(Long id) {
        return handoverMapper.selectById(id);
    }

    @Override
    public List<JnHandover> selectByOrderId(Long orderId) {
        return handoverMapper.selectByOrderId(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnHandover handover) {
        handover.setHandoverNo(generateHandoverNo());
        if (handover.getStatus() == null) {
            handover.setStatus("PENDING");
        }
        if (handover.getDefectQty() == null) {
            handover.setDefectQty(0);
        }
        handover.setCreateBy(SecurityUtils.getUsername());
        return handoverMapper.insert(handover);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnHandover handover) {
        handover.setUpdateBy(SecurityUtils.getUsername());
        return handoverMapper.updateById(handover);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] ids) {
        int count = 0;
        for (Long id : ids) {
            count += handoverMapper.deleteById(id);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirm(Long handoverId, Integer actualQty, Integer defectQty) {
        JnHandover handover = handoverMapper.selectById(handoverId);
        if (handover == null) {
            throw new RuntimeException("交接单不存在: " + handoverId);
        }
        handover.setStatus("TRANSFERRED");
        handover.setQuantity(actualQty);
        handover.setDefectQty(defectQty != null ? defectQty : 0);
        handover.setUpdateBy(SecurityUtils.getUsername());
        handoverMapper.updateById(handover);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long handoverId, String reason) {
        JnHandover handover = handoverMapper.selectById(handoverId);
        if (handover == null) {
            throw new RuntimeException("交接单不存在: " + handoverId);
        }
        handover.setStatus("REJECTED");
        handover.setRemark(reason);
        handover.setUpdateBy(SecurityUtils.getUsername());
        handoverMapper.updateById(handover);
    }

    private String generateHandoverNo() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "HO-" + datePart + "-";
        LambdaQueryWrapper<JnHandover> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(JnHandover::getHandoverNo, prefix);
        wrapper.orderByDesc(JnHandover::getHandoverNo);
        wrapper.last("LIMIT 1");
        JnHandover last = handoverMapper.selectOne(wrapper);
        int seq = 1;
        if (last != null && last.getHandoverNo() != null) {
            String lastCode = last.getHandoverNo();
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
