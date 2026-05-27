package com.jn.erp.finance.report.controller;

import com.jn.erp.finance.costing.mapper.JnWorkOrderCostMapper;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/erp/reports")
public class JnReportController extends BaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JnWorkOrderCostMapper workOrderCostMapper;

    @RequiresPermissions("erp:reports:salesAnalysis")
    @GetMapping("/sales-analysis")
    public AjaxResult salesAnalysis(@RequestParam(required = false) LocalDate startDate,
                                    @RequestParam(required = false) LocalDate endDate,
                                    @RequestParam(required = false) Long customerId,
                                    @RequestParam(required = false, defaultValue = "MONTH") String groupBy) {
        try {
            StringBuilder sql = new StringBuilder();
            List<Object> params = new ArrayList<>();

            if ("CUSTOMER".equalsIgnoreCase(groupBy)) {
                sql.append("SELECT so.customer_id, so.customer_name, ");
                sql.append("COUNT(DISTINCT so.order_id) as orderCount, ");
                sql.append("COALESCE(SUM(sol.total_amount), 0) as totalAmount, ");
                sql.append("COALESCE(SUM(sol.quantity), 0) as totalQty ");
                sql.append("FROM jn_sales_order so ");
                sql.append("JOIN jn_sales_order_line sol ON so.order_id = sol.order_id ");
                sql.append("WHERE so.order_date BETWEEN ? AND ? ");
                if (customerId != null) {
                    sql.append("AND so.customer_id = ? ");
                }
                sql.append("GROUP BY so.customer_id, so.customer_name ");
                sql.append("ORDER BY totalAmount DESC");
            } else if ("PRODUCT".equalsIgnoreCase(groupBy)) {
                sql.append("SELECT sol.product_id, sol.product_name, ");
                sql.append("COUNT(DISTINCT so.order_id) as orderCount, ");
                sql.append("COALESCE(SUM(sol.total_amount), 0) as totalAmount, ");
                sql.append("COALESCE(SUM(sol.quantity), 0) as totalQty ");
                sql.append("FROM jn_sales_order so ");
                sql.append("JOIN jn_sales_order_line sol ON so.order_id = sol.order_id ");
                sql.append("WHERE so.order_date BETWEEN ? AND ? ");
                if (customerId != null) {
                    sql.append("AND so.customer_id = ? ");
                }
                sql.append("GROUP BY sol.product_id, sol.product_name ");
                sql.append("ORDER BY totalAmount DESC");
            } else {
                sql.append("SELECT DATE_FORMAT(so.order_date, '%Y-%m') as period, ");
                sql.append("COUNT(DISTINCT so.order_id) as orderCount, ");
                sql.append("COALESCE(SUM(sol.total_amount), 0) as totalAmount, ");
                sql.append("COALESCE(SUM(sol.quantity), 0) as totalQty ");
                sql.append("FROM jn_sales_order so ");
                sql.append("JOIN jn_sales_order_line sol ON so.order_id = sol.order_id ");
                sql.append("WHERE so.order_date BETWEEN ? AND ? ");
                if (customerId != null) {
                    sql.append("AND so.customer_id = ? ");
                }
                sql.append("GROUP BY period ");
                sql.append("ORDER BY period ASC");
            }

            LocalDate start = startDate != null ? startDate : LocalDate.of(2000, 1, 1);
            LocalDate end = endDate != null ? endDate : LocalDate.now();
            params.add(java.sql.Date.valueOf(start));
            params.add(java.sql.Date.valueOf(end));
            if (customerId != null) {
                params.add(customerId);
            }

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), params.toArray());

            BigDecimal totalAmountSum = BigDecimal.ZERO;
            long totalOrderCount = 0;
            BigDecimal totalQtySum = BigDecimal.ZERO;

            for (Map<String, Object> row : rows) {
                Object amountObj = row.get("totalAmount");
                if (amountObj instanceof Number) {
                    totalAmountSum = totalAmountSum.add(new BigDecimal(((Number) amountObj).toString()));
                }
                Object countObj = row.get("orderCount");
                if (countObj instanceof Number) {
                    totalOrderCount += ((Number) countObj).longValue();
                }
                Object qtyObj = row.get("totalQty");
                if (qtyObj instanceof Number) {
                    totalQtySum = totalQtySum.add(new BigDecimal(((Number) qtyObj).toString()));
                }
            }

            Map<String, Object> totalRow = new HashMap<>();
            if ("CUSTOMER".equalsIgnoreCase(groupBy)) {
                totalRow.put("customerName", "合计");
            } else if ("PRODUCT".equalsIgnoreCase(groupBy)) {
                totalRow.put("productName", "合计");
            } else {
                totalRow.put("period", "合计");
            }
            totalRow.put("orderCount", totalOrderCount);
            totalRow.put("totalAmount", totalAmountSum);
            totalRow.put("totalQty", totalQtySum);

            Map<String, Object> result = new HashMap<>();
            result.put("rows", rows);
            result.put("total", totalRow);

            return success(result);
        } catch (DataAccessException e) {
            logger.error("销售分析报表查询失败", e);
            Map<String, Object> empty = new HashMap<>();
            empty.put("rows", new ArrayList<>());
            empty.put("total", new HashMap<>());
            return success(empty);
        }
    }

    @RequiresPermissions("erp:reports:inventoryAnalysis")
    @GetMapping("/inventory-analysis")
    public AjaxResult inventoryAnalysis(@RequestParam(required = false, defaultValue = "6") Integer months) {
        try {
            String inventorySql = "SELECT i.material_id, m.material_code, m.material_name, m.unit, " +
                    "i.quantity, m.unit_price, (i.quantity * m.unit_price) as stock_value " +
                    "FROM jn_inventory i " +
                    "JOIN jn_material m ON i.material_id = m.material_id " +
                    "WHERE i.quantity > 0 " +
                    "ORDER BY stock_value DESC";

            List<Map<String, Object>> inventoryList = jdbcTemplate.queryForList(inventorySql);

            String consumptionSql = "SELECT il.material_id, SUM(il.qty) as consumed_qty " +
                    "FROM jn_inventory_log il " +
                    "WHERE il.change_type = 'OUT' " +
                    "AND il.create_time >= DATE_SUB(NOW(), INTERVAL ? MONTH) " +
                    "GROUP BY il.material_id";

            List<Map<String, Object>> consumptionList = jdbcTemplate.queryForList(consumptionSql, months);

            Map<Object, Map<String, Object>> consumptionMap = new HashMap<>();
            for (Map<String, Object> row : consumptionList) {
                consumptionMap.put(row.get("material_id"), row);
            }

            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Map<String, Object> inv : inventoryList) {
                Map<String, Object> item = new HashMap<>();
                item.put("materialId", inv.get("material_id"));
                item.put("materialCode", inv.get("material_code"));
                item.put("materialName", inv.get("material_name"));
                item.put("stockQty", inv.get("quantity"));

                Object stockValue = inv.get("stock_value");
                item.put("stockValue", stockValue != null ? stockValue : BigDecimal.ZERO);

                Object materialId = inv.get("material_id");
                Map<String, Object> consumption = consumptionMap.get(materialId);
                BigDecimal consumedQty = BigDecimal.ZERO;
                if (consumption != null && consumption.get("consumed_qty") != null) {
                    consumedQty = new BigDecimal(((Number) consumption.get("consumed_qty")).toString());
                }
                item.put("consumedQty", consumedQty);

                BigDecimal stockQty = inv.get("quantity") != null ? new BigDecimal(((Number) inv.get("quantity")).toString()) : BigDecimal.ZERO;
                BigDecimal avgStock = stockQty.divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_HALF_UP);
                item.put("avgStock", avgStock);

                BigDecimal turnoverRate = BigDecimal.ZERO;
                if (avgStock.compareTo(BigDecimal.ZERO) > 0) {
                    turnoverRate = consumedQty.divide(avgStock, 4, BigDecimal.ROUND_HALF_UP);
                }
                item.put("turnoverRate", turnoverRate);

                boolean slowMoving = turnoverRate.compareTo(new BigDecimal("0.1")) < 0;
                item.put("slowMoving", slowMoving ? "Y" : "N");

                resultList.add(item);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("rows", resultList);
            result.put("total", resultList.size());

            return success(result);
        } catch (DataAccessException e) {
            logger.error("库存周转分析查询失败", e);
            Map<String, Object> empty = new HashMap<>();
            empty.put("rows", new ArrayList<>());
            empty.put("total", 0);
            return success(empty);
        }
    }

    @RequiresPermissions("erp:reports:dashboard")
    @GetMapping("/business-dashboard")
    public AjaxResult businessDashboard() {
        Map<String, Object> result = new HashMap<>();

        try {
            String todaySalesSql = "SELECT COALESCE(SUM(total_amount), 0) FROM jn_sales_order WHERE DATE(order_date) = CURDATE()";
            BigDecimal todaySales = jdbcTemplate.queryForObject(todaySalesSql, BigDecimal.class);
            result.put("todaySales", todaySales != null ? todaySales : BigDecimal.ZERO);
        } catch (DataAccessException e) {
            result.put("todaySales", BigDecimal.ZERO);
        }

        try {
            String monthSalesSql = "SELECT COALESCE(SUM(total_amount), 0) FROM jn_sales_order WHERE MONTH(order_date) = MONTH(CURDATE()) AND YEAR(order_date) = YEAR(CURDATE())";
            BigDecimal monthSales = jdbcTemplate.queryForObject(monthSalesSql, BigDecimal.class);
            result.put("monthSales", monthSales != null ? monthSales : BigDecimal.ZERO);
        } catch (DataAccessException e) {
            result.put("monthSales", BigDecimal.ZERO);
        }

        try {
            String monthReceiptSql = "SELECT COALESCE(SUM(amount), 0) FROM jn_receipt_payment WHERE slip_type = 'RECEIPT' AND MONTH(transaction_date) = MONTH(CURDATE()) AND YEAR(transaction_date) = YEAR(CURDATE()) AND status = 'APPROVED'";
            BigDecimal monthReceipt = jdbcTemplate.queryForObject(monthReceiptSql, BigDecimal.class);
            result.put("monthReceipt", monthReceipt != null ? monthReceipt : BigDecimal.ZERO);
        } catch (DataAccessException e) {
            result.put("monthReceipt", BigDecimal.ZERO);
        }

        try {
            String monthPayableSql = "SELECT COALESCE(SUM(amount), 0) FROM jn_receipt_payment WHERE slip_type = 'PAYMENT' AND MONTH(transaction_date) = MONTH(CURDATE()) AND YEAR(transaction_date) = YEAR(CURDATE()) AND status = 'APPROVED'";
            BigDecimal monthPayable = jdbcTemplate.queryForObject(monthPayableSql, BigDecimal.class);
            result.put("monthPayable", monthPayable != null ? monthPayable : BigDecimal.ZERO);
        } catch (DataAccessException e) {
            result.put("monthPayable", BigDecimal.ZERO);
        }

        try {
            String pendingOrdersSql = "SELECT COUNT(*) FROM jn_work_order WHERE status = 'PENDING'";
            Integer pendingOrders = jdbcTemplate.queryForObject(pendingOrdersSql, Integer.class);
            result.put("pendingOrders", pendingOrders != null ? pendingOrders : 0);
        } catch (DataAccessException e) {
            result.put("pendingOrders", 0);
        }

        try {
            String inProductionSql = "SELECT COUNT(*) FROM jn_work_order WHERE status = 'IN_PROGRESS'";
            Integer inProduction = jdbcTemplate.queryForObject(inProductionSql, Integer.class);
            result.put("inProduction", inProduction != null ? inProduction : 0);
        } catch (DataAccessException e) {
            result.put("inProduction", 0);
        }

        try {
            String completedTodaySql = "SELECT COUNT(*) FROM jn_work_order WHERE status = 'COMPLETED' AND DATE(actual_end) = CURDATE()";
            Integer completedToday = jdbcTemplate.queryForObject(completedTodaySql, Integer.class);
            result.put("completedToday", completedToday != null ? completedToday : 0);
        } catch (DataAccessException e) {
            result.put("completedToday", 0);
        }

        try {
            String monthOutputSql = "SELECT COALESCE(SUM(output_qty), 0) FROM jn_work_report WHERE MONTH(create_time) = MONTH(CURDATE()) AND YEAR(create_time) = YEAR(CURDATE()) AND status = 'APPROVED'";
            BigDecimal monthOutput = jdbcTemplate.queryForObject(monthOutputSql, BigDecimal.class);
            result.put("monthOutput", monthOutput != null ? monthOutput : BigDecimal.ZERO);
        } catch (DataAccessException e) {
            result.put("monthOutput", BigDecimal.ZERO);
        }

        try {
            String qualityRateSql = "SELECT CASE WHEN SUM(output_qty) > 0 THEN (SUM(good_qty) * 100.0 / SUM(output_qty)) ELSE 0 END FROM jn_work_report WHERE status = 'APPROVED'";
            BigDecimal qualityRate = jdbcTemplate.queryForObject(qualityRateSql, BigDecimal.class);
            result.put("qualityRate", qualityRate != null ? qualityRate : BigDecimal.ZERO);
        } catch (DataAccessException e) {
            result.put("qualityRate", BigDecimal.ZERO);
        }

        try {
            String arBalanceSql = "SELECT COALESCE(SUM(balance_amount), 0) FROM jn_receivable WHERE status IN ('PENDING', 'PARTIAL')";
            BigDecimal arBalance = jdbcTemplate.queryForObject(arBalanceSql, BigDecimal.class);
            result.put("arBalance", arBalance != null ? arBalance : BigDecimal.ZERO);
        } catch (DataAccessException e) {
            result.put("arBalance", BigDecimal.ZERO);
        }

        try {
            String apBalanceSql = "SELECT COALESCE(SUM(balance_amount), 0) FROM jn_payable WHERE status IN ('PENDING', 'PARTIAL')";
            BigDecimal apBalance = jdbcTemplate.queryForObject(apBalanceSql, BigDecimal.class);
            result.put("apBalance", apBalance != null ? apBalance : BigDecimal.ZERO);
        } catch (DataAccessException e) {
            result.put("apBalance", BigDecimal.ZERO);
        }

        return success(result);
    }

}
