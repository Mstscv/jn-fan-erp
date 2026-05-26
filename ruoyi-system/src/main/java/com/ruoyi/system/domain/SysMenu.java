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
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long menuId;

    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    private String menuName;

    private Long parentId;

    private Integer orderNum;

    @Size(max = 200, message = "路由地址长度不能超过200个字符")
    private String path;

    @Size(max = 255, message = "组件路径长度不能超过255个字符")
    private String component;

    @Size(max = 255, message = "路由参数长度不能超过255个字符")
    private String query;

    private String isFrame;

    private String isCache;

    @NotBlank(message = "菜单类型不能为空")
    private String menuType;

    private String visible;

    private String status;

    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    private String perms;

    @Size(max = 100, message = "菜单图标长度不能超过100个字符")
    private String icon;

    @TableField(exist = false)
    private List<SysMenu> children = new ArrayList<>();

    @TableField(exist = false)
    private String parentName;
}
