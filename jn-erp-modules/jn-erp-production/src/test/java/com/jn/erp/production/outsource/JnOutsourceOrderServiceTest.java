package com.jn.erp.production.outsource;

import com.jn.erp.production.outsource.domain.JnOutsourceOrder;
import com.jn.erp.production.outsource.domain.JnOutsourceOrderLine;
import com.jn.erp.production.outsource.mapper.JnOutsourceOrderLineMapper;
import com.jn.erp.production.outsource.mapper.JnOutsourceOrderMapper;
import com.jn.erp.production.outsource.service.IJnOutsourceOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JnOutsourceOrderServiceTest {

    @InjectMocks
    private IJnOutsourceOrderService outsourceService;

    @Mock
    private JnOutsourceOrderMapper orderMapper;
    @Mock
    private JnOutsourceOrderLineMapper orderLineMapper;

    @Test
    void testInsertOutsourceOrder() {
        JnOutsourceOrder order = new JnOutsourceOrder();
        order.setSupplierId(1L);
        order.setSupplierName("外协加工厂");
        order.setProcessType("WELDING");
        order.setProductName("蜗壳组件");
        order.setOrderQty(100);

        JnOutsourceOrderLine line = new JnOutsourceOrderLine();
        line.setMaterialId(1L);
        line.setMaterialName("钢板");
        line.setDispatchQty(100);

        when(orderMapper.insert(any(JnOutsourceOrder.class))).thenReturn(1);
        when(orderLineMapper.insert(any(JnOutsourceOrderLine.class))).thenReturn(1);

        int result = outsourceService.insert(order, List.of(line));
        assertEquals(1, result);
    }

    @Test
    void testSubmitAndApprove() {
        JnOutsourceOrder order = new JnOutsourceOrder();
        order.setOrderId(1L);
        order.setStatus("DRAFT");

        when(orderMapper.selectById(1L)).thenReturn(order);
        when(orderMapper.updateById(any())).thenReturn(1);

        outsourceService.submit(1L);
        verify(orderMapper).updateById(argThat(o -> "PENDING".equals(o.getStatus())));
    }
}
