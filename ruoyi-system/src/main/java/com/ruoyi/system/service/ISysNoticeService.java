package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysNotice;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ISysNoticeService extends IService<SysNotice> {

    SysNotice selectNoticeById(Long noticeId);

    List<SysNotice> selectNoticeList(SysNotice notice);

    int insertNotice(SysNotice notice);

    int updateNotice(SysNotice notice);

    int deleteNoticeByIds(Long[] noticeIds);
}
