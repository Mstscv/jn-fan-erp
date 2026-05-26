package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_notice")
public class SysNotice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long noticeId;

    @NotBlank(message = "公告标题不能为空")
    @Size(max = 50, message = "公告标题长度不能超过50个字符")
    private String noticeTitle;

    @NotBlank(message = "公告类型不能为空")
    private String noticeType;

    private String noticeContent;

    private String status;
}
