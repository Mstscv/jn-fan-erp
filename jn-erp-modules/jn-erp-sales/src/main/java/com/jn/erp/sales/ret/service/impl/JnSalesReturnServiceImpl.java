package com.jn.erp.sales.ret.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jn.erp.common.core.utils.SequenceUtils;
import com.jn.erp.sales.ret.domain.JnSalesReturn;
import com.jn.erp.sales.ret.mapper.JnSalesReturnMapper;
import com.jn.erp.sales.ret.service.IJnSalesReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class JnSalesReturnServiceImpl implements IJnSalesReturnService {

    @Autowired
    private JnSalesReturnMapper jnSalesReturnMapper;

    @Autowired
    private SequenceUtils sequenceUtils;

    @Override
    public List<JnSalesReturn> selectList(JnSalesReturn query) {
        LambdaQueryWrapper<JnSalesReturn> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(JnSalesReturn::getDelFlag, "0");
        if (query != null) {
            if (query.getReturnNo() != null && !query.getReturnNo().isEmpty()) {
                wrapper.like(JnSalesReturn::getReturnNo, query.getReturnNo());
            }
            if (query.getCustomerId() != null) {
                wrapper.eq(JnSalesReturn::getCustomerId, query.getCustomerId());
            }
            if (query.getOrderId() != null) {
                wrapper.eq(JnSalesReturn::getOrderId, query.getOrderId());
            }
            if (query.getStatus() != null && !query.getStatus().isEmpty()) {
                wrapper.eq(JnSalesReturn::getStatus, query.getStatus());
            }
        }
        wrapper.orderByDesc(JnSalesReturn::getCreateTime);
        return jnSalesReturnMapper.selectList(wrapper);
    }

    @Override
    public JnSalesReturn getById(Long returnId) {
        return jnSalesReturnMapper.selectById(returnId);
    }

    @Override
    public int insert(JnSalesReturn salesReturn) {
        salesReturn.setReturnNo(generateReturnNo());
        salesReturn.setDelFlag("0");
        if (salesReturn.getStatus() == null) {
            salesReturn.setStatus("0");
        }
        return jnSalesReturnMapper.insert(salesReturn);
    }

    @Override
    public int update(JnSalesReturn salesReturn) {
        return jnSalesReturnMapper.updateById(salesReturn);
    }

    @Override
    public int deleteByIds(Long[] returnIds) {
        List<Long> ids = Arrays.asList(returnIds);
        LambdaQueryWrapper<JnSalesReturn> wrapper = Wrappers.lambdaQuery();
        wrapper.in(JnSalesReturn::getReturnId, ids);
        JnSalesReturn updateEntity = new JnSalesReturn();
        updateEntity.setDelFlag("1");
        return jnSalesReturnMapper.update(updateEntity, wrapper);
    }

    @Override
    public int updateStatus(Long returnId, String newStatus) {
        JnSalesReturn entity = new JnSalesReturn();
        entity.setReturnId(returnId);
        entity.setStatus(newStatus);
        return jnSalesReturnMapper.updateById(entity);
    }

    private String generateReturnNo() {
        return sequenceUtils.generate("SALES_RETURN_NO");
    }
}
