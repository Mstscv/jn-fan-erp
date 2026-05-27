package com.jn.erp.finance.costing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.finance.costing.domain.JnWorkOrderCost;
import com.jn.erp.finance.costing.mapper.JnWorkOrderCostMapper;
import com.jn.erp.finance.costing.service.IJnCostingService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class JnCostingServiceImpl extends ServiceImpl<JnWorkOrderCostMapper, JnWorkOrderCost> implements IJnCostingService {

    @Autowired
    private JnWorkOrderCostMapper costMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int calculateWorkOrderCost(Long orderId) {
        Map<String, Object> orderInfo = jdbcTemplate.queryForMap(
                "SELECT order_no, product_id, product_name, quantity FROM jn_work_order WHERE order_id = ?",
                orderId);

        String orderNo = (String) orderInfo.get("order_no");
        Long productId = ((Number) orderInfo.get("product_id")).longValue();
        String productName = (String) orderInfo.get("product_name");
        Integer quantity = ((Number) orderInfo.get("quantity")).intValue();

        BigDecimal materialCost = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(wm.required_qty * m.unit_price), 0) FROM jn_work_order_material wm " +
                        "LEFT JOIN jn_material m ON wm.material_id = m.material_id WHERE wm.order_id = ?",
                BigDecimal.class, orderId);
        if (materialCost == null) {
            materialCost = BigDecimal.ZERO;
        }

        Number totalMinutes = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(wr.duration_minutes), 0) FROM jn_work_report wr " +
                        "WHERE wr.order_id = ? AND wr.status = 'APPROVED'",
                Number.class, orderId);
        BigDecimal laborCost = BigDecimal.valueOf((totalMinutes != null ? totalMinutes.doubleValue() : 0) * 0.5);

        BigDecimal overheadCost = materialCost.add(laborCost).multiply(new BigDecimal("0.15"))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalCost = materialCost.add(laborCost).add(overheadCost);
        BigDecimal unitCost = quantity > 0
                ? totalCost.divide(BigDecimal.valueOf(quantity), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        JnWorkOrderCost cost = new JnWorkOrderCost();
        cost.setOrderId(orderId);
        cost.setOrderNo(orderNo);
        cost.setProductId(productId);
        cost.setProductName(productName);
        cost.setProductQty(quantity);
        cost.setMaterialCost(materialCost.setScale(2, RoundingMode.HALF_UP));
        cost.setLaborCost(laborCost.setScale(2, RoundingMode.HALF_UP));
        cost.setOverheadCost(overheadCost);
        cost.setTotalCost(totalCost);
        cost.setUnitCost(unitCost);
        cost.setCostDate(LocalDate.now());
        cost.setStatus("CALCULATED");
        cost.setCreateBy(SecurityUtils.getUsername());

        return costMapper.insert(cost);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchCalculateWorkOrderCosts(List<Long> orderIds) {
        int count = 0;
        for (Long orderId : orderIds) {
            count += calculateWorkOrderCost(orderId);
        }
        return count;
    }

    @Override
    public int calculateProductCost(Long productId) {
        LambdaQueryWrapper<JnWorkOrderCost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JnWorkOrderCost::getProductId, productId)
                .in(JnWorkOrderCost::getStatus, "CALCULATED", "APPROVED");
        List<JnWorkOrderCost> costs = costMapper.selectList(wrapper);

        if (costs.isEmpty()) {
            return 0;
        }

        BigDecimal totalMaterialCost = BigDecimal.ZERO;
        BigDecimal totalLaborCost = BigDecimal.ZERO;
        BigDecimal totalOverheadCost = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        int totalQty = 0;

        for (JnWorkOrderCost c : costs) {
            totalMaterialCost = totalMaterialCost.add(c.getMaterialCost() != null ? c.getMaterialCost() : BigDecimal.ZERO);
            totalLaborCost = totalLaborCost.add(c.getLaborCost() != null ? c.getLaborCost() : BigDecimal.ZERO);
            totalOverheadCost = totalOverheadCost.add(c.getOverheadCost() != null ? c.getOverheadCost() : BigDecimal.ZERO);
            totalCost = totalCost.add(c.getTotalCost() != null ? c.getTotalCost() : BigDecimal.ZERO);
            totalQty += c.getProductQty() != null ? c.getProductQty() : 0;
        }

        String productName = costs.get(0).getProductName();

        JnWorkOrderCost summary = new JnWorkOrderCost();
        summary.setProductId(productId);
        summary.setProductName(productName);
        summary.setProductQty(totalQty);
        summary.setMaterialCost(totalMaterialCost);
        summary.setLaborCost(totalLaborCost);
        summary.setOverheadCost(totalOverheadCost);
        summary.setTotalCost(totalCost);
        summary.setUnitCost(totalQty > 0 ? totalCost.divide(BigDecimal.valueOf(totalQty), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        summary.setCostDate(LocalDate.now());
        summary.setStatus("CALCULATED");
        summary.setCreateBy(SecurityUtils.getUsername());

        return costMapper.insert(summary);
    }

    @Override
    public List<Map<String, Object>> getProductCostSummary(Long productId, LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT product_id, product_name, SUM(product_qty) AS total_qty, ")
                .append("SUM(material_cost) AS total_material_cost, ")
                .append("SUM(labor_cost) AS total_labor_cost, ")
                .append("SUM(overhead_cost) AS total_overhead_cost, ")
                .append("SUM(total_cost) AS total_cost, ")
                .append("CASE WHEN SUM(product_qty) > 0 ")
                .append("THEN SUM(total_cost) / SUM(product_qty) ELSE 0 END AS avg_unit_cost ")
                .append("FROM jn_work_order_cost WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (productId != null) {
            sql.append("AND product_id = ? ");
            params.add(productId);
        }
        if (startDate != null) {
            sql.append("AND cost_date >= ? ");
            params.add(startDate);
        }
        if (endDate != null) {
            sql.append("AND cost_date <= ? ");
            params.add(endDate);
        }

        sql.append("AND status IN ('CALCULATED', 'APPROVED') ");
        sql.append("GROUP BY product_id, product_name");

        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    @Override
    public JnWorkOrderCost selectById(Long id) {
        return costMapper.selectById(id);
    }

    @Override
    public List<JnWorkOrderCost> selectList(JnWorkOrderCost query) {
        LambdaQueryWrapper<JnWorkOrderCost> wrapper = new LambdaQueryWrapper<>();
        if (query.getOrderId() != null) {
            wrapper.eq(JnWorkOrderCost::getOrderId, query.getOrderId());
        }
        if (query.getOrderNo() != null && !query.getOrderNo().isEmpty()) {
            wrapper.like(JnWorkOrderCost::getOrderNo, query.getOrderNo());
        }
        if (query.getProductId() != null) {
            wrapper.eq(JnWorkOrderCost::getProductId, query.getProductId());
        }
        if (query.getProductName() != null && !query.getProductName().isEmpty()) {
            wrapper.like(JnWorkOrderCost::getProductName, query.getProductName());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(JnWorkOrderCost::getStatus, query.getStatus());
        }
        if (query.getCostDate() != null) {
            wrapper.ge(JnWorkOrderCost::getCostDate, query.getCostDate());
        }
        wrapper.orderByDesc(JnWorkOrderCost::getCostDate);
        return costMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approve(Long costId) {
        JnWorkOrderCost cost = costMapper.selectById(costId);
        if (cost == null) {
            return 0;
        }
        cost.setStatus("APPROVED");
        cost.setUpdateBy(SecurityUtils.getUsername());
        return costMapper.updateById(cost);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] ids) {
        int count = 0;
        for (Long id : ids) {
            count += costMapper.deleteById(id);
        }
        return count;
    }
}
