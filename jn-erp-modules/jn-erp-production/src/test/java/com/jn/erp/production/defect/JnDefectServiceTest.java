package com.jn.erp.production.defect;

import com.jn.erp.production.defect.domain.JnDefect;
import com.jn.erp.production.defect.domain.JnReworkOrder;
import com.jn.erp.production.defect.mapper.JnDefectMapper;
import com.jn.erp.production.defect.mapper.JnReworkOrderMapper;
import com.jn.erp.production.defect.service.IJnDefectService;
import com.jn.erp.production.defect.service.IJnReworkOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JnDefectServiceTest {

    @InjectMocks
    private IJnDefectService defectService;
    @InjectMocks
    private IJnReworkOrderService reworkService;

    @Mock
    private JnDefectMapper defectMapper;
    @Mock
    private JnReworkOrderMapper reworkMapper;

    @Test
    void testProcessDefectRework() {
        JnDefect defect = new JnDefect();
        defect.setDefectId(1L);
        defect.setQty(3);
        defect.setSeverity("MAJOR");

        when(defectMapper.selectById(1L)).thenReturn(defect);
        when(defectMapper.updateById(any())).thenReturn(1);

        defectService.processDefect(1L, "REWORK", "质检员");
        verify(defectMapper).updateById(argThat(d -> "CLOSED".equals(d.getStatus())));
    }

    @Test
    void testCreateReworkFromDefect() {
        JnDefect defect = new JnDefect();
        defect.setDefectId(1L);
        defect.setOrderId(1L);
        defect.setOrderNo("WO-001");
        defect.setQty(3);
        defect.setOperationId(1L);

        JnReworkOrder rework = new JnReworkOrder();
        rework.setReworkId(1L);

        when(defectMapper.selectById(1L)).thenReturn(defect);
        when(reworkMapper.insert(any(JnReworkOrder.class))).thenReturn(1);

        JnReworkOrder result = reworkService.createFromDefect(1L);
        assertNotNull(result);
    }
}
