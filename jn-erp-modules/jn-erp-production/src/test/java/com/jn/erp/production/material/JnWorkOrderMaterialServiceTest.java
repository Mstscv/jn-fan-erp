package com.jn.erp.production.material;

import com.jn.erp.production.bom.domain.JnBomLine;
import com.jn.erp.production.bom.mapper.JnBomLineMapper;
import com.jn.erp.production.material.domain.JnWorkOrderMaterial;
import com.jn.erp.production.material.mapper.JnWorkOrderMaterialMapper;
import com.jn.erp.production.material.service.IJnWorkOrderMaterialService;
import com.jn.erp.production.workorder.domain.JnWorkOrder;
import com.jn.erp.production.workorder.mapper.JnWorkOrderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JnWorkOrderMaterialServiceTest {

    @InjectMocks
    private IJnWorkOrderMaterialService materialService;

    @Mock
    private JnWorkOrderMaterialMapper materialMapper;
    @Mock
    private JnWorkOrderMapper workOrderMapper;
    @Mock
    private JnBomLineMapper bomLineMapper;

    @Test
    void testExplodeBom() {
        JnWorkOrder order = new JnWorkOrder();
        order.setOrderId(1L);
        order.setOrderNo("WO-001");
        order.setBomId(1L);
        order.setQuantity(10);

        JnBomLine bomLine = new JnBomLine();
        bomLine.setLineId(1L);
        bomLine.setMaterialId(1L);
        bomLine.setMaterialName("钢板");
        bomLine.setQuantity(BigDecimal.valueOf(2));
        bomLine.setUnit("张");
        bomLine.setIsOptional("N");

        when(workOrderMapper.selectById(1L)).thenReturn(order);
        when(bomLineMapper.selectByBomId(1L)).thenReturn(List.of(bomLine));
        when(materialMapper.selectByOrderId(1L)).thenReturn(new ArrayList<>());

        List<JnWorkOrderMaterial> result = materialService.explodeBom(1L);
        assertNotNull(result);
    }

    @Test
    void testAllocateMaterial() {
        JnWorkOrderMaterial material = new JnWorkOrderMaterial();
        material.setDemandId(1L);
        material.setRequiredQty(BigDecimal.valueOf(10));
        material.setAllocatedQty(BigDecimal.ZERO);
        material.setStatus("PENDING");

        when(materialMapper.selectById(1L)).thenReturn(material);
        when(materialMapper.updateById(any())).thenReturn(1);

        int result = materialService.allocateMaterial(1L, BigDecimal.valueOf(5));
        assertEquals(1, result);
    }

    @Test
    void testAllocateExceedsRequirement() {
        JnWorkOrderMaterial material = new JnWorkOrderMaterial();
        material.setDemandId(1L);
        material.setRequiredQty(BigDecimal.TEN);
        material.setAllocatedQty(BigDecimal.ZERO);

        when(materialMapper.selectById(1L)).thenReturn(material);

        assertThrows(RuntimeException.class, () -> materialService.allocateMaterial(1L, BigDecimal.valueOf(20)));
    }
}
