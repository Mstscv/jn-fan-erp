package com.jn.erp.production.routing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.routing.domain.JnOperation;
import com.jn.erp.production.routing.mapper.JnOperationMapper;
import com.jn.erp.production.routing.service.IJnOperationService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class JnOperationServiceImpl extends ServiceImpl<JnOperationMapper, JnOperation> implements IJnOperationService {

    @Autowired
    private JnOperationMapper operationMapper;

    @Override
    public List<JnOperation> selectList(JnOperation query) {
        LambdaQueryWrapper<JnOperation> wrapper = new LambdaQueryWrapper<>();
        if (query.getOperationCode() != null && !query.getOperationCode().isEmpty()) {
            wrapper.like(JnOperation::getOperationCode, query.getOperationCode());
        }
        if (query.getOperationName() != null && !query.getOperationName().isEmpty()) {
            wrapper.like(JnOperation::getOperationName, query.getOperationName());
        }
        if (query.getOperationType() != null && !query.getOperationType().isEmpty()) {
            wrapper.eq(JnOperation::getOperationType, query.getOperationType());
        }
        if (query.getIsActive() != null && !query.getIsActive().isEmpty()) {
            wrapper.eq(JnOperation::getIsActive, query.getIsActive());
        }
        wrapper.orderByDesc(JnOperation::getCreateTime);
        return operationMapper.selectList(wrapper);
    }

    @Override
    public JnOperation selectById(Long id) {
        return operationMapper.selectById(id);
    }

    @Override
    public int insert(JnOperation operation) {
        operation.setCreateBy(SecurityUtils.getUsername());
        return operationMapper.insert(operation);
    }

    @Override
    public int update(JnOperation operation) {
        operation.setUpdateBy(SecurityUtils.getUsername());
        return operationMapper.updateById(operation);
    }

    @Override
    public int deleteByIds(Long[] ids) {
        return operationMapper.deleteBatchIds(Arrays.asList(ids));
    }
}
