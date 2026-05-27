package com.jn.erp.production.monitor.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/erp/monitor")
public class JnHealthController extends BaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/health")
    public AjaxResult health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "jn-erp-production");
        status.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return success(status);
    }

    @GetMapping("/health/db")
    public AjaxResult dbHealth() {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer dbStatus = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            result.put("status", dbStatus != null && dbStatus == 1 ? "UP" : "DOWN");
            result.put("database", "jn_erp");
        } catch (Exception e) {
            result.put("status", "DOWN");
            result.put("error", e.getMessage());
        }
        return success(result);
    }

    @GetMapping("/stats/summary")
    public AjaxResult summary() {
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("materialCount", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM jn_material WHERE del_flag = '0'", Integer.class));
        } catch (Exception e) { stats.put("materialCount", 0); }
        try {
            stats.put("customerCount", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM jn_customer WHERE del_flag = '0'", Integer.class));
        } catch (Exception e) { stats.put("customerCount", 0); }
        try {
            stats.put("supplierCount", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM jn_supplier WHERE del_flag = '0'", Integer.class));
        } catch (Exception e) { stats.put("supplierCount", 0); }
        try {
            stats.put("workOrderCount", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM jn_work_order", Integer.class));
        } catch (Exception e) { stats.put("workOrderCount", 0); }
        try {
            stats.put("inventoryCount", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM jn_inventory", Integer.class));
        } catch (Exception e) { stats.put("inventoryCount", 0); }
        try {
            stats.put("bomCount", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM jn_bom WHERE del_flag = '0'", Integer.class));
        } catch (Exception e) { stats.put("bomCount", 0); }
        return success(stats);
    }

    @GetMapping("/ops/daily")
    public AjaxResult dailyOps() {
        Map<String, Object> ops = new HashMap<>();
        try {
            ops.put("todayWorkOrders", jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM jn_work_order WHERE DATE(create_time) = CURDATE()", Integer.class));
        } catch (Exception e) { ops.put("todayWorkOrders", 0); }
        try {
            ops.put("todayReports", jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM jn_work_report WHERE DATE(create_time) = CURDATE()", Integer.class));
        } catch (Exception e) { ops.put("todayReports", 0); }
        try {
            ops.put("todayInspections", jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM jn_inspection WHERE DATE(create_time) = CURDATE()", Integer.class));
        } catch (Exception e) { ops.put("todayInspections", 0); }
        try {
            ops.put("todayHandovers", jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM jn_handover WHERE DATE(create_time) = CURDATE()", Integer.class));
        } catch (Exception e) { ops.put("todayHandovers", 0); }
        return success(ops);
    }

    @GetMapping("/logs/errors")
    public AjaxResult recentErrors() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "请查看日志文件: /var/log/jn-erp/");
        result.put("logPath", "/var/log/jn-erp/");
        return success(result);
    }
}
