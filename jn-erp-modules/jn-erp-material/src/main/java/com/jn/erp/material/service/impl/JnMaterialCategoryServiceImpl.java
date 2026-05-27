package com.jn.erp.material.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jn.erp.material.domain.JnMaterialCategory;
import com.jn.erp.material.mapper.JnMaterialCategoryMapper;
import com.jn.erp.material.service.IJnMaterialCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JnMaterialCategoryServiceImpl extends ServiceImpl<JnMaterialCategoryMapper, JnMaterialCategory> implements IJnMaterialCategoryService {

    @Autowired
    private JnMaterialCategoryMapper categoryMapper;

    @Override
    public List<JnMaterialCategory> selectList(JnMaterialCategory category) {
        LambdaQueryWrapper<JnMaterialCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JnMaterialCategory::getDelFlag, "0");
        if (category.getCategoryName() != null && !category.getCategoryName().isEmpty()) {
            wrapper.like(JnMaterialCategory::getCategoryName, category.getCategoryName());
        }
        if (category.getParentId() != null) {
            wrapper.eq(JnMaterialCategory::getParentId, category.getParentId());
        }
        wrapper.orderByAsc(JnMaterialCategory::getOrderNum);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    public JnMaterialCategory selectById(Long categoryId) {
        return categoryMapper.selectById(categoryId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(JnMaterialCategory category) {
        if (category.getStatus() == null) {
            category.setStatus("0");
        }
        if (category.getDelFlag() == null) {
            category.setDelFlag("0");
        }
        return categoryMapper.insert(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(JnMaterialCategory category) {
        return categoryMapper.updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Long[] categoryIds) {
        int count = 0;
        for (Long id : categoryIds) {
            JnMaterialCategory category = categoryMapper.selectById(id);
            if (category != null) {
                category.setDelFlag("1");
                count += categoryMapper.updateById(category);
            }
        }
        return count;
    }
}
