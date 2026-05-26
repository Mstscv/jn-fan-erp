package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long deptId;

    private Long parentId;

    @Size(max = 500, message = "祖级列表长度不能超过500个字符")
    private String ancestors;

    @NotBlank(message = "部门名称不能为空")
    @Size(max = 100, message = "部门名称长度不能超过100个字符")
    private String deptName;

    private Integer orderNum;

    @Size(max = 50, message = "负责人长度不能超过50个字符")
    private String leader;

    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    private String phone;

    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    private String status;

    private String delFlag;

    @TableField(exist = false)
    private List<SysDept> children = new ArrayList<>();

    @TableField(exist = false)
    private Long parentName;
}
