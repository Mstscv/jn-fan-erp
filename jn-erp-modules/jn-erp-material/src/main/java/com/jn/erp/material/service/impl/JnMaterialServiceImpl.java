package com.jn.erp.material.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.material.domain.JnMaterial;
import com.jn.erp.material.domain.JnSequence;
import com.jn.erp.material.mapper.JnMaterialMapper;
import com.jn.erp.material.mapper.JnSequenceMapper;
import com.jn.erp.material.service.IJnMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class JnMaterialServiceImpl extends ServiceImpl<JnMaterialMapper, JnMaterial> implements IJnMaterialService {

    @Autowired
    private JnMaterialMapper materialMapper;

    @Autowired
    private JnSequenceMapper sequenceMapper;

    @Override
    public List<JnMaterial> selectList(JnMaterial material) {
        return materialMapper.selectListWithFan(material);
    }

    @Override
    public JnMaterial selectById(Long materialId) {
        return materialMapper.selectById(materialId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnMaterial material) {
        if (material.getUnit() == null) {
            material.setUnit("台");
        }
        if (material.getSafetyStock() == null) {
            material.setSafetyStock(0);
        }
        if (material.getStatus() == null) {
            material.setStatus("0");
        }
        if (material.getDelFlag() == null) {
            material.setDelFlag("0");
        }
        if (material.getIsConfigurable() == null) {
            material.setIsConfigurable("N");
        }
        if (material.getVersion() == null) {
            material.setVersion(1);
        }
        return materialMapper.insert(material);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnMaterial material) {
        return materialMapper.updateById(material);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] materialIds) {
        int count = 0;
        for (Long id : materialIds) {
            JnMaterial material = materialMapper.selectById(id);
            if (material != null) {
                material.setDelFlag("1");
                count += materialMapper.updateById(material);
            }
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getNextCode(String fanType, String fanModel, String category) {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String seqKey = "MATERIAL_" + fanType + "_" + fanModel + "_" + datePart;

        JnSequence sequence = sequenceMapper.selectBySeqKeyForUpdate(seqKey);
        if (sequence == null) {
            sequence = new JnSequence();
            sequence.setSeqKey(seqKey);
            sequence.setCurrentValue(1L);
            String prefix = buildCodePrefix(fanType, fanModel, category);
            sequence.setPrefix(prefix);
            sequence.setDescription("物料编码");
            sequenceMapper.insert(sequence);
        } else {
            sequenceMapper.incrementValue(seqKey);
            sequence.setCurrentValue(sequence.getCurrentValue() + 1);
        }

        return buildMaterialCode(sequence.getPrefix(), datePart, sequence.getCurrentValue());
    }

    @Override
    public boolean checkCodeUnique(String materialCode) {
        return materialMapper.selectByMaterialCode(materialCode) == null;
    }

    private String buildCodePrefix(String fanType, String fanModel, String category) {
        StringBuilder prefix = new StringBuilder();
        if (fanType != null && !fanType.isEmpty()) {
            switch (fanType) {
                case "CENTRIFUGAL":
                case "离心":
                    prefix.append("LX");
                    break;
                case "AXIAL":
                case "轴流":
                    prefix.append("ZL");
                    break;
                case "MIXED_FLOW":
                case "混流":
                    prefix.append("HL");
                    break;
                default:
                    prefix.append("QT");
                    break;
            }
        }
        if (fanModel != null && !fanModel.isEmpty()) {
            prefix.append("-").append(fanModel);
        }
        return prefix.toString();
    }

    private String buildMaterialCode(String prefix, String datePart, Long seq) {
        return prefix + "-" + datePart + "-" + String.format("%04d", seq);
    }
}
