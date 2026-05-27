package com.jn.erp.production.routing;

import com.jn.erp.production.routing.domain.JnRouting;
import com.jn.erp.production.routing.domain.JnRoutingLine;
import com.jn.erp.production.routing.mapper.JnRoutingLineMapper;
import com.jn.erp.production.routing.mapper.JnRoutingMapper;
import com.jn.erp.production.routing.service.IJnRoutingService;
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
class JnRoutingServiceTest {

    @InjectMocks
    private IJnRoutingService routingService;

    @Mock
    private JnRoutingMapper routingMapper;
    @Mock
    private JnRoutingLineMapper routingLineMapper;

    @Test
    void testInsertRoutingWithLines() {
        JnRouting routing = new JnRouting();
        routing.setRoutingName("LR406风机工艺路线");
        routing.setProductId(1L);
        routing.setProductName("LR406风机");

        JnRoutingLine line = new JnRoutingLine();
        line.setSeqNo(10);
        line.setOperationId(1L);
        line.setOperationName("下料");
        line.setStandardTime(BigDecimal.valueOf(30));

        when(routingMapper.insert(any(JnRouting.class))).thenReturn(1);
        when(routingLineMapper.insert(any(JnRoutingLine.class))).thenReturn(1);

        int result = routingService.insert(routing, List.of(line));
        assertEquals(1, result);
    }

    @Test
    void testUpdateStatus() {
        JnRouting routing = new JnRouting();
        routing.setRoutingId(1L);
        routing.setStatus("DRAFT");

        when(routingMapper.selectById(1L)).thenReturn(routing);
        when(routingMapper.updateById(any())).thenReturn(1);

        routingService.updateStatus(1L, "APPROVED");
        verify(routingMapper).updateById(argThat(r -> "APPROVED".equals(r.getStatus()) && r.getApproveBy() != null));
    }
}
