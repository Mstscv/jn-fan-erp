package com.jn.erp.production.workorder;

import com.jn.erp.production.workorder.domain.JnWorkOrder;
import com.jn.erp.production.workorder.domain.JnWorkOrderLine;
import com.jn.erp.production.workorder.mapper.JnWorkOrderLineMapper;
import com.jn.erp.production.workorder.mapper.JnWorkOrderMapper;
import com.jn.erp.production.workorder.service.IJnWorkOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JnWorkOrderServiceTest {

    @InjectMocks
    private IJnWorkOrderService workOrderService;

    @Mock
    private JnWorkOrderMapper workOrderMapper;
    @Mock
    private JnWorkOrderLineMapper workOrderLineMapper;

    @Test
    void testInsertWorkOrder() {
        JnWorkOrder order = new JnWorkOrder();
        order.setProductId(1L);
        order.setProductName("LR406风机");
        order.setQuantity(10);
        order.setPlannedStart(LocalDate.now());
        order.setPlannedEnd(LocalDate.now().plusDays(30));

        JnWorkOrderLine line = new JnWorkOrderLine();
        line.setProductId(1L);
        line.setQuantity(10);

        when(workOrderMapper.insert(any(JnWorkOrder.class))).thenReturn(1);
        when(workOrderLineMapper.insert(any(JnWorkOrderLine.class))).thenReturn(1);

        int result = workOrderService.insert(order, List.of(line));
        assertEquals(1, result);
    }

    @Test
    void testStatusTransition() {
        JnWorkOrder order = new JnWorkOrder();
        order.setOrderId(1L);
        order.setStatus("PENDING");

        when(workOrderMapper.selectById(1L)).thenReturn(order);
        when(workOrderMapper.updateById(any())).thenReturn(1);

        workOrderService.updateStatus(1L, "SCHEDULED");
        verify(workOrderMapper).updateById(argThat(o -> "SCHEDULED".equals(o.getStatus())));
    }

    @Test
    void testStatusTransitionCompletedSetsEndTime() {
        JnWorkOrder order = new JnWorkOrder();
        order.setOrderId(1L);
        order.setStatus("IN_PROGRESS");

        when(workOrderMapper.selectById(1L)).thenReturn(order);
        when(workOrderMapper.updateById(any())).thenReturn(1);

        workOrderService.updateStatus(1L, "COMPLETED");
        verify(workOrderMapper).updateById(argThat(o -> o.getActualEnd() != null));
    }

    @Test
    void testCannotCancelCompletedOrder() {
        JnWorkOrder order = new JnWorkOrder();
        order.setOrderId(1L);
        order.setStatus("COMPLETED");

        when(workOrderMapper.selectById(1L)).thenReturn(order);

        assertThrows(RuntimeException.class, () -> workOrderService.updateStatus(1L, "CANCELLED"));
    }

    @Test
    void testGetKanbanData() {
        when(workOrderMapper.selectCount(any())).thenReturn(5L);

        Map<String, Object> kanban = workOrderService.getKanbanData();
        assertNotNull(kanban);
        assertEquals(5L, kanban.get("pending"));
    }
}
