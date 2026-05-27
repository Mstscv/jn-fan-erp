package com.jn.erp.production.picking;

import com.jn.erp.production.picking.domain.JnPicking;
import com.jn.erp.production.picking.domain.JnPickingLine;
import com.jn.erp.production.picking.mapper.JnPickingLineMapper;
import com.jn.erp.production.picking.mapper.JnPickingMapper;
import com.jn.erp.production.picking.service.IJnPickingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JnPickingServiceTest {

    @InjectMocks
    private IJnPickingService pickingService;

    @Mock
    private JnPickingMapper pickingMapper;
    @Mock
    private JnPickingLineMapper pickingLineMapper;

    @Test
    void testInsertPicking() {
        JnPicking picking = new JnPicking();
        picking.setOrderId(1L);
        picking.setOrderNo("WO-001");
        picking.setPickingType("STANDARD");

        JnPickingLine line = new JnPickingLine();
        line.setMaterialId(1L);
        line.setMaterialName("钢板");
        line.setDemandQty(BigDecimal.TEN);

        when(pickingMapper.insert(any(JnPicking.class))).thenReturn(1);
        when(pickingLineMapper.insert(any(JnPickingLine.class))).thenReturn(1);

        int result = pickingService.insert(picking, List.of(line));
        assertEquals(1, result);
    }

    @Test
    void testApprovePicking() {
        JnPicking picking = new JnPicking();
        picking.setPickingId(1L);
        picking.setStatus("DRAFT");

        when(pickingMapper.selectById(1L)).thenReturn(picking);
        when(pickingMapper.updateById(any())).thenReturn(1);

        pickingService.approve(1L);
        verify(pickingMapper).updateById(argThat(p -> "APPROVED".equals(p.getStatus())));
    }
}
