package com.jn.erp.production.bom.service;

import com.jn.erp.production.bom.domain.JnBom;
import com.jn.erp.production.bom.domain.JnBomLine;

import java.util.List;

public interface IJnBomService {

    List<JnBom> selectList(JnBom query);

    JnBom getById(Long bomId);

    int insertBom(JnBom bom, List<JnBomLine> lines);

    int updateBom(JnBom bom, List<JnBomLine> lines);

    int deleteByIds(Long[] bomIds);

    void updateStatus(Long bomId, String newStatus);

    JnBom buildTree(Long bomId);

    java.math.BigDecimal calculateCost(Long bomId);

    void replaceMaterial(Long bomId, Long oldMaterialId, Long newMaterialId);

    JnBom copyBom(Long sourceBomId, String newBomName);

    JnBom getBomTree(Long bomId);
}
