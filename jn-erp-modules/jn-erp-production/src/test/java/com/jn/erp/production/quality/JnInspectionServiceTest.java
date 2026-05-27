package com.jn.erp.production.quality;

import com.jn.erp.production.quality.domain.JnInspection;
import com.jn.erp.production.quality.domain.JnInspectionLine;
import com.jn.erp.production.quality.mapper.JnInspectionLineMapper;
import com.jn.erp.production.quality.mapper.JnInspectionMapper;
import com.jn.erp.production.quality.service.IJnInspectionService;
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
class JnInspectionServiceTest {

    @InjectMocks
    private IJnInspectionService inspectionService;

    @Mock
    private JnInspectionMapper inspectionMapper;
    @Mock
    private JnInspectionLineMapper inspectionLineMapper;

    @Test
    void testCreateInspection() {
        JnInspection ins = new JnInspection();
        ins.setInspectionType("IQC");
        ins.setMaterialId(1L);
        ins.setMaterialName("钢板");
        ins.setSampleQty(10);

        JnInspectionLine line = new JnInspectionLine();
        line.setCheckItem("厚度");
        line.setLowerLimit("5.0");
        line.setUpperLimit("6.0");

        when(inspectionMapper.insert(any(JnInspection.class))).thenReturn(1);
        when(inspectionLineMapper.insert(any(JnInspectionLine.class))).thenReturn(1);

        int result = inspectionService.insert(ins, List.of(line));
        assertEquals(1, result);
    }

    @Test
    void testApproveInspection() {
        JnInspection ins = new JnInspection();
        ins.setInspectionId(1L);
        ins.setStatus("SUBMITTED");
        ins.setSampleQty(10);

        when(inspectionMapper.selectById(1L)).thenReturn(ins);
        when(inspectionMapper.updateById(any())).thenReturn(1);

        inspectionService.approve(1L, "PASS");
        verify(inspectionMapper).updateById(argThat(i -> "APPROVED".equals(i.getStatus()) && "PASS".equals(i.getResult())));
    }

    @Test
    void testRejectInspection() {
        JnInspection ins = new JnInspection();
        ins.setInspectionId(1L);
        ins.setStatus("SUBMITTED");

        when(inspectionMapper.selectById(1L)).thenReturn(ins);
        when(inspectionMapper.updateById(any())).thenReturn(1);

        inspectionService.reject(1L, "尺寸超差");
        verify(inspectionMapper).updateById(argThat(i -> "REJECT".equals(i.getResult())));
    }
}
