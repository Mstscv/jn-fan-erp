package com.jn.erp.sales.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jn.erp.common.core.utils.SequenceUtils;
import com.jn.erp.sales.delivery.domain.JnDeliveryNote;
import com.jn.erp.sales.delivery.mapper.JnDeliveryNoteMapper;
import com.jn.erp.sales.delivery.service.IJnDeliveryNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class JnDeliveryNoteServiceImpl implements IJnDeliveryNoteService {

    @Autowired
    private JnDeliveryNoteMapper jnDeliveryNoteMapper;

    @Autowired
    private SequenceUtils sequenceUtils;

    @Override
    public List<JnDeliveryNote> selectList(JnDeliveryNote query) {
        LambdaQueryWrapper<JnDeliveryNote> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(JnDeliveryNote::getDelFlag, "0");
        if (query != null) {
            if (query.getDeliveryNo() != null && !query.getDeliveryNo().isEmpty()) {
                wrapper.like(JnDeliveryNote::getDeliveryNo, query.getDeliveryNo());
            }
            if (query.getCustomerId() != null) {
                wrapper.eq(JnDeliveryNote::getCustomerId, query.getCustomerId());
            }
            if (query.getCustomerName() != null && !query.getCustomerName().isEmpty()) {
                wrapper.like(JnDeliveryNote::getCustomerName, query.getCustomerName());
            }
            if (query.getStatus() != null && !query.getStatus().isEmpty()) {
                wrapper.eq(JnDeliveryNote::getStatus, query.getStatus());
            }
            if (query.getOrderId() != null) {
                wrapper.eq(JnDeliveryNote::getOrderId, query.getOrderId());
            }
        }
        wrapper.orderByDesc(JnDeliveryNote::getCreateTime);
        return jnDeliveryNoteMapper.selectList(wrapper);
    }

    @Override
    public JnDeliveryNote getById(Long deliveryId) {
        return jnDeliveryNoteMapper.selectById(deliveryId);
    }

    @Override
    public int insert(JnDeliveryNote deliveryNote) {
        deliveryNote.setDeliveryNo(generateDeliveryNo());
        deliveryNote.setDelFlag("0");
        if (deliveryNote.getStatus() == null) {
            deliveryNote.setStatus("0");
        }
        return jnDeliveryNoteMapper.insert(deliveryNote);
    }

    @Override
    public int update(JnDeliveryNote deliveryNote) {
        return jnDeliveryNoteMapper.updateById(deliveryNote);
    }

    @Override
    public int deleteByIds(Long[] deliveryIds) {
        List<Long> ids = Arrays.asList(deliveryIds);
        LambdaQueryWrapper<JnDeliveryNote> wrapper = Wrappers.lambdaQuery();
        wrapper.in(JnDeliveryNote::getDeliveryId, ids);
        JnDeliveryNote updateEntity = new JnDeliveryNote();
        updateEntity.setDelFlag("1");
        return jnDeliveryNoteMapper.update(updateEntity, wrapper);
    }

    @Override
    public int markShipped(Long deliveryId) {
        JnDeliveryNote note = new JnDeliveryNote();
        note.setDeliveryId(deliveryId);
        note.setStatus("1");
        return jnDeliveryNoteMapper.updateById(note);
    }

    private String generateDeliveryNo() {
        return sequenceUtils.generate("DELIVERY_NOTE_NO");
    }
}
