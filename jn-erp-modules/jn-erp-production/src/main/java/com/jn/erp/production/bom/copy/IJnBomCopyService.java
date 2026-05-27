package com.jn.erp.production.bom.copy;

import com.jn.erp.production.bom.domain.JnBom;

public interface IJnBomCopyService {

    JnBom copyBom(Long sourceBomId, String newBomName, String newBomCode);

    void quickReference(Long sourceBomId, Long targetBomId);
}
