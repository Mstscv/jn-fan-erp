package com.jn.erp.warehouse.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jn.erp.common.core.utils.SequenceUtils;
import com.jn.erp.warehouse.check.domain.JnCheckLine;
import com.jn.erp.warehouse.check.domain.JnInventoryCheck;
import com.jn.erp.warehouse.check.mapper.JnCheckLineMapper;
import com.jn.erp.warehouse.check.mapper.JnCheckMapper;
import com.jn.erp.warehouse.check.service.IJnCheckService;
import com.jn.erp.warehouse.inventory.domain.JnInventory;
import com.jn.erp.warehouse.inventory.mapper.JnInventoryMapper;
import com.jn.erp.warehouse.log.domain.JnInventoryLog;
import com.jn.erp.warehouse.log.mapper.JnInventoryLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class JnCheckServiceImpl implements IJnCheckService {

    @Autowired
    private JnCheckMapper checkMapper;

    @Autowired
    private JnCheckLineMapper checkLineMapper;

    @Autowired
    private JnInventoryMapper inventoryMapper;

    @Autowired
    private JnInventoryLogMapper inventoryLogMapper;

    @Autowired
    private SequenceUtils sequenceUtils;

    @Override
    public List<JnInventoryCheck> selectList(JnInventoryCheck check) {
        LambdaQueryWrapper<JnInventoryCheck> wrapper = Wrappers.lambdaQuery();
        if (check != null) {
            if (check.getCheckNo() != null && !check.getCheckNo().isEmpty()) {
                wrapper.like(JnInventoryCheck::getCheckNo, check.getCheckNo());
            }
            if (check.getWhId() != null) {
                wrapper.eq(JnInventoryCheck::getWhId, check.getWhId());
            }
            if (check.getStatus() != null && !check.getStatus().isEmpty()) {
                wrapper.eq(JnInventoryCheck::getStatus, check.getStatus());
            }
        }
        wrapper.orderByDesc(JnInventoryCheck::getCreateTime);
        return checkMapper.selectList(wrapper);
    }

    @Override
    public JnInventoryCheck getById(Long checkId) {
        JnInventoryCheck check = checkMapper.selectById(checkId);
        if (check != null) {
            LambdaQueryWrapper<JnCheckLine> lineWrapper = Wrappers.lambdaQuery();
            lineWrapper.eq(JnCheckLine::getCheckId, checkId);
            List<JnCheckLine> lines = checkLineMapper.selectList(lineWrapper);
            check.setLines(lines);
        }
        return check;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JnInventoryCheck createCheck(JnInventoryCheck check, List<JnCheckLine> lines) {
        check.setCheckNo(sequenceUtils.generate("CHECK_NO"));
        check.setStatus("0");
        check.setDiffCount(0);
        if (check.getCheckDate() == null) {
            check.setCheckDate(LocalDate.now());
        }
        checkMapper.insert(check);

        if (lines != null && !lines.isEmpty()) {
            int diffCount = 0;
            for (JnCheckLine line : lines) {
                line.setCheckId(check.getCheckId());

                JnInventory inventory = inventoryMapper.selectOne(
                        Wrappers.<JnInventory>lambdaQuery()
                                .eq(JnInventory::getWhId, check.getWhId())
                                .eq(JnInventory::getMaterialId, line.getMaterialId())
                );

                if (inventory != null) {
                    line.setBookQty(inventory.getQuantity() != null ? inventory.getQuantity() : 0);
                    if (line.getUnitPrice() == null) {
                        line.setUnitPrice(BigDecimal.ZERO);
                    }
                } else {
                    line.setBookQty(0);
                    line.setUnitPrice(BigDecimal.ZERO);
                }

                if (line.getActualQty() == null) {
                    line.setActualQty(0);
                }
                line.setDiffQty(line.getActualQty() - line.getBookQty());
                line.setDiffAmount(line.getUnitPrice().multiply(BigDecimal.valueOf(line.getDiffQty())));

                if (line.getDiffQty() != 0) {
                    diffCount++;
                }

                checkLineMapper.insert(line);
            }
            check.setDiffCount(diffCount);
            checkMapper.updateById(check);
        }

        return check;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JnInventoryCheck updateCheck(JnInventoryCheck check, List<JnCheckLine> lines) {
        JnInventoryCheck existing = checkMapper.selectById(check.getCheckId());
        if (existing == null) {
            throw new RuntimeException("盘点单不存在: " + check.getCheckId());
        }
        if (!"0".equals(existing.getStatus())) {
            throw new RuntimeException("只能修改进行中的盘点单");
        }

        checkMapper.updateById(check);

        LambdaQueryWrapper<JnCheckLine> deleteWrapper = Wrappers.lambdaQuery();
        deleteWrapper.eq(JnCheckLine::getCheckId, check.getCheckId());
        checkLineMapper.delete(deleteWrapper);

        if (lines != null && !lines.isEmpty()) {
            int diffCount = 0;
            for (JnCheckLine line : lines) {
                line.setCheckId(check.getCheckId());
                line.setLineId(null);

                JnInventory inventory = inventoryMapper.selectOne(
                        Wrappers.<JnInventory>lambdaQuery()
                                .eq(JnInventory::getWhId, check.getWhId())
                                .eq(JnInventory::getMaterialId, line.getMaterialId())
                );

                if (inventory != null) {
                    line.setBookQty(inventory.getQuantity() != null ? inventory.getQuantity() : 0);
                    if (line.getUnitPrice() == null) {
                        line.setUnitPrice(BigDecimal.ZERO);
                    }
                } else {
                    line.setBookQty(0);
                    line.setUnitPrice(BigDecimal.ZERO);
                }

                if (line.getActualQty() == null) {
                    line.setActualQty(0);
                }
                line.setDiffQty(line.getActualQty() - line.getBookQty());
                line.setDiffAmount(line.getUnitPrice().multiply(BigDecimal.valueOf(line.getDiffQty())));

                if (line.getDiffQty() != 0) {
                    diffCount++;
                }

                checkLineMapper.insert(line);
            }
            check.setDiffCount(diffCount);
            checkMapper.updateById(check);
        }

        return getById(check.getCheckId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] checkIds) {
        List<Long> ids = Arrays.asList(checkIds);
        for (Long id : ids) {
            LambdaQueryWrapper<JnCheckLine> deleteWrapper = Wrappers.lambdaQuery();
            deleteWrapper.eq(JnCheckLine::getCheckId, id);
            checkLineMapper.delete(deleteWrapper);
        }
        return checkMapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long checkId, String user) {
        JnInventoryCheck check = checkMapper.selectById(checkId);
        if (check == null) {
            throw new RuntimeException("盘点单不存在: " + checkId);
        }
        if (!"0".equals(check.getStatus())) {
            throw new RuntimeException("只能审核进行中的盘点单");
        }

        LambdaQueryWrapper<JnCheckLine> lineWrapper = Wrappers.lambdaQuery();
        lineWrapper.eq(JnCheckLine::getCheckId, checkId);
        List<JnCheckLine> lines = checkLineMapper.selectList(lineWrapper);

        for (JnCheckLine line : lines) {
            if (line.getDiffQty() == null || line.getDiffQty() == 0) {
                continue;
            }

            JnInventory inventory = inventoryMapper.selectOne(
                    Wrappers.<JnInventory>lambdaQuery()
                            .eq(JnInventory::getWhId, check.getWhId())
                            .eq(JnInventory::getMaterialId, line.getMaterialId())
            );

            if (inventory == null) {
                continue;
            }

            int beforeQty = inventory.getQuantity() != null ? inventory.getQuantity() : 0;

            if (line.getDiffQty() > 0) {
                inventory.setQuantity(beforeQty + line.getDiffQty());
                inventory.setAvailableQty(inventory.getAvailableQty() + line.getDiffQty());
            } else {
                int absDiff = Math.abs(line.getDiffQty());
                if (beforeQty < absDiff) {
                    inventory.setQuantity(0);
                } else {
                    inventory.setQuantity(beforeQty - absDiff);
                }
                inventory.setAvailableQty(inventory.getQuantity());
            }
            inventoryMapper.updateById(inventory);

            JnInventoryLog log = new JnInventoryLog();
            log.setWhId(check.getWhId());
            log.setMaterialId(line.getMaterialId());
            log.setChangeType("ADJUSTMENT");
            log.setChangeQty(line.getDiffQty());
            log.setBeforeQty(beforeQty);
            log.setAfterQty(inventory.getQuantity());
            log.setRefNo(check.getCheckNo());
            log.setRefType("CHECK");
            log.setRemark("盘点单审核自动调整: " + (line.getDiffQty() > 0 ? "盘盈" : "盘亏"));
            log.setCreateBy(user);
            log.setCreateTime(LocalDateTime.now());
            inventoryLogMapper.insert(log);
        }

        check.setStatus("2");
        check.setApproveBy(user);
        check.setApproveTime(LocalDateTime.now());
        checkMapper.updateById(check);
    }
}
