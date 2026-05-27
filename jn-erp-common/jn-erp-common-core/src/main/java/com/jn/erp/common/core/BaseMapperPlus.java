package com.jn.erp.common.core;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public interface BaseMapperPlus<T> extends BaseMapper<T> {

    default <V> V selectVoById(Serializable id, Class<V> clazz) {
        T entity = this.selectById(id);
        if (entity == null) {
            return null;
        }
        V vo = BeanUtils.instantiateClass(clazz);
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    default <V> List<V> selectVoList(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper, Class<V> clazz) {
        List<T> list = this.selectList(queryWrapper);
        return list.stream().map(entity -> {
            V vo = BeanUtils.instantiateClass(clazz);
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    default <V> Page<V> selectVoPage(Page<T> page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper, Class<V> clazz) {
        Page<T> result = this.selectPage(page, queryWrapper);
        return result.convert(entity -> {
            V vo = BeanUtils.instantiateClass(clazz);
            BeanUtils.copyProperties(entity, vo);
            return vo;
        });
    }
}
