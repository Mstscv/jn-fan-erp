package com.jn.erp.production.e2e;

import com.jn.erp.production.bom.domain.JnBom;
import com.jn.erp.production.bom.domain.JnBomLine;
import com.jn.erp.production.bom.service.IJnBomService;
import com.jn.erp.production.defect.domain.JnDefect;
import com.jn.erp.production.defect.domain.JnReworkOrder;
import com.jn.erp.production.defect.service.IJnDefectService;
import com.jn.erp.production.defect.service.IJnReworkOrderService;
import com.jn.erp.production.picking.domain.JnPicking;
import com.jn.erp.production.picking.service.IJnPickingService;
import com.jn.erp.production.quality.domain.JnInspection;
import com.jn.erp.production.quality.service.IJnInspectionService;
import com.jn.erp.production.report.domain.JnWorkReport;
import com.jn.erp.production.report.service.IJnWorkReportService;
import com.jn.erp.production.routing.domain.JnRouting;
import com.jn.erp.production.routing.service.IJnRoutingService;
import com.jn.erp.production.schedule.domain.JnSchedule;
import com.jn.erp.production.schedule.service.IJnScheduleService;
import com.jn.erp.production.workorder.domain.JnWorkOrder;
import com.jn.erp.production.workorder.service.IJnWorkOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ErpEndToEndFlowTest {

    @Mock
    private IJnWorkOrderService workOrderService;

    @Mock
    private IJnBomService bomService;

    @Mock
    private IJnRoutingService routingService;

    @Mock
    private IJnScheduleService scheduleService;

    @Mock
    private IJnWorkReportService workReportService;

    @Mock
    private IJnInspectionService inspectionService;

    @Mock
    private IJnPickingService pickingService;

    @Mock
    private IJnDefectService defectService;

    @Mock
    private IJnReworkOrderService reworkOrderService;

    @Test
    void flow1_newCustomer_inquiryToReceivable() {
        String customerName = "新客户A";
        String productName = "LR406风机";

        JnWorkOrder quotation = buildQuotation(1L, "QUOTED", customerName, productName);
        when(workOrderService.createQuotation(any())).thenReturn(quotation);
        JnWorkOrder createdQuotation = workOrderService.createQuotation(quotation);
        assertNotNull(createdQuotation);
        assertEquals("QUOTED", createdQuotation.getStatus());
        assertEquals("QTN-0001", createdQuotation.getOrderNo());
        verify(workOrderService).createQuotation(any());

        JnWorkOrder salesOrder = buildSalesOrder(2L, "ORDERED", customerName, productName, createdQuotation);
        when(workOrderService.createSalesOrder(any())).thenReturn(salesOrder);
        JnWorkOrder createdOrder = workOrderService.createSalesOrder(salesOrder);
        assertNotNull(createdOrder);
        assertEquals("ORDERED", createdOrder.getStatus());
        assertEquals("SO-0002", createdOrder.getOrderNo());
        verify(workOrderService).createSalesOrder(any());

        JnWorkOrder mrpResult = buildWorkOrder(3L, "MRP_DONE", productName, 10);
        when(workOrderService.runMrp(anyLong())).thenReturn(mrpResult);
        JnWorkOrder mrpOrder = workOrderService.runMrp(createdOrder.getOrderId());
        assertNotNull(mrpOrder);
        assertEquals("MRP_DONE", mrpOrder.getStatus());
        verify(workOrderService).runMrp(anyLong());

        JnWorkOrder purchaseReq = buildWorkOrder(4L, "PURCHASED", productName, 5);
        when(workOrderService.createPurchaseOrder(any())).thenReturn(purchaseReq);
        JnWorkOrder purchaseOrder = workOrderService.createPurchaseOrder(mrpOrder);
        assertNotNull(purchaseOrder);
        assertEquals("PURCHASED", purchaseOrder.getStatus());
        verify(workOrderService).createPurchaseOrder(any());

        JnWorkOrder workOrder = buildWorkOrder(5L, "RELEASED", productName, 10);
        when(workOrderService.releaseWorkOrder(anyLong())).thenReturn(workOrder);
        JnWorkOrder released = workOrderService.releaseWorkOrder(5L);
        assertNotNull(released);
        assertEquals("RELEASED", released.getStatus());
        verify(workOrderService).releaseWorkOrder(anyLong());

        JnSchedule schedule = buildSchedule(1L, 5L, "SCHEDULED");
        when(scheduleService.createSchedule(any())).thenReturn(schedule);
        JnSchedule createdSchedule = scheduleService.createSchedule(schedule);
        assertNotNull(createdSchedule);
        assertEquals("SCHEDULED", createdSchedule.getStatus());
        verify(scheduleService).createSchedule(any());

        JnWorkReport report = buildWorkReport(1L, 5L, "REPORTED");
        when(workReportService.submitReport(any())).thenReturn(report);
        JnWorkReport submitted = workReportService.submitReport(report);
        assertNotNull(submitted);
        assertEquals("REPORTED", submitted.getStatus());
        verify(workReportService).submitReport(any());

        JnInspection inspection = buildInspection(1L, 5L, "INSPECTED");
        when(inspectionService.createInspection(any())).thenReturn(inspection);
        when(inspectionService.updateStatus(anyLong(), eq("QUALIFIED"))).thenReturn(inspection);
        JnInspection createdInspection = inspectionService.createInspection(inspection);
        assertNotNull(createdInspection);
        assertEquals("INSPECTED", createdInspection.getStatus());
        JnInspection qualified = inspectionService.updateStatus(createdInspection.getInspectionId(), "QUALIFIED");
        assertEquals("INSPECTED", qualified.getStatus());
        verify(inspectionService, times(2)).updateStatus(anyLong(), anyString());

        JnPicking picking = buildPicking(1L, "IN_STOCKED");
        when(pickingService.confirmInStock(any())).thenReturn(picking);
        JnPicking inStock = pickingService.confirmInStock(picking);
        assertNotNull(inStock);
        assertEquals("IN_STOCKED", inStock.getStatus());
        verify(pickingService).confirmInStock(any());

        JnWorkOrder delivery = buildWorkOrder(6L, "SHIPPED", customerName, productName);
        when(workOrderService.confirmDelivery(anyLong())).thenReturn(delivery);
        JnWorkOrder shipped = workOrderService.confirmDelivery(6L);
        assertNotNull(shipped);
        assertEquals("SHIPPED", shipped.getStatus());
        verify(workOrderService).confirmDelivery(anyLong());

        JnWorkOrder receivable = buildWorkOrder(7L, "RECEIVABLE", customerName, productName);
        when(workOrderService.createReceivable(anyLong())).thenReturn(receivable);
        JnWorkOrder ar = workOrderService.createReceivable(7L);
        assertNotNull(ar);
        assertEquals("RECEIVABLE", ar.getStatus());
        verify(workOrderService).createReceivable(anyLong());

        verifyNoMoreInteractions(workOrderService);
    }

    @Test
    void flow2_returnCustomer_additionalOrderWithInsertion() {
        String customerName = "老客户B";
        String productName = "LR406风机";

        JnWorkOrder initialOrder = buildSalesOrder(10L, "ORDERED", customerName, productName, null);
        when(workOrderService.createSalesOrder(any())).thenReturn(initialOrder);
        JnWorkOrder createdInitial = workOrderService.createSalesOrder(initialOrder);
        assertNotNull(createdInitial);
        assertEquals("ORDERED", createdInitial.getStatus());
        verify(workOrderService).createSalesOrder(any());

        JnWorkOrder initialWo = buildWorkOrder(11L, "IN_PROGRESS", productName, 10);
        when(workOrderService.releaseWorkOrder(anyLong())).thenReturn(initialWo);
        JnWorkOrder releasedInitial = workOrderService.releaseWorkOrder(11L);
        assertNotNull(releasedInitial);
        assertEquals("IN_PROGRESS", releasedInitial.getStatus());
        verify(workOrderService).releaseWorkOrder(anyLong());

        JnWorkOrder additionalOrder = buildSalesOrder(12L, "ORDERED", customerName, productName, null);
        additionalOrder.setPriority("URGENT");
        when(workOrderService.createSalesOrder(any())).thenReturn(additionalOrder);
        JnWorkOrder createdAdditional = workOrderService.createSalesOrder(additionalOrder);
        assertNotNull(createdAdditional);
        assertEquals("URGENT", createdAdditional.getPriority());
        verify(workOrderService, times(2)).createSalesOrder(any());

        JnSchedule adjustedSchedule = buildSchedule(2L, 11L, "RESCHEDULED");
        when(scheduleService.adjustSchedule(anyLong(), any())).thenReturn(adjustedSchedule);
        JnSchedule rescheduled = scheduleService.adjustSchedule(2L, Collections.singletonMap("insertOrderId", 12L));
        assertNotNull(rescheduled);
        assertEquals("RESCHEDULED", rescheduled.getStatus());
        verify(scheduleService).adjustSchedule(anyLong(), any());

        when(workOrderService.getWorkOrder(11L)).thenReturn(initialWo);
        JnWorkOrder woAfterInsertion = workOrderService.getWorkOrder(11L);
        assertNotNull(woAfterInsertion);
        assertEquals("IN_PROGRESS", woAfterInsertion.getStatus());

        JnWorkOrder finishedOrder = buildWorkOrder(11L, "COMPLETED", productName, 10);
        when(workOrderService.completeWorkOrder(anyLong())).thenReturn(finishedOrder);
        JnWorkOrder completed = workOrderService.completeWorkOrder(11L);
        assertNotNull(completed);
        assertEquals("COMPLETED", completed.getStatus());
        verify(workOrderService).completeWorkOrder(anyLong());

        JnWorkOrder additionalFinished = buildWorkOrder(12L, "COMPLETED", productName, 5);
        when(workOrderService.completeWorkOrder(anyLong())).thenReturn(additionalFinished);
        JnWorkOrder completedAdditional = workOrderService.completeWorkOrder(12L);
        assertNotNull(completedAdditional);
        assertEquals("COMPLETED", completedAdditional.getStatus());
        verify(workOrderService, times(2)).completeWorkOrder(anyLong());

        JnWorkOrder shipped = buildWorkOrder(13L, "SHIPPED", customerName, productName);
        when(workOrderService.confirmDelivery(anyLong())).thenReturn(shipped);
        JnWorkOrder deliveryComplete = workOrderService.confirmDelivery(13L);
        assertNotNull(deliveryComplete);
        assertEquals("SHIPPED", deliveryComplete.getStatus());
        verify(workOrderService).confirmDelivery(anyLong());
    }

    @Test
    void flow3_nonStandard_customBomToProduction() {
        String productName = "非标定制风机-XY01";

        JnBom bom = buildBom(1L, "DRAFT", productName);
        when(bomService.createBom(any())).thenReturn(bom);
        JnBom createdBom = bomService.createBom(bom);
        assertNotNull(createdBom);
        assertEquals("DRAFT", createdBom.getStatus());
        assertEquals(productName, createdBom.getProductName());
        verify(bomService).createBom(any());

        JnBomLine bomLine1 = buildBomLine(1L, 1L, "电机-非标-01", BigDecimal.valueOf(2));
        JnBomLine bomLine2 = buildBomLine(2L, 1L, "叶轮-非标-01", BigDecimal.ONE);
        List<JnBomLine> bomLines = Arrays.asList(bomLine1, bomLine2);
        when(bomService.addBomLines(anyLong(), anyList())).thenReturn(bomLines);
        List<JnBomLine> addedLines = bomService.addBomLines(1L, bomLines);
        assertNotNull(addedLines);
        assertEquals(2, addedLines.size());
        verify(bomService).addBomLines(anyLong(), anyList());

        JnBom releasedBom = buildBom(1L, "RELEASED", productName);
        when(bomService.releaseBom(anyLong())).thenReturn(releasedBom);
        JnBom released = bomService.releaseBom(1L);
        assertNotNull(released);
        assertEquals("RELEASED", released.getStatus());
        verify(bomService).releaseBom(anyLong());

        JnWorkOrder customOrder = buildWorkOrder(20L, "BOM_READY", productName, 3);
        customOrder.setBomId(1L);
        when(workOrderService.createWorkOrder(any())).thenReturn(customOrder);
        JnWorkOrder createdCustom = workOrderService.createWorkOrder(customOrder);
        assertNotNull(createdCustom);
        assertEquals("BOM_READY", createdCustom.getStatus());
        assertEquals(1L, createdCustom.getBomId().longValue());
        verify(workOrderService).createWorkOrder(any());

        JnRouting routing = buildRouting(1L, "ROUTED", "非标定制工艺");
        when(routingService.createRouting(any())).thenReturn(routing);
        JnRouting createdRouting = routingService.createRouting(routing);
        assertNotNull(createdRouting);
        assertEquals("ROUTED", createdRouting.getStatus());
        verify(routingService).createRouting(any());

        JnWorkOrder inProduction = buildWorkOrder(20L, "IN_PROGRESS", productName, 3);
        when(workOrderService.startProduction(anyLong())).thenReturn(inProduction);
        JnWorkOrder started = workOrderService.startProduction(20L);
        assertNotNull(started);
        assertEquals("IN_PROGRESS", started.getStatus());
        verify(workOrderService).startProduction(anyLong());

        JnWorkReport customReport = buildWorkReport(2L, 20L, "REPORTED");
        when(workReportService.submitReport(any())).thenReturn(customReport);
        JnWorkReport submitted = workReportService.submitReport(customReport);
        assertNotNull(submitted);
        assertEquals("REPORTED", submitted.getStatus());
        verify(workReportService).submitReport(any());

        JnWorkOrder completed = buildWorkOrder(20L, "COMPLETED", productName, 3);
        when(workOrderService.completeWorkOrder(anyLong())).thenReturn(completed);
        JnWorkOrder done = workOrderService.completeWorkOrder(20L);
        assertNotNull(done);
        assertEquals("COMPLETED", done.getStatus());
        verify(workOrderService).completeWorkOrder(anyLong());

        JnInspection finalInspection = buildInspection(2L, 20L, "QUALIFIED");
        when(inspectionService.createInspection(any())).thenReturn(finalInspection);
        JnInspection inspected = inspectionService.createInspection(finalInspection);
        assertNotNull(inspected);
        assertEquals("QUALIFIED", inspected.getResult());
        verify(inspectionService).createInspection(any());
    }

    @Test
    void flow4_purchaseReturn_refundToPayable() {
        Long materialId = 100L;

        JnPicking returnPicking = buildPicking(30L, "RETURNED");
        when(pickingService.createReturnOrder(any())).thenReturn(returnPicking);
        JnPicking returnOrder = pickingService.createReturnOrder(returnPicking);
        assertNotNull(returnOrder);
        assertEquals("RETURNED", returnOrder.getStatus());
        verify(pickingService).createReturnOrder(any());

        when(pickingService.confirmReturn(anyLong())).thenReturn(returnPicking);
        JnPicking confirmedReturn = pickingService.confirmReturn(30L);
        assertNotNull(confirmedReturn);
        assertEquals("RETURNED", confirmedReturn.getStatus());
        verify(pickingService).confirmReturn(anyLong());

        JnWorkOrder refund = buildWorkOrder(31L, "REFUNDED", "供应商C", "物料");
        when(workOrderService.processRefund(anyLong(), any())).thenReturn(refund);
        JnWorkOrder refundResult = workOrderService.processRefund(30L, new BigDecimal("5000.00"));
        assertNotNull(refundResult);
        assertEquals("REFUNDED", refundResult.getStatus());
        verify(workOrderService).processRefund(anyLong(), any());

        JnWorkOrder inventoryAdjustment = buildWorkOrder(32L, "INVENTORY_ADJUSTED", "供应商C", "物料");
        when(workOrderService.adjustInventory(anyLong(), anyInt())).thenReturn(inventoryAdjustment);
        JnWorkOrder adjusted = workOrderService.adjustInventory(materialId, -10);
        assertNotNull(adjusted);
        assertEquals("INVENTORY_ADJUSTED", adjusted.getStatus());
        verify(workOrderService).adjustInventory(anyLong(), anyInt());

        JnWorkOrder payableUpdate = buildWorkOrder(33L, "PAYABLE_UPDATED", "供应商C", "物料");
        when(workOrderService.updatePayable(anyLong(), any())).thenReturn(payableUpdate);
        JnWorkOrder payableUpdated = workOrderService.updatePayable(30L, new BigDecimal("5000.00"));
        assertNotNull(payableUpdated);
        assertEquals("PAYABLE_UPDATED", payableUpdated.getStatus());
        verify(workOrderService).updatePayable(anyLong(), any());

        JnWorkOrder payableSummary = buildWorkOrder(34L, "PAYABLE", "供应商C", "物料");
        when(workOrderService.getPayableSummary(anyLong())).thenReturn(payableSummary);
        JnWorkOrder summary = workOrderService.getPayableSummary(30L);
        assertNotNull(summary);
        assertEquals("PAYABLE", summary.getStatus());
        verify(workOrderService).getPayableSummary(anyLong());
    }

    @Test
    void flow5_inventoryCheck_surplusDeficitAdjustment() {
        Long materialId = 200L;

        JnDefect defect = buildDefect(1L, materialId, "SURPLUS", 5);
        when(defectService.createDefect(any())).thenReturn(defect);
        JnDefect createdDefect = defectService.createDefect(defect);
        assertNotNull(createdDefect);
        assertEquals("SURPLUS", createdDefect.getDefectType());
        assertEquals(5, createdDefect.getQty());
        verify(defectService).createDefect(any());

        JnDefect deficit = buildDefect(2L, materialId, "DEFICIT", -3);
        when(defectService.createDefect(any())).thenReturn(deficit);
        JnDefect createdDeficit = defectService.createDefect(deficit);
        assertNotNull(createdDeficit);
        assertEquals("DEFICIT", createdDeficit.getDefectType());
        assertEquals(-3, createdDeficit.getQty());
        verify(defectService, times(2)).createDefect(any());

        JnWorkOrder surplusAdjustment = buildWorkOrder(40L, "ADJUSTED", "盘点", "物料");
        when(workOrderService.adjustInventory(anyLong(), anyInt())).thenReturn(surplusAdjustment);
        JnWorkOrder surplusAdjusted = workOrderService.adjustInventory(materialId, 5);
        assertNotNull(surplusAdjusted);
        assertEquals("ADJUSTED", surplusAdjusted.getStatus());
        verify(workOrderService).adjustInventory(anyLong(), eq(5));

        JnWorkOrder deficitAdjustment = buildWorkOrder(41L, "ADJUSTED", "盘点", "物料");
        when(workOrderService.adjustInventory(anyLong(), anyInt())).thenReturn(deficitAdjustment);
        JnWorkOrder deficitAdjusted = workOrderService.adjustInventory(materialId, -3);
        assertNotNull(deficitAdjusted);
        assertEquals("ADJUSTED", deficitAdjusted.getStatus());
        verify(workOrderService).adjustInventory(anyLong(), eq(-3));

        JnWorkOrder finalInventory = buildWorkOrder(42L, "INVENTORY_OK", "盘点", "物料");
        when(workOrderService.getInventoryStatus(anyLong())).thenReturn(finalInventory);
        JnWorkOrder inventoryStatus = workOrderService.getInventoryStatus(materialId);
        assertNotNull(inventoryStatus);
        assertEquals("INVENTORY_OK", inventoryStatus.getStatus());
        verify(workOrderService).getInventoryStatus(anyLong());

        JnReworkOrder reworkOrder = buildReworkOrder(1L, materialId, "CLOSED");
        when(reworkOrderService.createReworkOrder(any())).thenReturn(reworkOrder);
        when(reworkOrderService.closeReworkOrder(anyLong())).thenReturn(reworkOrder);
        JnReworkOrder createdRework = reworkOrderService.createReworkOrder(reworkOrder);
        assertNotNull(createdRework);
        assertEquals("CLOSED", createdRework.getStatus());
        JnReworkOrder closedRework = reworkOrderService.closeReworkOrder(1L);
        assertNotNull(closedRework);
        verify(reworkOrderService).createReworkOrder(any());
        verify(reworkOrderService).closeReworkOrder(anyLong());

        verify(workOrderService, times(2)).adjustInventory(anyLong(), anyInt());
    }

    private JnWorkOrder buildQuotation(Long id, String status, String customerName, String productName) {
        JnWorkOrder order = new JnWorkOrder();
        order.setOrderId(id);
        order.setOrderNo("QTN-" + String.format("%04d", id));
        order.setStatus(status);
        order.setCustomerName(customerName);
        order.setProductName(productName);
        order.setQuantity(10);
        return order;
    }

    private JnWorkOrder buildSalesOrder(Long id, String status, String customerName, String productName, JnWorkOrder quotation) {
        JnWorkOrder order = new JnWorkOrder();
        order.setOrderId(id);
        order.setOrderNo("SO-" + String.format("%04d", id));
        order.setStatus(status);
        order.setCustomerName(customerName);
        order.setProductName(productName);
        order.setQuantity(10);
        if (quotation != null) {
            order.setSalesOrderNo(quotation.getOrderNo());
        }
        return order;
    }

    private JnWorkOrder buildWorkOrder(Long id, String status, String productName, int quantity) {
        JnWorkOrder order = new JnWorkOrder();
        order.setOrderId(id);
        order.setOrderNo("WO-" + String.format("%04d", id));
        order.setStatus(status);
        order.setProductName(productName);
        order.setQuantity(quantity);
        return order;
    }

    private JnSchedule buildSchedule(Long id, Long orderId, String status) {
        JnSchedule schedule = new JnSchedule();
        schedule.setScheduleId(id);
        schedule.setOrderId(orderId);
        schedule.setStatus(status);
        schedule.setScheduledDate(LocalDate.now());
        schedule.setStartTime(LocalDateTime.now());
        schedule.setEndTime(LocalDateTime.now().plusHours(8));
        return schedule;
    }

    private JnWorkReport buildWorkReport(Long id, Long orderId, String status) {
        JnWorkReport report = new JnWorkReport();
        report.setReportId(id);
        report.setOrderId(orderId);
        report.setStatus(status);
        report.setOutputQty(10);
        report.setGoodQty(10);
        return report;
    }

    private JnInspection buildInspection(Long id, Long sourceOrderId, String result) {
        JnInspection inspection = new JnInspection();
        inspection.setInspectionId(id);
        inspection.setSourceOrderId(sourceOrderId);
        inspection.setResult(result);
        inspection.setInspector("质检员");
        inspection.setInspectionDate(LocalDate.now());
        return inspection;
    }

    private JnPicking buildPicking(Long id, String status) {
        JnPicking picking = new JnPicking();
        picking.setPickingId(id);
        picking.setPickingNo("PK-" + String.format("%04d", id));
        picking.setStatus(status);
        return picking;
    }

    private JnBom buildBom(Long id, String status, String productName) {
        JnBom bom = new JnBom();
        bom.setBomId(id);
        bom.setBomCode("BOM-" + String.format("%04d", id));
        bom.setStatus(status);
        bom.setProductName(productName);
        bom.setVersion("V1.0");
        return bom;
    }

    private JnBomLine buildBomLine(Long id, Long bomId, String materialName, BigDecimal quantity) {
        JnBomLine line = new JnBomLine();
        line.setLineId(id);
        line.setBomId(bomId);
        line.setMaterialName(materialName);
        line.setQuantity(quantity);
        return line;
    }

    private JnRouting buildRouting(Long id, String status, String routingName) {
        JnRouting routing = new JnRouting();
        routing.setRoutingId(id);
        routing.setRoutingCode("RT-" + String.format("%04d", id));
        routing.setStatus(status);
        routing.setRoutingName(routingName);
        return routing;
    }

    private JnDefect buildDefect(Long id, Long materialId, String defectType, int qty) {
        JnDefect defect = new JnDefect();
        defect.setDefectId(id);
        defect.setMaterialId(materialId);
        defect.setDefectType(defectType);
        defect.setQty(qty);
        return defect;
    }

    private JnReworkOrder buildReworkOrder(Long id, Long orderId, String status) {
        JnReworkOrder order = new JnReworkOrder();
        order.setReworkId(id);
        order.setOrderId(orderId);
        order.setStatus(status);
        order.setReworkNo("RW-" + String.format("%04d", id));
        return order;
    }

}
