package com.jn.erp.production.equipment;

import com.jn.erp.production.equipment.domain.JnEquipment;
import com.jn.erp.production.equipment.mapper.JnEquipmentMapper;
import com.jn.erp.production.equipment.service.IJnEquipmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JnEquipmentServiceTest {

    @InjectMocks
    private IJnEquipmentService equipmentService;

    @Mock
    private JnEquipmentMapper equipmentMapper;

    @Test
    void testInsertAndQueryEquipment() {
        JnEquipment eq = new JnEquipment();
        eq.setEquipmentCode("BAL-001");
        eq.setEquipmentName("动平衡机");
        eq.setStatus("IDLE");
        eq.setWorkCenterId(1L);
        eq.setDailyCapacity(50);

        when(equipmentMapper.insert(any(JnEquipment.class))).thenReturn(1);

        int result = equipmentService.insert(eq);
        assertEquals(1, result);
    }
}
