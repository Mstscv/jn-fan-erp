package com.jn.erp.purchase.ret.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jn.erp.common.core.utils.SequenceUtils;
import com.jn.erp.purchase.ret.domain.JnPurchaseReturn;
import com.jn.erp.purchase.ret.mapper.JnPurchaseReturnMapper;
import com.jn.erp.purchase.ret.service.IJnPurchaseReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class JnPurchaseReturnServiceImpl implements IJnPurchaseReturnService {

    @Autowired
    private JnPurchaseReturnMapper purchaseReturnMapper;

    @Autowired
    private SequenceUtils sequenceUtils;

    @Override
    public List<JnPurchaseReturn> selectList(JnPurchaseReturn ret) {
        LambdaQueryWrapper<JnPurchaseReturn> wrapper = Wrappers.lambdaQuery();
        if (ret != null) {
            if (ret.getReturnNo() != null && !ret.getReturnNo().isEmpty()) {
                wrapper.like(JnPurchaseReturn::getReturnNo, ret.getReturnNo());
            }
            if (ret.getPoId() != null) {
                wrapper.eq(JnPurchaseReturn::getPoId, ret.getPoId());
            }
            if (ret.getSupplierId() != null) {
                wrapper.eq(JnPurchaseReturn::getSupplierId, ret.getSupplierId());
            }
            if (ret.getMaterialId() != null) {
                wrapper.eq(JnPurchaseReturn::getMaterialId, ret.getMaterialId());
            }
            if (ret.getStatus() != null && !ret.getStatus().isEmpty()) {
                wrapper.eq(JnPurchaseReturn::getStatus, ret.getStatus());
            }
        }
        wrapper.orderByDesc(JnPurchaseReturn::getCreateTime);
        return purchaseReturnMapper.selectList(wrapper);
    }

    @Override
    public JnPurchaseReturn getById(Long returnId) {
        return purchaseReturnMapper.selectById(returnId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnPurchaseReturn ret) {
        ret.setReturnNo(sequenceUtils.generate("RETURN_NO"));
        ret.setStatus("0");
        if (ret.getQuantity() == null) {
            ret.setQuantity(0);
        }
        if (ret.getReturnDate() == null) {
            ret.setReturnDate(LocalDate.now());
        }
        return purchaseReturnMapper.insert(ret);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnPurchaseReturn ret) {
        return purchaseReturnMapper.updateById(ret);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] returnIds) {
        List<Long> ids = Arrays.asList(returnIds);
        return purchaseReturnMapper.deleteByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(Long returnId, String status) {
        JnPurchaseReturn ret = new JnPurchaseReturn();
        ret.setReturnId(returnId);
        ret.setStatus(status);
        return purchaseReturnMapper.updateById(ret);
    }
}
