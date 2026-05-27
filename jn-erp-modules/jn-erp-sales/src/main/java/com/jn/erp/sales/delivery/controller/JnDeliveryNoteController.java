package com.jn.erp.sales.delivery.controller;

import com.jn.erp.sales.delivery.domain.JnDeliveryNote;
import com.jn.erp.sales.delivery.service.IJnDeliveryNoteService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/erp/delivery")
public class JnDeliveryNoteController extends BaseController {

    @Autowired
    private IJnDeliveryNoteService jnDeliveryNoteService;

    @GetMapping("/list")
    public TableDataInfo list(JnDeliveryNote deliveryNote) {
        startPage();
        List<JnDeliveryNote> list = jnDeliveryNoteService.selectList(deliveryNote);
        return getDataTable(list);
    }

    @GetMapping("/{deliveryId}")
    public AjaxResult getInfo(@PathVariable Long deliveryId) {
        return success(jnDeliveryNoteService.getById(deliveryId));
    }

    @PostMapping
    public AjaxResult add(@RequestBody JnDeliveryNote deliveryNote) {
        return toAjax(jnDeliveryNoteService.insert(deliveryNote));
    }

    @PutMapping
    public AjaxResult edit(@RequestBody JnDeliveryNote deliveryNote) {
        return toAjax(jnDeliveryNoteService.update(deliveryNote));
    }

    @DeleteMapping("/{deliveryIds}")
    public AjaxResult remove(@PathVariable Long[] deliveryIds) {
        return toAjax(jnDeliveryNoteService.deleteByIds(deliveryIds));
    }

    @PutMapping("/{deliveryId}/ship")
    public AjaxResult ship(@PathVariable Long deliveryId) {
        return toAjax(jnDeliveryNoteService.markShipped(deliveryId));
    }
}
