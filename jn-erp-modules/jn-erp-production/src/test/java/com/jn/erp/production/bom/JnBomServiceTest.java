package com.jn.erp.production.bom;

import com.jn.erp.production.bom.domain.JnBom;
import com.jn.erp.production.bom.domain.JnBomLine;
import com.jn.erp.production.bom.mapper.JnBomLineMapper;
import com.jn.erp.production.bom.mapper.JnBomMapper;
import com.jn.erp.production.bom.mapper.JnBomMaterialPriceMapper;
import com.jn.erp.production.bom.service.IJnBomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JnBomServiceTest {

    @InjectMocks
    private IJnBomService bomService;

    @Mock
    private JnBomMapper bomMapper;
    @Mock
    private JnBomLineMapper bomLineMapper;
    @Mock
    private JnBomMaterialPriceMapper materialPriceMapper;

    @Test
    void testInsertBomWithLines() {
        JnBom bom = new JnBom();
        bom.setBomName("测试BOM");
        bom.setProductId(1L);
        bom.setProductCode("P001");
        bom.setProductName("测试产品");

        JnBomLine line = new JnBomLine();
        line.setMaterialId(1L);
        line.setMaterialCode("M001");
        line.setMaterialName("测试物料");
        line.setQuantity(BigDecimal.TEN);
        line.setUnit("个");

        List<JnBomLine> lines = new ArrayList<>();
        lines.add(line);

        when(bomMapper.insert(any(JnBom.class))).thenReturn(1);
        when(bomLineMapper.insert(any(JnBomLine.class))).thenReturn(1);

        int result = bomService.insertBom(bom, lines);
        assertEquals(1, result);
    }

    @Test
    void testBomStatusTransition() {
        JnBom bom = new JnBom();
        bom.setBomId(1L);
        bom.setStatus("DRAFT");

        when(bomMapper.selectById(1L)).thenReturn(bom);
        when(bomMapper.updateById(any())).thenReturn(1);

        bomService.updateStatus(1L, "APPROVED");
        verify(bomMapper, times(1)).updateById(argThat(b -> "APPROVED".equals(b.getStatus())));
    }

    @Test
    void testBomStatusInvalidTransition() {
        JnBom bom = new JnBom();
        bom.setBomId(1L);
        bom.setStatus("DRAFT");

        when(bomMapper.selectById(1L)).thenReturn(bom);

        assertThrows(RuntimeException.class, () -> bomService.updateStatus(1L, "EFFECTIVE"));
    }

    @Test
    void testCalculateCost() {
        JnBom bom = new JnBom();
        bom.setBomId(1L);
        bom.setTotalCost(BigDecimal.ZERO);

        JnBomLine line = new JnBomLine();
        line.setLineId(1L);
        line.setMaterialId(1L);
        line.setQuantity(BigDecimal.TEN);
        line.setScrapRate(BigDecimal.ZERO);

        when(bomMapper.selectById(1L)).thenReturn(bom);
        when(bomLineMapper.selectByBomId(1L)).thenReturn(List.of(line));
        when(materialPriceMapper.selectUnitPriceByMaterialId(1L)).thenReturn(BigDecimal.valueOf(100));

        BigDecimal cost = bomService.calculateCost(1L);
        assertEquals(BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP), cost);
    }

    @Test
    void testDeleteBom() {
        JnBom bom = new JnBom();
        bom.setBomId(1L);
        when(bomMapper.selectById(1L)).thenReturn(bom);
        when(bomMapper.updateById(any())).thenReturn(1);

        bomService.deleteByIds(new Long[]{1L});
        verify(bomMapper, times(1)).updateById(argThat(b -> "1".equals(b.getDelFlag())));
    }
}
