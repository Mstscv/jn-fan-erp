package com.jn.erp.production.handover;

import com.jn.erp.production.handover.domain.JnHandover;
import com.jn.erp.production.handover.mapper.JnHandoverMapper;
import com.jn.erp.production.handover.service.IJnHandoverService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JnHandoverServiceTest {

    @InjectMocks
    private IJnHandoverService handoverService;

    @Mock
    private JnHandoverMapper handoverMapper;

    @Test
    void testConfirmHandover() {
        JnHandover handover = new JnHandover();
        handover.setHandoverId(1L);
        handover.setStatus("PENDING");

        when(handoverMapper.selectById(1L)).thenReturn(handover);
        when(handoverMapper.updateById(any())).thenReturn(1);

        handoverService.confirm(1L, 50, 2);
        verify(handoverMapper).updateById(argThat(h -> "TRANSFERRED".equals(h.getStatus())));
    }

    @Test
    void testRejectHandover() {
        JnHandover handover = new JnHandover();
        handover.setHandoverId(1L);
        handover.setStatus("PENDING");
        handover.setRemark("");

        when(handoverMapper.selectById(1L)).thenReturn(handover);
        when(handoverMapper.updateById(any())).thenReturn(1);

        handoverService.reject(1L, "质量不合格，退回重做");
        verify(handoverMapper).updateById(argThat(h -> "REJECTED".equals(h.getStatus())));
    }
}
