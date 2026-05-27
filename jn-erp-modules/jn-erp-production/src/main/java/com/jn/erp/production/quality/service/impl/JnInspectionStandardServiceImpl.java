package com.jn.erp.production.quality.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.quality.domain.JnInspectionStandard;
import com.jn.erp.production.quality.mapper.JnInspectionStandardMapper;
import com.jn.erp.production.quality.service.IJnInspectionStandardService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JnInspectionStandardServiceImpl extends ServiceImpl<JnInspectionStandardMapper, JnInspectionStandard> implements IJnInspectionStandardService {

    @Autowired
    private JnInspectionStandardMapper inspectionStandardMapper;

    @Override
    public List<JnInspectionStandard> selectList(JnInspectionStandard query) {
        LambdaQueryWrapper<JnInspectionStandard> wrapper = new LambdaQueryWrapper<>();
        if (query.getStandardCode() != null && !query.getStandardCode().isEmpty()) {
            wrapper.like(JnInspectionStandard::getStandardCode, query.getStandardCode());
        }
        if (query.getStandardName() != null && !query.getStandardName().isEmpty()) {
            wrapper.like(JnInspectionStandard::getStandardName, query.getStandardName());
        }
        if (query.getStandardType() != null && !query.getStandardType().isEmpty()) {
            wrapper.eq(JnInspectionStandard::getStandardType, query.getStandardType());
        }
        if (query.getInspectionType() != null && !query.getInspectionType().isEmpty()) {
            wrapper.eq(JnInspectionStandard::getInspectionType, query.getInspectionType());
        }
        if (query.getIsActive() != null && !query.getIsActive().isEmpty()) {
            wrapper.eq(JnInspectionStandard::getIsActive, query.getIsActive());
        }
        wrapper.orderByDesc(JnInspectionStandard::getCreateTime);
        return inspectionStandardMapper.selectList(wrapper);
    }

    @Override
    public JnInspectionStandard selectById(Long id) {
        return inspectionStandardMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnInspectionStandard standard) {
        if (standard.getIsActive() == null) {
            standard.setIsActive("Y");
        }
        standard.setCreateBy(SecurityUtils.getUsername());
        return inspectionStandardMapper.insert(standard);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnInspectionStandard standard) {
        standard.setUpdateBy(SecurityUtils.getUsername());
        return inspectionStandardMapper.updateById(standard);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] ids) {
        int count = 0;
        for (Long id : ids) {
            count += inspectionStandardMapper.deleteById(id);
        }
        return count;
    }

    @Override
    public List<JnInspectionStandard> selectByMaterialOrOperation(Long materialId, Long operationId, String inspectionType) {
        return inspectionStandardMapper.selectByMaterialOrOperation(materialId, operationId, inspectionType);
    }
}
