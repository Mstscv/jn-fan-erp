package com.jn.erp.common.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class SequenceUtils {

    private static final Logger log = LoggerFactory.getLogger(SequenceUtils.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional(rollbackFor = Exception.class)
    public String generate(String seqCode) {
        Map<String, Object> config = querySequenceConfig(seqCode);
        if (config == null) {
            throw new RuntimeException("序列号配置不存在: " + seqCode);
        }

        String prefix = (String) config.get("prefix");
        String dateFormat = (String) config.get("date_format");
        Integer seqLength = (Integer) config.get("seq_length");
        String resetMode = (String) config.get("reset_mode");
        Integer currentNo = (Integer) config.get("current_no");
        String currentDateStr = (String) config.get("current_date");
        Integer version = (Integer) config.get("version");

        String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        boolean needReset = needReset(resetMode, currentDateStr, todayStr);

        int newNo;
        if (needReset || currentNo == null) {
            newNo = 1;
        } else {
            newNo = currentNo + 1;
        }

        int updated = jdbcTemplate.update(
                "UPDATE jn_sequence SET current_no = ?, current_date = ?, version = version + 1 " +
                        "WHERE seq_code = ? AND version = ?",
                newNo, todayStr, seqCode, version
        );

        if (updated == 0) {
            throw new RuntimeException("序列号生成失败，并发冲突: " + seqCode);
        }

        return formatSequence(prefix, todayStr, newNo, seqLength);
    }

    private Map<String, Object> querySequenceConfig(String seqCode) {
        try {
            return jdbcTemplate.queryForMap(
                    "SELECT seq_code, prefix, date_format, seq_length, reset_mode, current_no, current_date, version " +
                            "FROM jn_sequence WHERE seq_code = ?", seqCode);
        } catch (Exception e) {
            log.error("查询序列号配置失败: {}", seqCode, e);
            return null;
        }
    }

    private boolean needReset(String resetMode, String currentDateStr, String todayStr) {
        if (currentDateStr == null) {
            return true;
        }
        if ("DAILY".equals(resetMode)) {
            return !todayStr.equals(currentDateStr);
        }
        if ("MONTHLY".equals(resetMode)) {
            String currentMonth = currentDateStr.substring(0, 6);
            String todayMonth = todayStr.substring(0, 6);
            return !todayMonth.equals(currentMonth);
        }
        if ("YEARLY".equals(resetMode)) {
            String currentYear = currentDateStr.substring(0, 4);
            String todayYear = todayStr.substring(0, 4);
            return !todayYear.equals(currentYear);
        }
        return false;
    }

    private String formatSequence(String prefix, String dateStr, int no, int seqLength) {
        String seqNo = String.format("%0" + seqLength + "d", no);
        return prefix + "-" + dateStr + "-" + seqNo;
    }
}
