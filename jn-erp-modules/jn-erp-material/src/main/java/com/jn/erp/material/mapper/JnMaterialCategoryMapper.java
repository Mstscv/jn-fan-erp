package com.jn.erp.material.mapper;

import com.jn.erp.material.domain.JnMaterialCategory;

import java.util.List;

public interface JnMaterialCategoryMapper extends BaseMapperPlus<JnMaterialCategory> {

    List<JnMaterialCategory> selectListByParentId(Long parentId);
}
