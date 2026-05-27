package com.jn.erp.production.routing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.routing.domain.JnRouting;
import com.jn.erp.production.routing.domain.JnRoutingLine;
import com.jn.erp.production.routing.mapper.JnRoutingLineMapper;
import com.jn.erp.production.routing.mapper.JnRoutingMapper;
import com.jn.erp.production.routing.service.IJnRoutingService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class JnRoutingServiceImpl extends ServiceImpl<JnRoutingMapper, JnRouting> implements IJnRoutingService {

    @Autowired
    private JnRoutingMapper routingMapper;

    @Autowired
    private JnRoutingLineMapper routingLineMapper;

    @Override
    public List<JnRouting> selectList(JnRouting query) {
        LambdaQueryWrapper<JnRouting> wrapper = new LambdaQueryWrapper<>();
        if (query.getRoutingCode() != null && !query.getRoutingCode().isEmpty()) {
            wrapper.like(JnRouting::getRoutingCode, query.getRoutingCode());
        }
        if (query.getRoutingName() != null && !query.getRoutingName().isEmpty()) {
            wrapper.like(JnRouting::getRoutingName, query.getRoutingName());
        }
        if (query.getProductId() != null) {
            wrapper.eq(JnRouting::getProductId, query.getProductId());
        }
        if (query.getProductName() != null && !query.getProductName().isEmpty()) {
            wrapper.like(JnRouting::getProductName, query.getProductName());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(JnRouting::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(JnRouting::getCreateTime);
        return routingMapper.selectList(wrapper);
    }

    @Override
    public JnRouting selectById(Long id) {
        return routingMapper.selectById(id);
    }

    @Override
    public JnRouting selectWithLines(Long id) {
        JnRouting routing = routingMapper.selectById(id);
        if (routing != null) {
            List<JnRoutingLine> lines = routingLineMapper.selectByRoutingId(id);
            routing.setLines(lines);
        }
        return routing;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnRouting routing, List<JnRoutingLine> lines) {
        routing.setRoutingCode(generateRoutingCode());
        if (routing.getVersion() == null) {
            routing.setVersion("v1.0");
        }
        if (routing.getStatus() == null) {
            routing.setStatus("DRAFT");
        }
        if (routing.getIsActive() == null) {
            routing.setIsActive("Y");
        }
        routing.setCreateBy(SecurityUtils.getUsername());
        int result = routingMapper.insert(routing);

        if (lines != null && !lines.isEmpty()) {
            for (JnRoutingLine line : lines) {
                line.setRoutingId(routing.getRoutingId());
                if (line.getParallelFlag() == null) {
                    line.setParallelFlag("N");
                }
                if (line.getIsKeyOperation() == null) {
                    line.setIsKeyOperation("N");
                }
                routingLineMapper.insert(line);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnRouting routing, List<JnRoutingLine> lines) {
        routing.setUpdateBy(SecurityUtils.getUsername());
        int result = routingMapper.updateById(routing);

        routingLineMapper.deleteByRoutingId(routing.getRoutingId());

        if (lines != null && !lines.isEmpty()) {
            for (JnRoutingLine line : lines) {
                line.setLineId(null);
                line.setRoutingId(routing.getRoutingId());
                if (line.getParallelFlag() == null) {
                    line.setParallelFlag("N");
                }
                if (line.getIsKeyOperation() == null) {
                    line.setIsKeyOperation("N");
                }
                routingLineMapper.insert(line);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] ids) {
        for (Long id : ids) {
            routingLineMapper.deleteByRoutingId(id);
        }
        return routingMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long routingId, String newStatus) {
        JnRouting routing = routingMapper.selectById(routingId);
        if (routing == null) {
            throw new RuntimeException("工艺路线不存在: " + routingId);
        }

        String currentStatus = routing.getStatus();
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new RuntimeException("不允许的状态变更: " + currentStatus + " -> " + newStatus);
        }

        routing.setStatus(newStatus);
        if ("APPROVED".equals(newStatus)) {
            routing.setApproveBy(SecurityUtils.getUsername());
            routing.setApproveTime(LocalDateTime.now());
        }
        routing.setUpdateBy(SecurityUtils.getUsername());
        routingMapper.updateById(routing);
    }

    private boolean isValidTransition(String currentStatus, String newStatus) {
        if ("DRAFT".equals(currentStatus)) {
            return "APPROVED".equals(newStatus) || "DEPRECATED".equals(newStatus);
        }
        if ("APPROVED".equals(currentStatus)) {
            return "EFFECTIVE".equals(newStatus) || "DRAFT".equals(newStatus) || "DEPRECATED".equals(newStatus);
        }
        if ("EFFECTIVE".equals(currentStatus)) {
            return "DEPRECATED".equals(newStatus);
        }
        if ("DEPRECATED".equals(currentStatus)) {
            return "DRAFT".equals(newStatus);
        }
        return false;
    }

    private String generateRoutingCode() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "RT-" + datePart + "-";
        LambdaQueryWrapper<JnRouting> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(JnRouting::getRoutingCode, prefix);
        wrapper.orderByDesc(JnRouting::getRoutingCode);
        wrapper.last("LIMIT 1");
        JnRouting last = routingMapper.selectOne(wrapper);
        int seq = 1;
        if (last != null && last.getRoutingCode() != null) {
            String lastCode = last.getRoutingCode();
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
