package com.jn.erp.production.quality.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.quality.domain.JnInspection;
import com.jn.erp.production.quality.domain.JnInspectionLine;
import com.jn.erp.production.quality.mapper.JnInspectionLineMapper;
import com.jn.erp.production.quality.mapper.JnInspectionMapper;
import com.jn.erp.production.quality.service.IJnInspectionService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class JnInspectionServiceImpl extends ServiceImpl<JnInspectionMapper, JnInspection> implements IJnInspectionService {

    @Autowired
    private JnInspectionMapper inspectionMapper;

    @Autowired
    private JnInspectionLineMapper inspectionLineMapper;

    @Override
    public List<JnInspection> selectList(JnInspection query) {
        LambdaQueryWrapper<JnInspection> wrapper = new LambdaQueryWrapper<>();
        if (query.getInspectionNo() != null && !query.getInspectionNo().isEmpty()) {
            wrapper.like(JnInspection::getInspectionNo, query.getInspectionNo());
        }
        if (query.getInspectionType() != null && !query.getInspectionType().isEmpty()) {
            wrapper.eq(JnInspection::getInspectionType, query.getInspectionType());
        }
        if (query.getSourceOrderNo() != null && !query.getSourceOrderNo().isEmpty()) {
            wrapper.like(JnInspection::getSourceOrderNo, query.getSourceOrderNo());
        }
        if (query.getMaterialName() != null && !query.getMaterialName().isEmpty()) {
            wrapper.like(JnInspection::getMaterialName, query.getMaterialName());
        }
        if (query.getResult() != null && !query.getResult().isEmpty()) {
            wrapper.eq(JnInspection::getResult, query.getResult());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(JnInspection::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(JnInspection::getCreateTime);
        return inspectionMapper.selectList(wrapper);
    }

    @Override
    public JnInspection selectById(Long id) {
        return inspectionMapper.selectById(id);
    }

    @Override
    public JnInspection selectWithLines(Long id) {
        JnInspection inspection = inspectionMapper.selectById(id);
        if (inspection != null) {
            List<JnInspectionLine> lines = inspectionLineMapper.selectByInspectionId(id);
            inspection.setLines(lines);
        }
        return inspection;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnInspection inspection, List<JnInspectionLine> lines) {
        inspection.setInspectionNo(generateInspectionNo(inspection.getInspectionType()));
        if (inspection.getStatus() == null) {
            inspection.setStatus("DRAFT");
        }
        if (inspection.getResult() == null) {
            inspection.setResult("PENDING");
        }
        inspection.setInspector(SecurityUtils.getUsername());
        inspection.setInspectionDate(LocalDate.now());
        inspection.setCreateBy(SecurityUtils.getUsername());
        int result = inspectionMapper.insert(inspection);

        if (lines != null && !lines.isEmpty()) {
            for (JnInspectionLine line : lines) {
                line.setInspectionId(inspection.getInspectionId());
                if (line.getResult() == null) {
                    line.setResult("PASS");
                }
                if (line.getDefectQty() == null) {
                    line.setDefectQty(0);
                }
                inspectionLineMapper.insert(line);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnInspection inspection, List<JnInspectionLine> lines) {
        inspection.setUpdateBy(SecurityUtils.getUsername());
        int result = inspectionMapper.updateById(inspection);

        inspectionLineMapper.deleteByInspectionId(inspection.getInspectionId());

        if (lines != null && !lines.isEmpty()) {
            for (JnInspectionLine line : lines) {
                line.setLineId(null);
                line.setInspectionId(inspection.getInspectionId());
                if (line.getResult() == null) {
                    line.setResult("PASS");
                }
                if (line.getDefectQty() == null) {
                    line.setDefectQty(0);
                }
                inspectionLineMapper.insert(line);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] ids) {
        int count = 0;
        for (Long id : ids) {
            inspectionLineMapper.deleteByInspectionId(id);
            count += inspectionMapper.deleteById(id);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(Long inspectionId) {
        JnInspection inspection = inspectionMapper.selectById(inspectionId);
        if (inspection == null) {
            throw new RuntimeException("检验单不存在: " + inspectionId);
        }
        inspection.setStatus("SUBMITTED");
        inspection.setUpdateBy(SecurityUtils.getUsername());
        inspectionMapper.updateById(inspection);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long inspectionId, String result) {
        JnInspection inspection = inspectionMapper.selectById(inspectionId);
        if (inspection == null) {
            throw new RuntimeException("检验单不存在: " + inspectionId);
        }
        inspection.setResult(result);
        inspection.setStatus("APPROVED");
        inspection.setPassQty(inspection.getSampleQty() - inspection.getRejectQty());
        inspection.setUpdateBy(SecurityUtils.getUsername());
        inspectionMapper.updateById(inspection);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long inspectionId, String defectDesc) {
        JnInspection inspection = inspectionMapper.selectById(inspectionId);
        if (inspection == null) {
            throw new RuntimeException("检验单不存在: " + inspectionId);
        }
        inspection.setResult("REJECT");
        inspection.setStatus("APPROVED");
        inspection.setDefectDesc(defectDesc);
        inspection.setPassQty(inspection.getSampleQty() - inspection.getRejectQty());
        inspection.setUpdateBy(SecurityUtils.getUsername());
        inspectionMapper.updateById(inspection);
    }

    private String generateInspectionNo(String inspectionType) {
        String prefix;
        if ("IQC".equals(inspectionType)) {
            prefix = "IQC-";
        } else if ("IPQC".equals(inspectionType)) {
            prefix = "IPQC-";
        } else if ("OQC".equals(inspectionType)) {
            prefix = "OQC-";
        } else {
            prefix = "QC-";
        }
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String codePrefix = prefix + datePart + "-";
        LambdaQueryWrapper<JnInspection> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(JnInspection::getInspectionNo, codePrefix);
        wrapper.orderByDesc(JnInspection::getInspectionNo);
        wrapper.last("LIMIT 1");
        JnInspection last = inspectionMapper.selectOne(wrapper);
        int seq = 1;
        if (last != null && last.getInspectionNo() != null) {
            String lastCode = last.getInspectionNo();
            String seqStr = lastCode.substring(lastCode.lastIndexOf("-") + 1);
            try {
                seq = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException e) {
                seq = 1;
            }
        }
        return codePrefix + String.format("%04d", seq);
    }
}
