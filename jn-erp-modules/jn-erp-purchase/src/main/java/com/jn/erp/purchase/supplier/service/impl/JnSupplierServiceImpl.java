package com.jn.erp.purchase.supplier.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jn.erp.common.core.utils.SequenceUtils;
import com.jn.erp.purchase.supplier.domain.JnSupplier;
import com.jn.erp.purchase.supplier.mapper.JnSupplierMapper;
import com.jn.erp.purchase.supplier.service.IJnSupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class JnSupplierServiceImpl implements IJnSupplierService {

    @Autowired
    private JnSupplierMapper jnSupplierMapper;

    @Autowired
    private SequenceUtils sequenceUtils;

    @Override
    public List<JnSupplier> selectList(JnSupplier supplier) {
        LambdaQueryWrapper<JnSupplier> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(JnSupplier::getDelFlag, "0");
        if (supplier != null) {
            if (supplier.getSupplierCode() != null && !supplier.getSupplierCode().isEmpty()) {
                wrapper.like(JnSupplier::getSupplierCode, supplier.getSupplierCode());
            }
            if (supplier.getSupplierName() != null && !supplier.getSupplierName().isEmpty()) {
                wrapper.like(JnSupplier::getSupplierName, supplier.getSupplierName());
            }
            if (supplier.getContact() != null && !supplier.getContact().isEmpty()) {
                wrapper.like(JnSupplier::getContact, supplier.getContact());
            }
            if (supplier.getPhone() != null && !supplier.getPhone().isEmpty()) {
                wrapper.like(JnSupplier::getPhone, supplier.getPhone());
            }
            if (supplier.getBizScope() != null && !supplier.getBizScope().isEmpty()) {
                wrapper.like(JnSupplier::getBizScope, supplier.getBizScope());
            }
            if (supplier.getCooperation() != null && !supplier.getCooperation().isEmpty()) {
                wrapper.eq(JnSupplier::getCooperation, supplier.getCooperation());
            }
            if (supplier.getRating() != null) {
                wrapper.eq(JnSupplier::getRating, supplier.getRating());
            }
        }
        wrapper.orderByDesc(JnSupplier::getCreateTime);
        return jnSupplierMapper.selectList(wrapper);
    }

    @Override
    public JnSupplier getById(Long supplierId) {
        return jnSupplierMapper.selectById(supplierId);
    }

    @Override
    public int insert(JnSupplier supplier) {
        supplier.setSupplierCode(generateSupplierCode());
        supplier.setDelFlag("0");
        if (supplier.getRating() == null) {
            supplier.setRating(3);
        }
        if (supplier.getCooperation() == null) {
            supplier.setCooperation("0");
        }
        return jnSupplierMapper.insert(supplier);
    }

    @Override
    public int update(JnSupplier supplier) {
        return jnSupplierMapper.updateById(supplier);
    }

    @Override
    public int deleteByIds(Long[] supplierIds) {
        List<Long> ids = Arrays.asList(supplierIds);
        LambdaQueryWrapper<JnSupplier> wrapper = Wrappers.lambdaQuery();
        wrapper.in(JnSupplier::getSupplierId, ids);
        JnSupplier updateEntity = new JnSupplier();
        updateEntity.setDelFlag("1");
        return jnSupplierMapper.update(updateEntity, wrapper);
    }

    private String generateSupplierCode() {
        return sequenceUtils.generate("SUPPLIER_CODE");
    }
}
