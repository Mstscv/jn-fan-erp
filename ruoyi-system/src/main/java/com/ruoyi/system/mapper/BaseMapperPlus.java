package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface BaseMapperPlus<T> extends BaseMapper<T> {

    default T selectVoById(Serializable id) {
        return this.selectById(id);
    }

    default List<T> selectVoList(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper) {
        return this.selectList(queryWrapper);
    }

    default <P extends IPage<T>> P selectVoPage(Page<T> page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper) {
        return (P) this.selectPage(page, queryWrapper);
    }

    default List<T> selectVoBatchIds(@Param(Constants.COLL) Collection<? extends Serializable> idList) {
        return this.selectBatchIds(idList);
    }
}
