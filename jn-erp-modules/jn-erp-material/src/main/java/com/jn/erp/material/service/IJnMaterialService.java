package com.jn.erp.material.service;

import com.jn.erp.material.domain.JnMaterial;

import java.util.List;

public interface IJnMaterialService {

    List<JnMaterial> selectList(JnMaterial material);

    JnMaterial selectById(Long materialId);

    int insert(JnMaterial material);

    int update(JnMaterial material);

    int deleteByIds(Long[] materialIds);

    String getNextCode(String fanType, String fanModel, String category);

    boolean checkCodeUnique(String materialCode);
}
