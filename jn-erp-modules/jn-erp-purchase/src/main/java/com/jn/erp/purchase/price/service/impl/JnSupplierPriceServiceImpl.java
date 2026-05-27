package com.jn.erp.purchase.price.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jn.erp.purchase.price.domain.JnSupplierPrice;
import com.jn.erp.purchase.price.mapper.JnSupplierPriceMapper;
import com.jn.erp.purchase.price.service.IJnSupplierPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class JnSupplierPriceServiceImpl implements IJnSupplierPriceService {

    @Autowired
    private JnSupplierPriceMapper jnSupplierPriceMapper;

    @Override
    public List<JnSupplierPrice> selectList(JnSupplierPrice price) {
        LambdaQueryWrapper<JnSupplierPrice> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(JnSupplierPrice::getDelFlag, "0");
        if (price != null) {
            if (price.getSupplierId() != null) {
                wrapper.eq(JnSupplierPrice::getSupplierId, price.getSupplierId());
            }
            if (price.getSupplierName() != null && !price.getSupplierName().isEmpty()) {
                wrapper.like(JnSupplierPrice::getSupplierName, price.getSupplierName());
            }
            if (price.getMaterialId() != null) {
                wrapper.eq(JnSupplierPrice::getMaterialId, price.getMaterialId());
            }
            if (price.getMaterialCode() != null && !price.getMaterialCode().isEmpty()) {
                wrapper.like(JnSupplierPrice::getMaterialCode, price.getMaterialCode());
            }
            if (price.getMaterialName() != null && !price.getMaterialName().isEmpty()) {
                wrapper.like(JnSupplierPrice::getMaterialName, price.getMaterialName());
            }
            if (price.getSpec() != null && !price.getSpec().isEmpty()) {
                wrapper.like(JnSupplierPrice::getSpec, price.getSpec());
            }
            if (price.getStatus() != null && !price.getStatus().isEmpty()) {
                wrapper.eq(JnSupplierPrice::getStatus, price.getStatus());
            }
        }
        wrapper.orderByDesc(JnSupplierPrice::getCreateTime);
        return jnSupplierPriceMapper.selectList(wrapper);
    }

    @Override
    public JnSupplierPrice getById(Long priceId) {
        return jnSupplierPriceMapper.selectById(priceId);
    }

    @Override
    public int insert(JnSupplierPrice price) {
        price.setDelFlag("0");
        if (price.getStatus() == null) {
            price.setStatus("0");
        }
        if (price.getMinOrderQty() == null) {
            price.setMinOrderQty(1);
        }
        if (price.getLeadTimeDays() == null) {
            price.setLeadTimeDays(30);
        }
        return jnSupplierPriceMapper.insert(price);
    }

    @Override
    public int update(JnSupplierPrice price) {
        return jnSupplierPriceMapper.updateById(price);
    }

    @Override
    public int deleteByIds(Long[] priceIds) {
        List<Long> ids = Arrays.asList(priceIds);
        LambdaQueryWrapper<JnSupplierPrice> wrapper = Wrappers.lambdaQuery();
        wrapper.in(JnSupplierPrice::getPriceId, ids);
        JnSupplierPrice updateEntity = new JnSupplierPrice();
        updateEntity.setDelFlag("1");
        return jnSupplierPriceMapper.update(updateEntity, wrapper);
    }

    @Override
    public JnSupplierPrice getBestPrice(Long supplierId, Long materialId) {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<JnSupplierPrice> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(JnSupplierPrice::getDelFlag, "0")
                .eq(JnSupplierPrice::getStatus, "0")
                .eq(JnSupplierPrice::getSupplierId, supplierId)
                .eq(JnSupplierPrice::getMaterialId, materialId)
                .le(JnSupplierPrice::getEffectiveDate, today)
                .and(w -> w.isNull(JnSupplierPrice::getExpireDate)
                        .or(w2 -> w2.ge(JnSupplierPrice::getExpireDate, today)))
                .orderByAsc(JnSupplierPrice::getUnitPrice)
                .last("LIMIT 1");
        return jnSupplierPriceMapper.selectOne(wrapper);
    }

    @Override
    public List<JnSupplierPrice> getPriceHistory(Long materialId, Long supplierId) {
        LambdaQueryWrapper<JnSupplierPrice> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(JnSupplierPrice::getDelFlag, "0");
        if (materialId != null) {
            wrapper.eq(JnSupplierPrice::getMaterialId, materialId);
        }
        if (supplierId != null) {
            wrapper.eq(JnSupplierPrice::getSupplierId, supplierId);
        }
        wrapper.orderByDesc(JnSupplierPrice::getCreateTime);
        return jnSupplierPriceMapper.selectList(wrapper);
    }
}
