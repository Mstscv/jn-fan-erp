package com.jn.erp.production.bom.copy;

import com.jn.erp.production.bom.domain.JnBomLine;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BomDeepCopyHelper {

    public List<JnBomLine> deepCopyLines(List<JnBomLine> sourceLines, Long newBomId) {
        List<JnBomLine> newLines = new ArrayList<>();
        for (JnBomLine sourceLine : sourceLines) {
            JnBomLine newLine = new JnBomLine();
            newLine.setBomId(newBomId);
            newLine.setParentLineId(null);
            newLine.setLineSeq(sourceLine.getLineSeq());
            newLine.setLineType(sourceLine.getLineType());
            newLine.setMaterialId(sourceLine.getMaterialId());
            newLine.setMaterialCode(sourceLine.getMaterialCode());
            newLine.setMaterialName(sourceLine.getMaterialName());
            newLine.setSpec(sourceLine.getSpec());
            newLine.setUnit(sourceLine.getUnit());
            newLine.setQuantity(sourceLine.getQuantity());
            newLine.setOptionalRule(sourceLine.getOptionalRule());
            newLine.setSubstituteRule(sourceLine.getSubstituteRule());
            newLine.setSubBomId(sourceLine.getSubBomId());
            newLine.setRemark(sourceLine.getRemark());
            newLines.add(newLine);
        }
        return newLines;
    }
}
