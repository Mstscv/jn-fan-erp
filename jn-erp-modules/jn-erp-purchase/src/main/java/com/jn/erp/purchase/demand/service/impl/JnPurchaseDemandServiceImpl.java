package com.jn.erp.purchase.demand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jn.erp.common.core.utils.SequenceUtils;
import com.jn.erp.purchase.demand.domain.JnPurchaseDemand;
import com.jn.erp.purchase.demand.mapper.JnPurchaseDemandMapper;
import com.jn.erp.purchase.demand.remote.RemoteProductionService;
import com.jn.erp.purchase.demand.remote.RemoteWarehouseService;
import com.jn.erp.purchase.demand.remote.domain.BomItemDTO;
import com.jn.erp.purchase.demand.remote.domain.InventoryDTO;
import com.jn.erp.purchase.demand.remote.domain.WorkOrderDTO;
import com.jn.erp.purchase.demand.service.IJnPurchaseDemandService;
import com.ruoyi.common.core.domain.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class JnPurchaseDemandServiceImpl implements IJnPurchaseDemandService {

    private static final Logger log = LoggerFactory.getLogger(JnPurchaseDemandServiceImpl.class);

    @Autowired
    private JnPurchaseDemandMapper jnPurchaseDemandMapper;

    @Autowired
    private SequenceUtils sequenceUtils;

    @Autowired(required = false)
    private RemoteProductionService remoteProductionService;

    @Autowired(required = false)
    private RemoteWarehouseService remoteWarehouseService;

    @Override
    public List<JnPurchaseDemand> selectList(JnPurchaseDemand query) {
        LambdaQueryWrapper<JnPurchaseDemand> wrapper = Wrappers.lambdaQuery();
        if (query != null) {
            if (query.getDemandNo() != null && !query.getDemandNo().isEmpty()) {
                wrapper.like(JnPurchaseDemand::getDemandNo, query.getDemandNo());
            }
            if (query.getMaterialId() != null) {
                wrapper.eq(JnPurchaseDemand::getMaterialId, query.getMaterialId());
            }
            if (query.getMaterialName() != null && !query.getMaterialName().isEmpty()) {
                wrapper.like(JnPurchaseDemand::getMaterialName, query.getMaterialName());
            }
            if (query.getStatus() != null && !query.getStatus().isEmpty()) {
                wrapper.eq(JnPurchaseDemand::getStatus, query.getStatus());
            }
            if (query.getSourceType() != null && !query.getSourceType().isEmpty()) {
                wrapper.eq(JnPurchaseDemand::getSourceType, query.getSourceType());
            }
            if (query.getDemandDate() != null) {
                wrapper.eq(JnPurchaseDemand::getDemandDate, query.getDemandDate());
            }
        }
        wrapper.orderByDesc(JnPurchaseDemand::getCreateTime);
        return jnPurchaseDemandMapper.selectList(wrapper);
    }

    @Override
    public JnPurchaseDemand getById(Long demandId) {
        return jnPurchaseDemandMapper.selectById(demandId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDemand(JnPurchaseDemand demand) {
        demand.setDemandNo(generateDemandNo());
        if (demand.getQuantity() == null) {
            demand.setQuantity(0);
        }
        if (demand.getSourceType() == null || demand.getSourceType().isEmpty()) {
            demand.setSourceType("MANUAL");
        }
        if (demand.getStatus() == null || demand.getStatus().isEmpty()) {
            demand.setStatus("0");
        }
        return jnPurchaseDemandMapper.insert(demand);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDemand(JnPurchaseDemand demand) {
        return jnPurchaseDemandMapper.updateById(demand);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] demandIds) {
        List<Long> ids = Arrays.asList(demandIds);
        return jnPurchaseDemandMapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int convertToPo(Long[] demandIds) {
        List<Long> ids = Arrays.asList(demandIds);
        LambdaQueryWrapper<JnPurchaseDemand> wrapper = Wrappers.lambdaQuery();
        wrapper.in(JnPurchaseDemand::getDemandId, ids);
        wrapper.eq(JnPurchaseDemand::getStatus, "0");
        JnPurchaseDemand updateEntity = new JnPurchaseDemand();
        updateEntity.setStatus("1");
        return jnPurchaseDemandMapper.update(updateEntity, wrapper);
    }

    @Override
    public String generateDemandNo() {
        return sequenceUtils.generate("PURCHASE_DEMAND_NO");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<JnPurchaseDemand> runMrp() {
        List<JnPurchaseDemand> createdDemands = new ArrayList<>();

        try {
            if (remoteProductionService == null) {
                log.warn("MRP: 生产服务不可用，无法获取工单和BOM数据");
                return createdDemands;
            }

            List<String> statusList = Arrays.asList("PENDING", "IN_PROGRESS");
            R<List<WorkOrderDTO>> workOrderResult = remoteProductionService.listWorkOrdersByStatus(statusList);

            if (workOrderResult == null || workOrderResult.getData() == null || workOrderResult.getData().isEmpty()) {
                log.info("MRP: 没有待处理或进行中的工单");
                return createdDemands;
            }

            List<WorkOrderDTO> workOrders = workOrderResult.getData();
            log.info("MRP: 获取到 {} 个工单", workOrders.size());

            for (WorkOrderDTO workOrder : workOrders) {
                Integer workOrderQty = workOrder.getQuantity();
                if (workOrderQty == null || workOrderQty <= 0) {
                    continue;
                }

                R<List<BomItemDTO>> bomResult = remoteProductionService.getBomItemList(workOrder.getMaterialId());
                if (bomResult == null || bomResult.getData() == null || bomResult.getData().isEmpty()) {
                    log.warn("MRP: 物料 {} 未配置BOM，跳过", workOrder.getMaterialName());
                    continue;
                }

                List<BomItemDTO> bomItems = bomResult.getData();
                for (BomItemDTO bomItem : bomItems) {
                    BigDecimal bomQty = bomItem.getQuantity() != null ? bomItem.getQuantity() : BigDecimal.ZERO;
                    int requiredQty = bomQty.multiply(BigDecimal.valueOf(workOrderQty)).intValue();

                    if (requiredQty <= 0) {
                        continue;
                    }

                    int onHandQty = getOnHandQuantity(bomItem.getMaterialId());
                    int netRequirement = Math.max(0, requiredQty - onHandQty);

                    if (netRequirement <= 0) {
                        log.info("MRP: 物料 {} 库存充足，无需采购", bomItem.getMaterialName());
                        continue;
                    }

                    JnPurchaseDemand demand = new JnPurchaseDemand();
                    demand.setDemandNo(generateDemandNo());
                    demand.setMaterialId(bomItem.getMaterialId());
                    demand.setMaterialName(bomItem.getMaterialName());
                    demand.setQuantity(netRequirement);
                    demand.setDemandDate(LocalDate.now());
                    demand.setSourceType("MRP");
                    demand.setSourceRef(workOrder.getWorkOrderNo());
                    demand.setStatus("0");
                    jnPurchaseDemandMapper.insert(demand);

                    createdDemands.add(demand);
                    log.info("MRP: 生成采购需求，物料={}，数量={}", bomItem.getMaterialName(), netRequirement);
                }
            }
        } catch (Exception e) {
            log.error("MRP运算执行异常", e);
            throw new RuntimeException("MRP运算失败: " + e.getMessage(), e);
        }

        return createdDemands;
    }

    private int getOnHandQuantity(Long materialId) {
        if (remoteWarehouseService == null) {
            log.warn("MRP: 仓库服务不可用，默认库存为0");
            return 0;
        }
        try {
            R<InventoryDTO> inventoryResult = remoteWarehouseService.getInventoryByMaterial(materialId);
            if (inventoryResult != null && inventoryResult.getData() != null) {
                Integer qty = inventoryResult.getData().getQuantity();
                return qty != null ? qty : 0;
            }
        } catch (Exception e) {
            log.warn("MRP: 查询物料 {} 库存失败", materialId, e);
        }
        return 0;
    }
}
