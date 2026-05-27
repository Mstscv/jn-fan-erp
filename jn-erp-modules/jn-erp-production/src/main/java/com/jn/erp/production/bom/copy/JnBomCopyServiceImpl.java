package com.jn.erp.production.bom.copy;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jn.erp.production.bom.domain.JnBom;
import com.jn.erp.production.bom.domain.JnBomLine;
import com.jn.erp.production.bom.mapper.JnBomLineMapper;
import com.jn.erp.production.bom.mapper.JnBomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JnBomCopyServiceImpl implements IJnBomCopyService {

    @Autowired
    private JnBomMapper bomMapper;

    @Autowired
    private JnBomLineMapper bomLineMapper;

    @Autowired
    private BomDeepCopyHelper deepCopyHelper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JnBom copyBom(Long sourceBomId, String newBomName, String newBomCode) {
        JnBom sourceBom = bomMapper.selectById(sourceBomId);
        if (sourceBom == null) {
            throw new RuntimeException("源BOM不存在: " + sourceBomId);
        }

        JnBom newBom = new JnBom();
        newBom.setBomCode(newBomCode);
        newBom.setBomName(newBomName);
        newBom.setProductId(sourceBom.getProductId());
        newBom.setProductCode(sourceBom.getProductCode());
        newBom.setProductName(sourceBom.getProductName());
        newBom.setVersion("v1.0");
        newBom.setStatus("DRAFT");
        bomMapper.insert(newBom);

        List<JnBomLine> sourceLines = bomLineMapper.selectList(
                Wrappers.<JnBomLine>lambdaQuery().eq(JnBomLine::getBomId, sourceBomId));

        List<JnBomLine> newLines = deepCopyHelper.deepCopyLines(sourceLines, newBom.getBomId());
        for (JnBomLine line : newLines) {
            bomLineMapper.insert(line);
        }

        newBom.setLines(newLines);
        return newBom;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void quickReference(Long sourceBomId, Long targetBomId) {
        JnBom sourceBom = bomMapper.selectById(sourceBomId);
        if (sourceBom == null) {
            throw new RuntimeException("源BOM不存在: " + sourceBomId);
        }

        JnBom targetBom = bomMapper.selectById(targetBomId);
        if (targetBom == null) {
            throw new RuntimeException("目标BOM不存在: " + targetBomId);
        }

        JnBomLine refLine = new JnBomLine();
        refLine.setBomId(targetBomId);
        refLine.setLineType("SUB_ASSEMBLY");
        refLine.setMaterialId(sourceBom.getProductId());
        refLine.setMaterialCode(sourceBom.getProductCode());
        refLine.setMaterialName(sourceBom.getBomName());
        refLine.setQuantity(1);
        refLine.setSubBomId(sourceBomId);
        bomLineMapper.insert(refLine);
    }
}
