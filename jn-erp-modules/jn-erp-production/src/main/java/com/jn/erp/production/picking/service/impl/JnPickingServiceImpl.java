package com.jn.erp.production.picking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.picking.domain.JnPicking;
import com.jn.erp.production.picking.domain.JnPickingLine;
import com.jn.erp.production.picking.mapper.JnPickingLineMapper;
import com.jn.erp.production.picking.mapper.JnPickingMapper;
import com.jn.erp.production.picking.service.IJnPickingService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class JnPickingServiceImpl extends ServiceImpl<JnPickingMapper, JnPicking> implements IJnPickingService {

    @Autowired
    private JnPickingMapper pickingMapper;

    @Autowired
    private JnPickingLineMapper pickingLineMapper;

    @Override
    public List<JnPicking> selectList(JnPicking query) {
        LambdaQueryWrapper<JnPicking> wrapper = new LambdaQueryWrapper<>();
        if (query.getPickingNo() != null && !query.getPickingNo().isEmpty()) {
            wrapper.like(JnPicking::getPickingNo, query.getPickingNo());
        }
        if (query.getOrderNo() != null && !query.getOrderNo().isEmpty()) {
            wrapper.like(JnPicking::getOrderNo, query.getOrderNo());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(JnPicking::getStatus, query.getStatus());
        }
        if (query.getPickingType() != null && !query.getPickingType().isEmpty()) {
            wrapper.eq(JnPicking::getPickingType, query.getPickingType());
        }
        wrapper.orderByDesc(JnPicking::getCreateTime);
        return pickingMapper.selectList(wrapper);
    }

    @Override
    public JnPicking selectById(Long id) {
        return pickingMapper.selectById(id);
    }

    @Override
    public JnPicking selectWithLines(Long id) {
        JnPicking picking = pickingMapper.selectById(id);
        if (picking != null) {
            List<JnPickingLine> lines = pickingLineMapper.selectByPickingId(id);
            picking.setLines(lines);
        }
        return picking;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnPicking picking, List<JnPickingLine> lines) {
        picking.setPickingNo(generatePickingNo());
        if (picking.getStatus() == null) {
            picking.setStatus("DRAFT");
        }
        if (picking.getPickingType() == null) {
            picking.setPickingType("STANDARD");
        }
        picking.setApplyBy(SecurityUtils.getUsername());
        picking.setApplyDate(LocalDate.now());
        picking.setCreateBy(SecurityUtils.getUsername());
        int result = pickingMapper.insert(picking);

        if (lines != null && !lines.isEmpty()) {
            for (JnPickingLine line : lines) {
                line.setPickingId(picking.getPickingId());
                if (line.getActualQty() == null) {
                    line.setActualQty(java.math.BigDecimal.ZERO);
                }
                pickingLineMapper.insert(line);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnPicking picking, List<JnPickingLine> lines) {
        picking.setUpdateBy(SecurityUtils.getUsername());
        int result = pickingMapper.updateById(picking);

        pickingLineMapper.deleteByPickingId(picking.getPickingId());

        if (lines != null && !lines.isEmpty()) {
            for (JnPickingLine line : lines) {
                line.setLineId(null);
                line.setPickingId(picking.getPickingId());
                if (line.getActualQty() == null) {
                    line.setActualQty(java.math.BigDecimal.ZERO);
                }
                pickingLineMapper.insert(line);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] ids) {
        int count = 0;
        for (Long id : ids) {
            pickingLineMapper.deleteByPickingId(id);
            count += pickingMapper.deleteById(id);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long pickingId) {
        JnPicking picking = pickingMapper.selectById(pickingId);
        if (picking == null) {
            throw new RuntimeException("领料单不存在: " + pickingId);
        }
        picking.setStatus("APPROVED");
        picking.setApproveBy(SecurityUtils.getUsername());
        picking.setApproveTime(LocalDateTime.now());
        picking.setUpdateBy(SecurityUtils.getUsername());
        pickingMapper.updateById(picking);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long pickingId) {
        JnPicking picking = pickingMapper.selectById(pickingId);
        if (picking == null) {
            throw new RuntimeException("领料单不存在: " + pickingId);
        }
        picking.setStatus("CANCELLED");
        picking.setUpdateBy(SecurityUtils.getUsername());
        pickingMapper.updateById(picking);
    }

    private String generatePickingNo() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "PK-" + datePart + "-";
        LambdaQueryWrapper<JnPicking> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(JnPicking::getPickingNo, prefix);
        wrapper.orderByDesc(JnPicking::getPickingNo);
        wrapper.last("LIMIT 1");
        JnPicking last = pickingMapper.selectOne(wrapper);
        int seq = 1;
        if (last != null && last.getPickingNo() != null) {
            String lastCode = last.getPickingNo();
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
