package com.jn.erp.material.mapper;

import com.jn.erp.material.domain.JnMaterial;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JnMaterialMapper extends BaseMapperPlus<JnMaterial> {

    List<JnMaterial> selectListWithFan(@Param("material") JnMaterial material);

    JnMaterial selectByMaterialCode(@Param("materialCode") String materialCode);
}
