package com.jn.erp.production.routing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.routing.domain.JnWorkCenter;
import com.jn.erp.production.routing.mapper.JnWorkCenterMapper;
import com.jn.erp.production.routing.service.IJnWorkCenterService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class JnWorkCenterServiceImpl extends ServiceImpl<JnWorkCenterMapper, JnWorkCenter> implements IJnWorkCenterService {

    @Autowired
    private JnWorkCenterMapper workCenterMapper;

    @Override
    public List<JnWorkCenter> selectList(JnWorkCenter query) {
        LambdaQueryWrapper<JnWorkCenter> wrapper = new LambdaQueryWrapper<>();
        if (query.getCenterCode() != null && !query.getCenterCode().isEmpty()) {
            wrapper.like(JnWorkCenter::getCenterCode, query.getCenterCode());
        }
        if (query.getCenterName() != null && !query.getCenterName().isEmpty()) {
            wrapper.like(JnWorkCenter::getCenterName, query.getCenterName());
        }
        if (query.getCenterType() != null && !query.getCenterType().isEmpty()) {
            wrapper.eq(JnWorkCenter::getCenterType, query.getCenterType());
        }
        if (query.getIsActive() != null && !query.getIsActive().isEmpty()) {
            wrapper.eq(JnWorkCenter::getIsActive, query.getIsActive());
        }
        wrapper.orderByDesc(JnWorkCenter::getCreateTime);
        return workCenterMapper.selectList(wrapper);
    }

    @Override
    public JnWorkCenter selectById(Long id) {
        return workCenterMapper.selectById(id);
    }

    @Override
    public int insert(JnWorkCenter center) {
        center.setCreateBy(SecurityUtils.getUsername());
        return workCenterMapper.insert(center);
    }

    @Override
    public int update(JnWorkCenter center) {
        center.setUpdateBy(SecurityUtils.getUsername());
        return workCenterMapper.updateById(center);
    }

    @Override
    public int deleteByIds(Long[] ids) {
        return workCenterMapper.deleteBatchIds(Arrays.asList(ids));
    }
}
