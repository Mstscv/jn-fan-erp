package com.jn.erp.production.outsource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.outsource.domain.JnOutsourceDispatch;
import com.jn.erp.production.outsource.domain.JnOutsourceDispatchLine;
import com.jn.erp.production.outsource.mapper.JnOutsourceDispatchLineMapper;
import com.jn.erp.production.outsource.mapper.JnOutsourceDispatchMapper;
import com.jn.erp.production.outsource.service.IJnOutsourceDispatchService;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Service
public class JnOutsourceDispatchServiceImpl extends ServiceImpl<JnOutsourceDispatchMapper, JnOutsourceDispatch> implements IJnOutsourceDispatchService {

    @Autowired
    private JnOutsourceDispatchMapper outsourceDispatchMapper;

    @Autowired
    private JnOutsourceDispatchLineMapper outsourceDispatchLineMapper;

    @Override
    public List<JnOutsourceDispatch> selectList(JnOutsourceDispatch query) {
        LambdaQueryWrapper<JnOutsourceDispatch> wrapper = new LambdaQueryWrapper<>();
        if (query.getDispatchNo() != null && !query.getDispatchNo().isEmpty()) {
            wrapper.like(JnOutsourceDispatch::getDispatchNo, query.getDispatchNo());
        }
        if (query.getOrderNo() != null && !query.getOrderNo().isEmpty()) {
            wrapper.like(JnOutsourceDispatch::getOrderNo, query.getOrderNo());
        }
        if (query.getSupplierName() != null && !query.getSupplierName().isEmpty()) {
            wrapper.like(JnOutsourceDispatch::getSupplierName, query.getSupplierName());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(JnOutsourceDispatch::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(JnOutsourceDispatch::getCreateTime);
        return outsourceDispatchMapper.selectList(wrapper);
    }

    @Override
    public JnOutsourceDispatch selectById(Long id) {
        return outsourceDispatchMapper.selectById(id);
    }

    @Override
    public JnOutsourceDispatch selectWithLines(Long id) {
        JnOutsourceDispatch dispatch = outsourceDispatchMapper.selectById(id);
        if (dispatch != null) {
            List<JnOutsourceDispatchLine> lines = outsourceDispatchLineMapper.selectByDispatchId(id);
            dispatch.setParams(new HashMap<>());
            dispatch.getParams().put("lines", lines);
        }
        return dispatch;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnOutsourceDispatch dispatch, List<JnOutsourceDispatchLine> lines) {
        dispatch.setDispatchNo(generateDispatchNo());
        if (dispatch.getStatus() == null) {
            dispatch.setStatus("DRAFT");
        }
        dispatch.setCreateBy(SecurityUtils.getUsername());
        int result = outsourceDispatchMapper.insert(dispatch);

        if (lines != null && !lines.isEmpty()) {
            for (JnOutsourceDispatchLine line : lines) {
                line.setDispatchId(dispatch.getDispatchId());
                if (line.getSortOrder() == null) {
                    line.setSortOrder(0);
                }
                outsourceDispatchLineMapper.insert(line);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnOutsourceDispatch dispatch, List<JnOutsourceDispatchLine> lines) {
        dispatch.setUpdateBy(SecurityUtils.getUsername());
        int result = outsourceDispatchMapper.updateById(dispatch);

        outsourceDispatchLineMapper.deleteByDispatchId(dispatch.getDispatchId());

        if (lines != null && !lines.isEmpty()) {
            for (JnOutsourceDispatchLine line : lines) {
                line.setLineId(null);
                line.setDispatchId(dispatch.getDispatchId());
                if (line.getSortOrder() == null) {
                    line.setSortOrder(0);
                }
                outsourceDispatchLineMapper.insert(line);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] ids) {
        int count = 0;
        for (Long id : ids) {
            outsourceDispatchLineMapper.deleteByDispatchId(id);
            count += outsourceDispatchMapper.deleteById(id);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long dispatchId) {
        JnOutsourceDispatch dispatch = outsourceDispatchMapper.selectById(dispatchId);
        if (dispatch == null) {
            throw new RuntimeException("委外发料单不存在: " + dispatchId);
        }
        dispatch.setStatus("APPROVED");
        dispatch.setUpdateBy(SecurityUtils.getUsername());
        outsourceDispatchMapper.updateById(dispatch);
    }

    private String generateDispatchNo() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "OD-" + datePart + "-";
        LambdaQueryWrapper<JnOutsourceDispatch> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(JnOutsourceDispatch::getDispatchNo, prefix);
        wrapper.orderByDesc(JnOutsourceDispatch::getDispatchNo);
        wrapper.last("LIMIT 1");
        JnOutsourceDispatch last = outsourceDispatchMapper.selectOne(wrapper);
        int seq = 1;
        if (last != null && last.getDispatchNo() != null) {
            String lastCode = last.getDispatchNo();
            String seqStr = lastCode.substring(lastCode.lastIndexOf("-") + 1);
            try {
                seq = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException e) {
                seq = 1;
            }
        }
        return prefix + String.format("%04d", seq);
    }

}
