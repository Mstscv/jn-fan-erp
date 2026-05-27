package com.jn.erp.common.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DataImportUtil {

    private static final Logger log = LoggerFactory.getLogger(DataImportUtil.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<String> validateRow(Map<String, Object> row, Map<String, String> fieldRules) {
        List<String> errors = new ArrayList<>();
        if (row == null || fieldRules == null) {
            errors.add("Row data or field rules cannot be null");
            return errors;
        }
        for (Map.Entry<String, String> rule : fieldRules.entrySet()) {
            String fieldName = rule.getKey();
            String ruleValue = rule.getValue();
            String[] parts = ruleValue.split("\\|");
            String type = parts.length > 0 ? parts[0] : "string";
            boolean required = parts.length > 1 && "required".equals(parts[1]);
            int maxLength = parts.length > 2 ? Integer.parseInt(parts[2]) : Integer.MAX_VALUE;

            Object value = row.get(fieldName);
            if (required && (value == null || (value instanceof String && ((String) value).trim().isEmpty()))) {
                errors.add("Field '" + fieldName + "' is required");
                continue;
            }
            if (value == null) {
                continue;
            }
            if (value instanceof String) {
                String strVal = ((String) value).trim();
                if (strVal.length() > maxLength) {
                    errors.add("Field '" + fieldName + "' exceeds max length of " + maxLength);
                }
                if ("numeric".equals(type)) {
                    try {
                        new BigDecimal(strVal);
                    } catch (NumberFormatException e) {
                        errors.add("Field '" + fieldName + "' must be a valid number");
                    }
                }
                if ("integer".equals(type)) {
                    try {
                        Long.parseLong(strVal);
                    } catch (NumberFormatException e) {
                        errors.add("Field '" + fieldName + "' must be a valid integer");
                    }
                }
            }
        }
        return errors;
    }

    public String cleanStringValue(String value) {
        if (value == null) {
            return "";
        }
        String cleaned = value.trim();
        cleaned = cleaned.replaceAll("\\s+", " ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            if (c >= 32 && c != 127) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public BigDecimal parseNumeric(Object value, String fieldName) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        try {
            if (value instanceof BigDecimal) {
                return (BigDecimal) value;
            }
            if (value instanceof Number) {
                return BigDecimal.valueOf(((Number) value).doubleValue());
            }
            String str = cleanStringValue(value.toString());
            if (str.isEmpty()) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse numeric value '{}' for field '{}'", value, fieldName);
            return BigDecimal.ZERO;
        }
    }

    public LocalDate parseDate(Object value, String fieldName) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate();
        }
        if (value instanceof java.util.Date) {
            return ((java.util.Date) value).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
        }
        String str = cleanStringValue(value.toString());
        if (str.isEmpty()) {
            return null;
        }
        String[] patterns = {"yyyy-MM-dd", "yyyy/MM/dd", "yyyyMMdd", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss"};
        for (String pattern : patterns) {
            try {
                if (pattern.contains("HH:mm")) {
                    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                    sdf.setLenient(false);
                    java.util.Date parsed = sdf.parse(str);
                    return parsed.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                } else {
                    return LocalDate.parse(str, DateTimeFormatter.ofPattern(pattern));
                }
            } catch (ParseException | DateTimeParseException e) {
                // continue to next pattern
            }
        }
        log.warn("Failed to parse date value '{}' for field '{}'", value, fieldName);
        return null;
    }

    public ImportResult importMaterials(List<Map<String, Object>> records) {
        ImportResult result = new ImportResult();
        result.setTotal(records != null ? records.size() : 0);
        result.setErrors(new ArrayList<>());
        if (records == null || records.isEmpty()) {
            return result;
        }
        Map<String, String> fieldRules = Map.of(
            "material_code", "string|required|100",
            "material_name", "string|required|200",
            "spec", "string||500",
            "unit", "string||20",
            "unit_price", "numeric||10",
            "category_name", "string||50",
            "fan_type", "string||30",
            "fan_model", "string||100"
        );
        List<Object[]> batchArgs = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            Map<String, Object> row = records.get(i);
            List<String> errors = validateRow(row, fieldRules);
            if (!errors.isEmpty()) {
                result.getErrors().add("Row " + (i + 1) + ": " + String.join("; ", errors));
                result.setFailed(result.getFailed() + 1);
                continue;
            }
            String materialCode = cleanStringValue((String) row.get("material_code"));
            String materialName = cleanStringValue((String) row.get("material_name"));
            String spec = cleanStringValue((String) row.getOrDefault("spec", ""));
            String unit = cleanStringValue((String) row.getOrDefault("unit", "个"));
            BigDecimal unitPrice = parseNumeric(row.get("unit_price"), "unit_price");
            String categoryName = cleanStringValue((String) row.getOrDefault("category_name", ""));
            String fanType = cleanStringValue((String) row.getOrDefault("fan_type", ""));
            String fanModel = cleanStringValue((String) row.getOrDefault("fan_model", ""));
            String remark = cleanStringValue((String) row.getOrDefault("remark", ""));
            batchArgs.add(new Object[]{materialCode, materialName, spec, unit, unitPrice, categoryName, fanType, fanModel, remark});
        }
        if (batchArgs.isEmpty()) {
            return result;
        }
        String sql = "INSERT INTO jn_material (material_code, material_name, spec, unit, unit_price, category_name, fan_type, fan_model, remark, status, del_flag, create_by, create_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, '0', '0', 'import', NOW()) " +
                     "ON DUPLICATE KEY UPDATE material_name = VALUES(material_name), spec = VALUES(spec), unit = VALUES(unit), " +
                     "unit_price = VALUES(unit_price), category_name = VALUES(category_name), fan_type = VALUES(fan_type), " +
                     "fan_model = VALUES(fan_model), remark = VALUES(remark), update_time = NOW()";
        try {
            int[] updateCounts = jdbcTemplate.batchUpdate(sql, batchArgs);
            int successCount = 0;
            for (int count : updateCounts) {
                if (count > 0) {
                    successCount++;
                }
            }
            result.setSuccess(successCount);
            result.setFailed(batchArgs.size() - successCount + result.getFailed());
        } catch (Exception e) {
            log.error("Batch insert materials failed", e);
            result.getErrors().add("Batch insert failed: " + e.getMessage());
            result.setFailed(batchArgs.size());
        }
        return result;
    }

    public ImportResult importCustomers(List<Map<String, Object>> records) {
        ImportResult result = new ImportResult();
        result.setTotal(records != null ? records.size() : 0);
        result.setErrors(new ArrayList<>());
        if (records == null || records.isEmpty()) {
            return result;
        }
        Map<String, String> fieldRules = Map.of(
            "customer_code", "string|required|50",
            "customer_name", "string|required|200",
            "contact", "string||50",
            "phone", "string||50",
            "region", "string||100",
            "address", "string||500",
            "credit_limit", "numeric||12"
        );
        List<Object[]> batchArgs = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            Map<String, Object> row = records.get(i);
            List<String> errors = validateRow(row, fieldRules);
            if (!errors.isEmpty()) {
                result.getErrors().add("Row " + (i + 1) + ": " + String.join("; ", errors));
                result.setFailed(result.getFailed() + 1);
                continue;
            }
            String customerCode = cleanStringValue((String) row.get("customer_code"));
            String customerName = cleanStringValue((String) row.get("customer_name"));
            String contact = cleanStringValue((String) row.getOrDefault("contact", ""));
            String phone = cleanStringValue((String) row.getOrDefault("phone", ""));
            String region = cleanStringValue((String) row.getOrDefault("region", ""));
            String address = cleanStringValue((String) row.getOrDefault("address", ""));
            BigDecimal creditLimit = parseNumeric(row.get("credit_limit"), "credit_limit");
            String remark = cleanStringValue((String) row.getOrDefault("remark", ""));
            batchArgs.add(new Object[]{customerCode, customerName, contact, phone, region, address, creditLimit, remark});
        }
        if (batchArgs.isEmpty()) {
            return result;
        }
        String sql = "INSERT INTO jn_customer (customer_code, customer_name, contact, phone, region, address, credit_limit, remark, status, del_flag, create_by, create_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, '0', '0', 'import', NOW()) " +
                     "ON DUPLICATE KEY UPDATE customer_name = VALUES(customer_name), contact = VALUES(contact), " +
                     "phone = VALUES(phone), region = VALUES(region), address = VALUES(address), " +
                     "credit_limit = VALUES(credit_limit), remark = VALUES(remark), update_time = NOW()";
        try {
            int[] updateCounts = jdbcTemplate.batchUpdate(sql, batchArgs);
            int successCount = 0;
            for (int count : updateCounts) {
                if (count > 0) {
                    successCount++;
                }
            }
            result.setSuccess(successCount);
            result.setFailed(batchArgs.size() - successCount + result.getFailed());
        } catch (Exception e) {
            log.error("Batch insert customers failed", e);
            result.getErrors().add("Batch insert failed: " + e.getMessage());
            result.setFailed(batchArgs.size());
        }
        return result;
    }

    public ImportResult importSuppliers(List<Map<String, Object>> records) {
        ImportResult result = new ImportResult();
        result.setTotal(records != null ? records.size() : 0);
        result.setErrors(new ArrayList<>());
        if (records == null || records.isEmpty()) {
            return result;
        }
        Map<String, String> fieldRules = Map.of(
            "supplier_code", "string|required|50",
            "supplier_name", "string|required|200",
            "contact", "string||50",
            "phone", "string||50",
            "biz_scope", "string||500",
            "address", "string||500",
            "rating", "integer||5"
        );
        List<Object[]> batchArgs = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            Map<String, Object> row = records.get(i);
            List<String> errors = validateRow(row, fieldRules);
            if (!errors.isEmpty()) {
                result.getErrors().add("Row " + (i + 1) + ": " + String.join("; ", errors));
                result.setFailed(result.getFailed() + 1);
                continue;
            }
            String supplierCode = cleanStringValue((String) row.get("supplier_code"));
            String supplierName = cleanStringValue((String) row.get("supplier_name"));
            String contact = cleanStringValue((String) row.getOrDefault("contact", ""));
            String phone = cleanStringValue((String) row.getOrDefault("phone", ""));
            String bizScope = cleanStringValue((String) row.getOrDefault("biz_scope", ""));
            String address = cleanStringValue((String) row.getOrDefault("address", ""));
            BigDecimal ratingBig = parseNumeric(row.get("rating"), "rating");
            int rating = ratingBig.intValue();
            if (rating < 1) rating = 3;
            if (rating > 5) rating = 5;
            String remark = cleanStringValue((String) row.getOrDefault("remark", ""));
            batchArgs.add(new Object[]{supplierCode, supplierName, contact, phone, bizScope, address, rating, remark});
        }
        if (batchArgs.isEmpty()) {
            return result;
        }
        String sql = "INSERT INTO jn_supplier (supplier_code, supplier_name, contact, phone, biz_scope, address, rating, remark, cooperation, del_flag, create_by, create_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, '0', '0', 'import', NOW()) " +
                     "ON DUPLICATE KEY UPDATE supplier_name = VALUES(supplier_name), contact = VALUES(contact), " +
                     "phone = VALUES(phone), biz_scope = VALUES(biz_scope), address = VALUES(address), " +
                     "rating = VALUES(rating), remark = VALUES(remark), update_time = NOW()";
        try {
            int[] updateCounts = jdbcTemplate.batchUpdate(sql, batchArgs);
            int successCount = 0;
            for (int count : updateCounts) {
                if (count > 0) {
                    successCount++;
                }
            }
            result.setSuccess(successCount);
            result.setFailed(batchArgs.size() - successCount + result.getFailed());
        } catch (Exception e) {
            log.error("Batch insert suppliers failed", e);
            result.getErrors().add("Batch insert failed: " + e.getMessage());
            result.setFailed(batchArgs.size());
        }
        return result;
    }

    public int batchInsert(String tableName, List<Map<String, Object>> records, Map<String, String> fieldMappings) {
        if (tableName == null || records == null || records.isEmpty() || fieldMappings == null || fieldMappings.isEmpty()) {
            return 0;
        }
        Set<String> fields = fieldMappings.keySet();
        String columns = String.join(", ", fields);
        String placeholders = String.join(", ", fields.stream().map(f -> "?").toArray(String[]::new));
        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";
        List<Object[]> batchArgs = new ArrayList<>();
        for (Map<String, Object> record : records) {
            List<Object> rowValues = new ArrayList<>();
            for (String field : fields) {
                String mapping = fieldMappings.get(field);
                Object value = record.get(mapping);
                if (value instanceof String) {
                    value = cleanStringValue((String) value);
                }
                rowValues.add(value);
            }
            batchArgs.add(rowValues.toArray());
        }
        try {
            int[] updateCounts = jdbcTemplate.batchUpdate(sql, batchArgs);
            int total = 0;
            for (int count : updateCounts) {
                if (count > 0) {
                    total++;
                }
            }
            return total;
        } catch (Exception e) {
            log.error("Batch insert into table '{}' failed", tableName, e);
            return 0;
        }
    }

    public static class ImportResult {
        private int total;
        private int success;
        private int failed;
        private List<String> errors;

        public ImportResult() {
            this.total = 0;
            this.success = 0;
            this.failed = 0;
            this.errors = new ArrayList<>();
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getSuccess() {
            return success;
        }

        public void setSuccess(int success) {
            this.success = success;
        }

        public int getFailed() {
            return failed;
        }

        public void setFailed(int failed) {
            this.failed = failed;
        }

        public List<String> getErrors() {
            return errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }
    }
}
