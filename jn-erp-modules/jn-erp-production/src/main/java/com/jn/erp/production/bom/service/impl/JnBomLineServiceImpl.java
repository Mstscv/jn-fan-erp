package com.jn.erp.production.bom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.production.bom.domain.JnBomLine;
import com.jn.erp.production.bom.mapper.JnBomLineMapper;
import com.jn.erp.production.bom.service.IJnBomLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JnBomLineServiceImpl extends ServiceImpl<JnBomLineMapper, JnBomLine> implements IJnBomLineService {

    @Autowired
    private JnBomLineMapper bomLineMapper;

    @Override
    public List<JnBomLine> selectByBomId(Long bomId) {
        return bomLineMapper.selectByBomId(bomId);
    }

    @Override
    public JnBomLine selectById(Long lineId) {
        return bomLineMapper.selectById(lineId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnBomLine line) {
        if (line.getQuantity() == null) {
            line.setQuantity(java.math.BigDecimal.ONE);
        }
        if (line.getScrapRate() == null) {
            line.setScrapRate(java.math.BigDecimal.ZERO);
        }
        if (line.getIsOptional() == null) {
            line.setIsOptional("N");
        }
        if (line.getDefaultSelected() == null) {
            line.setDefaultSelected("N");
        }
        if (line.getSortOrder() == null) {
            line.setSortOrder(0);
        }
        return bomLineMapper.insert(line);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnBomLine line) {
        return bomLineMapper.updateById(line);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] lineIds) {
        int count = 0;
        for (Long id : lineIds) {
            count += bomLineMapper.deleteById(id);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByBomId(Long bomId) {
        return bomLineMapper.deleteByBomId(bomId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsert(List<JnBomLine> lines) {
        int count = 0;
        for (JnBomLine line : lines) {
            count += insert(line);
        }
        return count;
    }
}
