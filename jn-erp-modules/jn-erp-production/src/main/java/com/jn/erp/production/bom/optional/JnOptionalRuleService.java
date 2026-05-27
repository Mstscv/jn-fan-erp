package com.jn.erp.production.bom.optional;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jn.erp.production.bom.domain.JnBomLine;
import com.jn.erp.production.bom.mapper.JnBomLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JnOptionalRuleService {

    private static final String GROUP_ATTR_NAME = "groupName";

    @Autowired
    private JnBomLineMapper bomLineMapper;

    public Map<String, Object> evaluateRule(JnBomLine line, Map<String, Object> materialParams) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("lineId", line.getLineId());
        result.put("materialCode", line.getMaterialCode());
        result.put("materialName", line.getMaterialName());

        if (line.getOptionalRule() == null || line.getOptionalRule().isEmpty()) {
            result.put("matched", true);
            result.put("evaluated", false);
            return result;
        }

        JSONObject ruleJson;
        try {
            ruleJson = JSON.parseObject(line.getOptionalRule());
        } catch (Exception e) {
            result.put("matched", false);
            result.put("evaluated", false);
            result.put("error", "规则JSON解析失败");
            return result;
        }

        String condition = ruleJson.getString("condition");
        if (condition == null || condition.isEmpty()) {
            result.put("matched", true);
            result.put("evaluated", false);
            return result;
        }

        String evaluatedCondition = replaceParams(condition, materialParams);
        boolean matched = evaluateExpression(evaluatedCondition);

        result.put("matched", matched);
        result.put("evaluated", true);
        if (matched) {
            result.put("matchMaterialName", ruleJson.getString("matchMaterialName"));
            result.put("matchMaterialCode", ruleJson.getString("matchMaterialCode"));
        }
        return result;
    }

    public List<Map<String, Object>> getOptionGroups(Long bomId) {
        List<JnBomLine> lines = bomLineMapper.selectByBomId(bomId);

        List<JnBomLine> optionalLines = lines.stream()
                .filter(line -> "Y".equals(line.getIsOptional()))
                .collect(Collectors.toList());

        Map<String, List<JnBomLine>> grouped = new LinkedHashMap<>();
        List<JnBomLine> noGroupLines = new ArrayList<>();

        for (JnBomLine line : optionalLines) {
            String groupName = extractGroupName(line.getOptionalRule());
            if (groupName != null) {
                grouped.computeIfAbsent(groupName, k -> new ArrayList<>()).add(line);
            } else if (line.getOptionalGroup() != null && !line.getOptionalGroup().isEmpty()) {
                grouped.computeIfAbsent(line.getOptionalGroup(), k -> new ArrayList<>()).add(line);
            } else {
                noGroupLines.add(line);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<JnBomLine>> entry : grouped.entrySet()) {
            Map<String, Object> group = new LinkedHashMap<>();
            group.put("groupName", entry.getKey());
            group.put("items", entry.getValue().stream().map(this::toOptionItem).collect(Collectors.toList()));
            result.add(group);
        }

        if (!noGroupLines.isEmpty()) {
            Map<String, Object> defaultGroup = new LinkedHashMap<>();
            defaultGroup.put("groupName", "默认分组");
            defaultGroup.put("items", noGroupLines.stream().map(this::toOptionItem).collect(Collectors.toList()));
            result.add(defaultGroup);
        }

        return result;
    }

    public void setDefaultSelection(Long bomId, String groupName, Long lineId) {
        List<JnBomLine> lines = bomLineMapper.selectByBomId(bomId);

        for (JnBomLine line : lines) {
            if (!"Y".equals(line.getIsOptional())) {
                continue;
            }

            String effectiveGroup = null;
            if (line.getOptionalRule() != null) {
                try {
                    JSONObject ruleJson = JSON.parseObject(line.getOptionalRule());
                    effectiveGroup = ruleJson.getString(GROUP_ATTR_NAME);
                } catch (Exception ignored) {
                }
            }
            if (effectiveGroup == null) {
                effectiveGroup = line.getOptionalGroup();
            }

            if (groupName.equals(effectiveGroup)) {
                line.setDefaultSelected(line.getLineId().equals(lineId) ? "Y" : "N");
                bomLineMapper.updateById(line);
            }
        }
    }

    private String extractGroupName(String optionalRule) {
        if (optionalRule == null || optionalRule.isEmpty()) {
            return null;
        }
        try {
            JSONObject ruleJson = JSON.parseObject(optionalRule);
            return ruleJson.getString(GROUP_ATTR_NAME);
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Object> toOptionItem(JnBomLine line) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("lineId", line.getLineId());
        item.put("materialId", line.getMaterialId());
        item.put("materialCode", line.getMaterialCode());
        item.put("materialName", line.getMaterialName());
        item.put("spec", line.getSpec());
        item.put("quantity", line.getQuantity());

        boolean isDefault = "Y".equals(line.getDefaultSelected());
        if (!isDefault && line.getOptionalRule() != null) {
            try {
                JSONObject ruleJson = JSON.parseObject(line.getOptionalRule());
                isDefault = ruleJson.getBooleanValue("isDefault");
            } catch (Exception ignored) {
            }
        }
        item.put("isDefault", isDefault);
        return item;
    }

    private String replaceParams(String condition, Map<String, Object> params) {
        String result = condition;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = entry.getKey();
            Object value = entry.getValue();
            String replacement;
            if (value instanceof Number) {
                replacement = value.toString();
            } else if (value instanceof String) {
                replacement = "\"" + value + "\"";
            } else {
                replacement = String.valueOf(value);
            }
            result = result.replace(placeholder, replacement);
        }
        return result;
    }

    private boolean evaluateExpression(String expression) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            if (engine == null) {
                engine = manager.getEngineByName("nashorn");
            }
            if (engine == null) {
                return simpleEvaluate(expression);
            }
            Object result = engine.eval(expression);
            if (result instanceof Boolean) {
                return (Boolean) result;
            }
            return "true".equalsIgnoreCase(String.valueOf(result));
        } catch (ScriptException e) {
            return simpleEvaluate(expression);
        }
    }

    private boolean simpleEvaluate(String expression) {
        if (expression == null || expression.isEmpty()) {
            return true;
        }
        expression = expression.trim();

        if (expression.contains("&&") || expression.contains("AND")) {
            String separator = expression.contains("AND") ? "AND" : "&&";
            String[] parts = expression.split(separator);
            for (String part : parts) {
                if (!simpleEvaluate(part.trim())) {
                    return false;
                }
            }
            return true;
        }

        if (expression.contains("||") || expression.contains("OR")) {
            String separator = expression.contains("OR") ? "OR" : "||";
            String[] parts = expression.split(separator);
            for (String part : parts) {
                if (simpleEvaluate(part.trim())) {
                    return true;
                }
            }
            return false;
        }

        return evaluateComparison(expression);
    }

    private boolean evaluateComparison(String expr) {
        expr = expr.trim();

        if (expr.contains(">=")) {
            String[] parts = expr.split(">=");
            if (parts.length == 2) {
                return parseDouble(parts[0]) >= parseDouble(parts[1]);
            }
        }
        if (expr.contains("<=")) {
            String[] parts = expr.split("<=");
            if (parts.length == 2) {
                return parseDouble(parts[0]) <= parseDouble(parts[1]);
            }
        }
        if (expr.contains(">")) {
            String[] parts = expr.split(">");
            if (parts.length == 2) {
                return parseDouble(parts[0]) > parseDouble(parts[1]);
            }
        }
        if (expr.contains("<")) {
            String[] parts = expr.split("<");
            if (parts.length == 2) {
                return parseDouble(parts[0]) < parseDouble(parts[1]);
            }
        }
        if (expr.contains("==")) {
            String[] parts = expr.split("==");
            if (parts.length == 2) {
                return parts[0].trim().equals(parts[1].trim());
            }
        }
        if (expr.contains("!=")) {
            String[] parts = expr.split("!=");
            if (parts.length == 2) {
                return !parts[0].trim().equals(parts[1].trim());
            }
        }

        return "true".equalsIgnoreCase(expr);
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
