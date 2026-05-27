package com.jn.erp.production.bom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.bom.domain.JnBom;
import com.jn.erp.production.bom.domain.JnBomLine;
import com.jn.erp.production.bom.domain.JnBomVersion;
import com.jn.erp.production.bom.mapper.JnBomLineMapper;
import com.jn.erp.production.bom.mapper.JnBomMapper;
import com.jn.erp.production.bom.mapper.JnBomMaterialPriceMapper;
import com.jn.erp.production.bom.mapper.JnBomVersionMapper;
import com.jn.erp.production.bom.service.IJnBomService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JnBomServiceImpl extends ServiceImpl<JnBomMapper, JnBom> implements IJnBomService {

    @Autowired
    private JnBomMapper bomMapper;

    @Autowired
    private JnBomLineMapper bomLineMapper;

    @Autowired
    private JnBomVersionMapper bomVersionMapper;

    @Autowired
    private JnBomMaterialPriceMapper materialPriceMapper;

    @Override
    public List<JnBom> selectList(JnBom query) {
        LambdaQueryWrapper<JnBom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JnBom::getDelFlag, "0");
        if (query.getBomCode() != null && !query.getBomCode().isEmpty()) {
            wrapper.like(JnBom::getBomCode, query.getBomCode());
        }
        if (query.getBomName() != null && !query.getBomName().isEmpty()) {
            wrapper.like(JnBom::getBomName, query.getBomName());
        }
        if (query.getProductId() != null) {
            wrapper.eq(JnBom::getProductId, query.getProductId());
        }
        if (query.getProductName() != null && !query.getProductName().isEmpty()) {
            wrapper.like(JnBom::getProductName, query.getProductName());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(JnBom::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(JnBom::getCreateTime);
        return bomMapper.selectList(wrapper);
    }

    @Override
    public JnBom getById(Long bomId) {
        JnBom bom = bomMapper.selectById(bomId);
        if (bom == null) {
            return null;
        }
        return buildTree(bomId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBom(JnBom bom, List<JnBomLine> lines) {
        if (bom.getVersion() == null) {
            bom.setVersion("v1.0");
        }
        if (bom.getStatus() == null) {
            bom.setStatus("DRAFT");
        }
        if (bom.getTotalCost() == null) {
            bom.setTotalCost(BigDecimal.ZERO);
        }
        if (bom.getDelFlag() == null) {
            bom.setDelFlag("0");
        }
        bom.setBomCode(generateBomCode());
        bom.setCreateBy(SecurityUtils.getUsername());
        int result = bomMapper.insert(bom);

        if (lines != null && !lines.isEmpty()) {
            for (JnBomLine line : lines) {
                line.setBomId(bom.getBomId());
                if (line.getQuantity() == null) {
                    line.setQuantity(BigDecimal.ONE);
                }
                if (line.getScrapRate() == null) {
                    line.setScrapRate(BigDecimal.ZERO);
                }
                if (line.getIsOptional() == null) {
                    line.setIsOptional("N");
                }
                if (line.getDefaultSelected() == null) {
                    line.setDefaultSelected("N");
                }
                if (line.getSortOrder() == null) {
                    line.setSortOrder(0);
                }
                bomLineMapper.insert(line);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBom(JnBom bom, List<JnBomLine> lines) {
        bom.setUpdateBy(SecurityUtils.getUsername());
        int result = bomMapper.updateById(bom);

        bomLineMapper.deleteByBomId(bom.getBomId());

        if (lines != null && !lines.isEmpty()) {
            for (JnBomLine line : lines) {
                line.setLineId(null);
                line.setBomId(bom.getBomId());
                if (line.getQuantity() == null) {
                    line.setQuantity(BigDecimal.ONE);
                }
                if (line.getScrapRate() == null) {
                    line.setScrapRate(BigDecimal.ZERO);
                }
                if (line.getIsOptional() == null) {
                    line.setIsOptional("N");
                }
                if (line.getDefaultSelected() == null) {
                    line.setDefaultSelected("N");
                }
                if (line.getSortOrder() == null) {
                    line.setSortOrder(0);
                }
                bomLineMapper.insert(line);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] bomIds) {
        int count = 0;
        for (Long id : bomIds) {
            JnBom bom = bomMapper.selectById(id);
            if (bom != null) {
                bom.setDelFlag("1");
                count += bomMapper.updateById(bom);
            }
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long bomId, String newStatus) {
        JnBom bom = bomMapper.selectById(bomId);
        if (bom == null) {
            throw new RuntimeException("BOM不存在: " + bomId);
        }

        String currentStatus = bom.getStatus();
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new RuntimeException("不允许的状态变更: " + currentStatus + " -> " + newStatus);
        }

        bom.setStatus(newStatus);
        if ("APPROVED".equals(newStatus)) {
            bom.setApproveBy(SecurityUtils.getUsername());
            bom.setApproveTime(LocalDateTime.now());
        }
        bom.setUpdateBy(SecurityUtils.getUsername());
        bomMapper.updateById(bom);

        if ("EFFECTIVE".equals(newStatus)) {
            JnBomVersion versionRecord = new JnBomVersion();
            versionRecord.setBomId(bomId);
            versionRecord.setVersion(bom.getVersion());
            versionRecord.setChangeLog("BOM生效，状态变更为EFFECTIVE");
            versionRecord.setBomSnapshot(buildSnapshotJson(bomId));
            versionRecord.setCreatedBy(SecurityUtils.getUsername());
            versionRecord.setCreatedTime(LocalDateTime.now());
            bomVersionMapper.insert(versionRecord);
        }
    }

    @Override
    public JnBom buildTree(Long bomId) {
        List<JnBomLine> allLines = bomLineMapper.selectByBomId(bomId);
        Map<Long, JnBomLine> lineMap = allLines.stream()
                .collect(Collectors.toMap(JnBomLine::getLineId, line -> line));

        List<JnBomLine> rootLines = new ArrayList<>();
        for (JnBomLine line : allLines) {
            if (line.getParentLineId() == null) {
                rootLines.add(line);
            } else {
                JnBomLine parent = lineMap.get(line.getParentLineId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(line);
                }
            }
        }

        JnBom bom = bomMapper.selectById(bomId);
        if (bom != null) {
            bom.setParams(new java.util.HashMap<>());
            bom.getParams().put("lines", rootLines);
        }
        return bom;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal calculateCost(Long bomId) {
        JnBom bom = bomMapper.selectById(bomId);
        if (bom == null) {
            throw new RuntimeException("BOM不存在: " + bomId);
        }

        List<JnBomLine> allLines = bomLineMapper.selectByBomId(bomId);
        Map<Long, List<JnBomLine>> parentChildMap = new HashMap<>();
        List<JnBomLine> rootLines = new ArrayList<>();

        for (JnBomLine line : allLines) {
            if (line.getParentLineId() == null) {
                rootLines.add(line);
            } else {
                parentChildMap.computeIfAbsent(line.getParentLineId(), k -> new ArrayList<>()).add(line);
            }
        }

        BigDecimal totalCost = BigDecimal.ZERO;
        for (JnBomLine rootLine : rootLines) {
            BigDecimal lineCost = calculateLineCost(rootLine, parentChildMap);
            totalCost = totalCost.add(lineCost);
        }

        bom.setTotalCost(totalCost.setScale(2, RoundingMode.HALF_UP));
        bom.setUpdateBy(SecurityUtils.getUsername());
        bomMapper.updateById(bom);
        return bom.getTotalCost();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceMaterial(Long bomId, Long oldMaterialId, Long newMaterialId) {
        List<JnBomLine> lines = bomLineMapper.selectByBomId(bomId);
        for (JnBomLine line : lines) {
            if (oldMaterialId.equals(line.getMaterialId())) {
                line.setMaterialId(newMaterialId);
                bomLineMapper.updateById(line);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JnBom copyBom(Long sourceBomId, String newBomName) {
        JnBom sourceBom = bomMapper.selectById(sourceBomId);
        if (sourceBom == null) {
            throw new RuntimeException("源BOM不存在: " + sourceBomId);
        }

        JnBom newBom = new JnBom();
        newBom.setBomName(newBomName);
        newBom.setBomCode(generateBomCode());
        newBom.setProductId(sourceBom.getProductId());
        newBom.setProductCode(sourceBom.getProductCode());
        newBom.setProductName(sourceBom.getProductName());
        newBom.setParentBomId(sourceBom.getBomId());
        newBom.setVersion("v1.0");
        newBom.setStatus("DRAFT");
        newBom.setTotalCost(BigDecimal.ZERO);
        newBom.setDelFlag("0");
        newBom.setCreateBy(SecurityUtils.getUsername());
        bomMapper.insert(newBom);

        List<JnBomLine> sourceLines = bomLineMapper.selectByBomId(sourceBomId);
        Map<Long, Long> lineIdMapping = new HashMap<>();

        for (JnBomLine sourceLine : sourceLines) {
            if (sourceLine.getParentLineId() == null) {
                JnBomLine newLine = cloneLine(sourceLine, newBom.getBomId(), null);
                bomLineMapper.insert(newLine);
                lineIdMapping.put(sourceLine.getLineId(), newLine.getLineId());
            }
        }

        for (JnBomLine sourceLine : sourceLines) {
            if (sourceLine.getParentLineId() != null) {
                Long newParentId = lineIdMapping.get(sourceLine.getParentLineId());
                if (newParentId != null) {
                    JnBomLine newLine = cloneLine(sourceLine, newBom.getBomId(), newParentId);
                    bomLineMapper.insert(newLine);
                    lineIdMapping.put(sourceLine.getLineId(), newLine.getLineId());
                }
            }
        }

        return newBom;
    }

    @Override
    public JnBom getBomTree(Long bomId) {
        return buildTree(bomId);
    }

    private BigDecimal calculateLineCost(JnBomLine line, Map<Long, List<JnBomLine>> parentChildMap) {
        List<JnBomLine> children = parentChildMap.get(line.getLineId());

        if (children == null || children.isEmpty()) {
            BigDecimal unitPrice = materialPriceMapper.selectUnitPriceByMaterialId(line.getMaterialId());
            if (unitPrice == null) {
                unitPrice = BigDecimal.ZERO;
            }
            BigDecimal scrapFactor = BigDecimal.ONE.add(
                    line.getScrapRate().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
            return unitPrice.multiply(line.getQuantity()).multiply(scrapFactor);
        }

        BigDecimal sum = BigDecimal.ZERO;
        for (JnBomLine child : children) {
            BigDecimal childCost = calculateLineCost(child, parentChildMap);
            sum = sum.add(childCost);
        }
        return sum;
    }

    private String buildSnapshotJson(Long bomId) {
        JnBom bom = bomMapper.selectById(bomId);
        List<JnBomLine> lines = bomLineMapper.selectByBomId(bomId);
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"bomId\":").append(bomId).append(",");
        sb.append("\"bomCode\":\"").append(escapeJson(bom.getBomCode())).append("\",");
        sb.append("\"bomName\":\"").append(escapeJson(bom.getBomName())).append("\",");
        sb.append("\"version\":\"").append(escapeJson(bom.getVersion())).append("\",");
        sb.append("\"totalCost\":").append(bom.getTotalCost()).append(",");
        sb.append("\"lines\":[");
        for (int i = 0; i < lines.size(); i++) {
            JnBomLine line = lines.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"lineId\":").append(line.getLineId()).append(",");
            sb.append("\"materialId\":").append(line.getMaterialId()).append(",");
            sb.append("\"materialCode\":\"").append(escapeJson(line.getMaterialCode())).append("\",");
            sb.append("\"materialName\":\"").append(escapeJson(line.getMaterialName())).append("\",");
            sb.append("\"quantity\":").append(line.getQuantity()).append(",");
            sb.append("\"scrapRate\":").append(line.getScrapRate());
            sb.append("}");
        }
        sb.append("]}");
        return sb.toString();
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
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

    private String generateBomCode() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "BOM-" + datePart + "-";
        LambdaQueryWrapper<JnBom> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(JnBom::getBomCode, prefix);
        wrapper.orderByDesc(JnBom::getBomCode);
        wrapper.last("LIMIT 1");
        JnBom last = bomMapper.selectOne(wrapper);
        int seq = 1;
        if (last != null && last.getBomCode() != null) {
            String lastCode = last.getBomCode();
            String seqStr = lastCode.substring(lastCode.lastIndexOf("-") + 1);
            try {
                seq = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException e) {
                seq = 1;
            }
        }
        return prefix + String.format("%04d", seq);
    }

    private JnBomLine cloneLine(JnBomLine source, Long newBomId, Long newParentLineId) {
        JnBomLine newLine = new JnBomLine();
        newLine.setBomId(newBomId);
        newLine.setParentLineId(newParentLineId);
        newLine.setMaterialId(source.getMaterialId());
        newLine.setMaterialCode(source.getMaterialCode());
        newLine.setMaterialName(source.getMaterialName());
        newLine.setSpec(source.getSpec());
        newLine.setQuantity(source.getQuantity());
        newLine.setUnit(source.getUnit());
        newLine.setPositionNo(source.getPositionNo());
        newLine.setScrapRate(source.getScrapRate());
        newLine.setIsOptional(source.getIsOptional());
        newLine.setOptionalGroup(source.getOptionalGroup());
        newLine.setOptionalRule(source.getOptionalRule());
        newLine.setDefaultSelected(source.getDefaultSelected());
        newLine.setAlternativeId(source.getAlternativeId());
        newLine.setEffectiveDate(source.getEffectiveDate());
        newLine.setExpireDate(source.getExpireDate());
        newLine.setRemark(source.getRemark());
        newLine.setSortOrder(source.getSortOrder());
        return newLine;
    }
}
