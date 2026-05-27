package com.jn.erp.sales.quotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jn.erp.common.core.utils.SequenceUtils;
import com.jn.erp.sales.quotation.domain.JnQuotationLine;
import com.jn.erp.sales.quotation.domain.JnSalesQuotation;
import com.jn.erp.sales.quotation.mapper.JnQuotationLineMapper;
import com.jn.erp.sales.quotation.mapper.JnSalesQuotationMapper;
import com.jn.erp.sales.quotation.service.IJnSalesQuotationService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class JnSalesQuotationServiceImpl implements IJnSalesQuotationService {

    @Autowired
    private JnSalesQuotationMapper jnSalesQuotationMapper;

    @Autowired
    private JnQuotationLineMapper jnQuotationLineMapper;

    @Autowired
    private SequenceUtils sequenceUtils;

    @Override
    public List<JnSalesQuotation> selectList(JnSalesQuotation query) {
        LambdaQueryWrapper<JnSalesQuotation> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(JnSalesQuotation::getDelFlag, "0");
        if (query != null) {
            if (query.getStatus() != null && !query.getStatus().isEmpty()) {
                wrapper.eq(JnSalesQuotation::getStatus, query.getStatus());
            }
            if (query.getCustomerName() != null && !query.getCustomerName().isEmpty()) {
                wrapper.like(JnSalesQuotation::getCustomerName, query.getCustomerName());
            }
            if (query.getQuotationNo() != null && !query.getQuotationNo().isEmpty()) {
                wrapper.like(JnSalesQuotation::getQuotationNo, query.getQuotationNo());
            }
            if (query.getSalesman() != null && !query.getSalesman().isEmpty()) {
                wrapper.like(JnSalesQuotation::getSalesman, query.getSalesman());
            }
        }
        wrapper.orderByDesc(JnSalesQuotation::getCreateTime);
        return jnSalesQuotationMapper.selectList(wrapper);
    }

    @Override
    public JnSalesQuotation getById(Long quotationId) {
        JnSalesQuotation quotation = jnSalesQuotationMapper.selectById(quotationId);
        if (quotation != null) {
            LambdaQueryWrapper<JnQuotationLine> lineWrapper = Wrappers.lambdaQuery();
            lineWrapper.eq(JnQuotationLine::getQuotationId, quotationId);
            lineWrapper.orderByAsc(JnQuotationLine::getSortOrder);
            List<JnQuotationLine> lines = jnQuotationLineMapper.selectList(lineWrapper);
            quotation.setLines(lines);
        }
        return quotation;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWithLines(JnSalesQuotation quotation, List<JnQuotationLine> lines) {
        quotation.setQuotationNo(generateQuotationNo());
        quotation.setDelFlag("0");
        if (quotation.getStatus() == null) {
            quotation.setStatus("0");
        }
        if (quotation.getValidDays() == null) {
            quotation.setValidDays(30);
        }
        if (quotation.getValidDays() != null && quotation.getValidUntil() == null) {
            quotation.setValidUntil(LocalDate.now().plusDays(quotation.getValidDays()));
        }
        int rows = jnSalesQuotationMapper.insert(quotation);
        if (lines != null && !lines.isEmpty()) {
            for (int i = 0; i < lines.size(); i++) {
                JnQuotationLine line = lines.get(i);
                line.setQuotationId(quotation.getQuotationId());
                if (line.getSortOrder() == null) {
                    line.setSortOrder(i);
                }
                if (line.getQuantity() == null) {
                    line.setQuantity(1);
                }
            }
            jnQuotationLineMapper.insert(lines);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWithLines(JnSalesQuotation quotation, List<JnQuotationLine> lines) {
        if (quotation.getValidDays() != null && quotation.getValidUntil() == null) {
            quotation.setValidUntil(LocalDate.now().plusDays(quotation.getValidDays()));
        }
        int rows = jnSalesQuotationMapper.updateById(quotation);

        LambdaQueryWrapper<JnQuotationLine> deleteWrapper = Wrappers.lambdaQuery();
        deleteWrapper.eq(JnQuotationLine::getQuotationId, quotation.getQuotationId());
        jnQuotationLineMapper.delete(deleteWrapper);

        if (lines != null && !lines.isEmpty()) {
            for (int i = 0; i < lines.size(); i++) {
                JnQuotationLine line = lines.get(i);
                line.setLineId(null);
                line.setQuotationId(quotation.getQuotationId());
                if (line.getSortOrder() == null) {
                    line.setSortOrder(i);
                }
                if (line.getQuantity() == null) {
                    line.setQuantity(1);
                }
            }
            jnQuotationLineMapper.insert(lines);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] quotationIds) {
        List<Long> ids = Arrays.asList(quotationIds);
        LambdaQueryWrapper<JnSalesQuotation> wrapper = Wrappers.lambdaQuery();
        wrapper.in(JnSalesQuotation::getQuotationId, ids);
        JnSalesQuotation updateEntity = new JnSalesQuotation();
        updateEntity.setDelFlag("1");
        int rows = jnSalesQuotationMapper.update(updateEntity, wrapper);

        LambdaQueryWrapper<JnQuotationLine> lineDeleteWrapper = Wrappers.lambdaQuery();
        lineDeleteWrapper.in(JnQuotationLine::getQuotationId, ids);
        jnQuotationLineMapper.delete(lineDeleteWrapper);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long quotationId) {
        JnSalesQuotation quotation = jnSalesQuotationMapper.selectById(quotationId);
        if (quotation == null) {
            throw new RuntimeException("报价单不存在");
        }
        if (!"0".equals(quotation.getStatus())) {
            throw new RuntimeException("报价单状态不允许审核");
        }
        quotation.setStatus("1");
        quotation.setApproveBy(SecurityUtils.getUsername());
        quotation.setApproveTime(LocalDateTime.now());
        jnSalesQuotationMapper.updateById(quotation);
    }

    @Override
    public String generateQuotationNo() {
        return sequenceUtils.generate("QUOTATION_NO");
    }
}
