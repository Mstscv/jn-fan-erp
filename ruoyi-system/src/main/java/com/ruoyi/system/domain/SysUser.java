package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long userId;

    private Long deptId;

    @TableField(exist = false)
    private SysDept dept;

    @NotBlank(message = "登录账号不能为空")
    @Size(max = 50, message = "登录账号长度不能超过50个字符")
    private String userName;

    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickName;

    private String userType;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    @Size(max = 20, message = "手机号码长度不能超过20个字符")
    private String phone;

    private String sex;

    @Size(max = 255, message = "头像地址长度不能超过255个字符")
    private String avatar;

    @JsonIgnore
    @JsonProperty
    private String password;

    private String status;

    private String delFlag;

    @Size(max = 50, message = "登录IP长度不能超过50个字符")
    private String loginIp;

    private LocalDateTime loginDate;

    @TableField(exist = false)
    private List<SysRole> roles;

    @TableField(exist = false)
    private Long[] roleIds;

    @TableField(exist = false)
    private Long[] postIds;

    @TableField(exist = false)
    private Long roleId;

    public boolean isAdmin() {
        return userId != null && 1L == userId;
    }
}
