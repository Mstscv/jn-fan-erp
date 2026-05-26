package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.mapper.SysNoticeMapper;
import com.ruoyi.system.service.ISysNoticeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements ISysNoticeService {

    @Override
    public SysNotice selectNoticeById(Long noticeId) {
        return baseMapper.selectById(noticeId);
    }

    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice) {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        if (notice.getNoticeTitle() != null && !notice.getNoticeTitle().isEmpty()) {
            wrapper.like(SysNotice::getNoticeTitle, notice.getNoticeTitle());
        }
        if (notice.getNoticeType() != null && !notice.getNoticeType().isEmpty()) {
            wrapper.eq(SysNotice::getNoticeType, notice.getNoticeType());
        }
        wrapper.orderByDesc(SysNotice::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public int insertNotice(SysNotice notice) {
        return baseMapper.insert(notice);
    }

    @Override
    @Transactional
    public int updateNotice(SysNotice notice) {
        return baseMapper.updateById(notice);
    }

    @Override
    @Transactional
    public int deleteNoticeByIds(Long[] noticeIds) {
        int count = 0;
        for (Long noticeId : noticeIds) {
            count += baseMapper.deleteById(noticeId);
        }
        return count;
    }
}
