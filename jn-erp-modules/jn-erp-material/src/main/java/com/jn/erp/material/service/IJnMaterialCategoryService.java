package com.jn.erp.material.service;

import com.jn.erp.material.domain.JnMaterialCategory;

import java.util.List;

public interface IJnMaterialCategoryService {

    List<JnMaterialCategory> selectList(JnMaterialCategory category);

    JnMaterialCategory selectById(Long categoryId);

    int insert(JnMaterialCategory category);

    int update(JnMaterialCategory category);

    int deleteByIds(Long[] categoryIds);
}
