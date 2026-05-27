package com.jn.erp.purchase.receive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jn.erp.common.core.utils.SequenceUtils;
import com.jn.erp.purchase.po.domain.JnPurchaseOrder;
import com.jn.erp.purchase.po.mapper.JnPurchaseOrderMapper;
import com.jn.erp.purchase.receive.domain.JnPurchaseReceive;
import com.jn.erp.purchase.receive.mapper.JnPurchaseReceiveMapper;
import com.jn.erp.purchase.receive.service.IJnPurchaseReceiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class JnPurchaseReceiveServiceImpl implements IJnPurchaseReceiveService {

    @Autowired
    private JnPurchaseReceiveMapper purchaseReceiveMapper;

    @Autowired
    private JnPurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    private SequenceUtils sequenceUtils;

    @Override
    public List<JnPurchaseReceive> selectList(JnPurchaseReceive receive) {
        LambdaQueryWrapper<JnPurchaseReceive> wrapper = Wrappers.lambdaQuery();
        if (receive != null) {
            if (receive.getReceiveNo() != null && !receive.getReceiveNo().isEmpty()) {
                wrapper.like(JnPurchaseReceive::getReceiveNo, receive.getReceiveNo());
            }
            if (receive.getPoId() != null) {
                wrapper.eq(JnPurchaseReceive::getPoId, receive.getPoId());
            }
            if (receive.getSupplierId() != null) {
                wrapper.eq(JnPurchaseReceive::getSupplierId, receive.getSupplierId());
            }
            if (receive.getStatus() != null && !receive.getStatus().isEmpty()) {
                wrapper.eq(JnPurchaseReceive::getStatus, receive.getStatus());
            }
            if (receive.getQcResult() != null && !receive.getQcResult().isEmpty()) {
                wrapper.eq(JnPurchaseReceive::getQcResult, receive.getQcResult());
            }
        }
        wrapper.orderByDesc(JnPurchaseReceive::getCreateTime);
        return purchaseReceiveMapper.selectList(wrapper);
    }

    @Override
    public JnPurchaseReceive getById(Long receiveId) {
        return purchaseReceiveMapper.selectById(receiveId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnPurchaseReceive receive) {
        receive.setReceiveNo(sequenceUtils.generate("RECEIVE_NO"));
        receive.setStatus("0");
        receive.setQcResult("0");
        if (receive.getTotalQty() == null) {
            receive.setTotalQty(0);
        }
        if (receive.getOkQty() == null) {
            receive.setOkQty(0);
        }
        if (receive.getBadQty() == null) {
            receive.setBadQty(0);
        }
        if (receive.getReceiveDate() == null) {
            receive.setReceiveDate(LocalDate.now());
        }
        return purchaseReceiveMapper.insert(receive);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnPurchaseReceive receive) {
        return purchaseReceiveMapper.updateById(receive);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] receiveIds) {
        List<Long> ids = Arrays.asList(receiveIds);
        return purchaseReceiveMapper.deleteByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateQcResult(Long receiveId, String qcResult) {
        JnPurchaseReceive receive = new JnPurchaseReceive();
        receive.setReceiveId(receiveId);
        receive.setQcResult(qcResult);
        return purchaseReceiveMapper.updateById(receive);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JnPurchaseReceive createFromPo(Long poId, Integer totalQty, Integer okQty, Integer badQty, String remark) {
        JnPurchaseOrder po = purchaseOrderMapper.selectById(poId);
        if (po == null) {
            throw new RuntimeException("采购订单不存在: " + poId);
        }

        JnPurchaseReceive receive = new JnPurchaseReceive();
        receive.setReceiveNo(sequenceUtils.generate("RECEIVE_NO"));
        receive.setPoId(poId);
        receive.setSupplierId(po.getSupplierId());
        receive.setReceiveDate(LocalDate.now());
        receive.setTotalQty(totalQty != null ? totalQty : 0);
        receive.setOkQty(okQty != null ? okQty : 0);
        receive.setBadQty(badQty != null ? badQty : 0);
        receive.setQcResult("0");
        receive.setStatus("1");
        receive.setRemark(remark);
        receive.setCreateBy(po.getCreateBy());
        purchaseReceiveMapper.insert(receive);

        JnPurchaseOrder updatedPo = new JnPurchaseOrder();
        updatedPo.setPoId(poId);
        updatedPo.setStatus("PARTIAL");
        purchaseOrderMapper.updateById(updatedPo);

        return receive;
    }
}
