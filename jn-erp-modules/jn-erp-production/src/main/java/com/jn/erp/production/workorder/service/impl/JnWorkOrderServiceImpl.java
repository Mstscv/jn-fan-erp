package com.jn.erp.production.workorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.workorder.domain.JnWorkOrder;
import com.jn.erp.production.workorder.domain.JnWorkOrderLine;
import com.jn.erp.production.workorder.mapper.JnWorkOrderLineMapper;
import com.jn.erp.production.workorder.mapper.JnWorkOrderMapper;
import com.jn.erp.production.workorder.service.IJnWorkOrderService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JnWorkOrderServiceImpl extends ServiceImpl<JnWorkOrderMapper, JnWorkOrder> implements IJnWorkOrderService {

    @Autowired
    private JnWorkOrderMapper workOrderMapper;

    @Autowired
    private JnWorkOrderLineMapper workOrderLineMapper;

    @Override
    public List<JnWorkOrder> selectList(JnWorkOrder query) {
        LambdaQueryWrapper<JnWorkOrder> wrapper = new LambdaQueryWrapper<>();
        if (query.getOrderNo() != null && !query.getOrderNo().isEmpty()) {
            wrapper.like(JnWorkOrder::getOrderNo, query.getOrderNo());
        }
        if (query.getProductName() != null && !query.getProductName().isEmpty()) {
            wrapper.like(JnWorkOrder::getProductName, query.getProductName());
        }
        if (query.getCustomerName() != null && !query.getCustomerName().isEmpty()) {
            wrapper.like(JnWorkOrder::getCustomerName, query.getCustomerName());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(JnWorkOrder::getStatus, query.getStatus());
        }
        if (query.getPlannedStart() != null) {
            wrapper.ge(JnWorkOrder::getPlannedStart, query.getPlannedStart());
        }
        if (query.getPlannedEnd() != null) {
            wrapper.le(JnWorkOrder::getPlannedEnd, query.getPlannedEnd());
        }
        wrapper.orderByDesc(JnWorkOrder::getCreateTime);
        return workOrderMapper.selectList(wrapper);
    }

    @Override
    public JnWorkOrder selectById(Long id) {
        return workOrderMapper.selectById(id);
    }

    @Override
    public JnWorkOrder selectWithLines(Long id) {
        JnWorkOrder order = workOrderMapper.selectById(id);
        if (order != null) {
            List<JnWorkOrderLine> lines = workOrderLineMapper.selectByOrderId(id);
            order.setParams(new HashMap<>());
            order.getParams().put("lines", lines);
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnWorkOrder order, List<JnWorkOrderLine> lines) {
        order.setOrderNo(generateOrderNo());
        if (order.getPriority() == null) {
            order.setPriority("NORMAL");
        }
        if (order.getStatus() == null) {
            order.setStatus("PENDING");
        }
        order.setCreateBy(SecurityUtils.getUsername());
        int result = workOrderMapper.insert(order);

        if (lines != null && !lines.isEmpty()) {
            for (JnWorkOrderLine line : lines) {
                line.setOrderId(order.getOrderId());
                if (line.getSortOrder() == null) {
                    line.setSortOrder(0);
                }
                workOrderLineMapper.insert(line);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnWorkOrder order, List<JnWorkOrderLine> lines) {
        order.setUpdateBy(SecurityUtils.getUsername());
        int result = workOrderMapper.updateById(order);

        workOrderLineMapper.deleteByOrderId(order.getOrderId());

        if (lines != null && !lines.isEmpty()) {
            for (JnWorkOrderLine line : lines) {
                line.setLineId(null);
                line.setOrderId(order.getOrderId());
                if (line.getSortOrder() == null) {
                    line.setSortOrder(0);
                }
                workOrderLineMapper.insert(line);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] ids) {
        int count = 0;
        for (Long id : ids) {
            workOrderLineMapper.deleteByOrderId(id);
            count += workOrderMapper.deleteById(id);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long orderId, String newStatus) {
        JnWorkOrder order = workOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("工单不存在: " + orderId);
        }

        String currentStatus = order.getStatus();
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new RuntimeException("不允许的状态变更: " + currentStatus + " -> " + newStatus);
        }

        order.setStatus(newStatus);
        if ("IN_PROGRESS".equals(newStatus)) {
            order.setActualStart(LocalDateTime.now());
        }
        if ("COMPLETED".equals(newStatus)) {
            order.setActualEnd(LocalDateTime.now());
        }
        order.setUpdateBy(SecurityUtils.getUsername());
        workOrderMapper.updateById(order);
    }

    @Override
    public List<JnWorkOrder> getPendingScheduling() {
        return workOrderMapper.selectByStatus("PENDING");
    }

    @Override
    public Map<String, Object> getKanbanData() {
        Map<String, Object> data = new HashMap<>();

        LambdaQueryWrapper<JnWorkOrder> wrapper = new LambdaQueryWrapper<>();
        long pending = workOrderMapper.selectCount(wrapper.eq(JnWorkOrder::getStatus, "PENDING"));
        data.put("pending", pending);

        wrapper.clear();
        long scheduled = workOrderMapper.selectCount(wrapper.eq(JnWorkOrder::getStatus, "SCHEDULED"));
        data.put("scheduled", scheduled);

        wrapper.clear();
        long inProgress = workOrderMapper.selectCount(wrapper.eq(JnWorkOrder::getStatus, "IN_PROGRESS"));
        data.put("inProgress", inProgress);

        wrapper.clear();
        long completed = workOrderMapper.selectCount(wrapper.eq(JnWorkOrder::getStatus, "COMPLETED"));
        data.put("completed", completed);

        wrapper.clear();
        long cancelled = workOrderMapper.selectCount(wrapper.eq(JnWorkOrder::getStatus, "CANCELLED"));
        data.put("cancelled", cancelled);

        return data;
    }

    private String generateOrderNo() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "WO-" + datePart + "-";
        LambdaQueryWrapper<JnWorkOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(JnWorkOrder::getOrderNo, prefix);
        wrapper.orderByDesc(JnWorkOrder::getOrderNo);
        wrapper.last("LIMIT 1");
        JnWorkOrder last = workOrderMapper.selectOne(wrapper);
        int seq = 1;
        if (last != null && last.getOrderNo() != null) {
            String lastCode = last.getOrderNo();
            String seqStr = lastCode.substring(lastCode.lastIndexOf("-") + 1);
            try {
                seq = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException e) {
                seq = 1;
            }
        }
        return prefix + String.format("%04d", seq);
    }

    private boolean isValidTransition(String currentStatus, String newStatus) {
        if ("PENDING".equals(currentStatus)) {
            return "SCHEDULED".equals(newStatus) || "CANCELLED".equals(newStatus);
        }
        if ("SCHEDULED".equals(currentStatus)) {
            return "IN_PROGRESS".equals(newStatus) || "CANCELLED".equals(newStatus);
        }
        if ("IN_PROGRESS".equals(currentStatus)) {
            return "COMPLETED".equals(newStatus) || "CANCELLED".equals(newStatus);
        }
        if ("COMPLETED".equals(currentStatus)) {
            return "CLOSED".equals(newStatus);
        }
        return false;
    }

}
