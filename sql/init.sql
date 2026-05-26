-- ============================================================
-- 精恩风机ERP - 数据库初始化脚本 (基于若依框架表结构)
-- 版本: v1.0.0
-- 日期: 2026-05-26
-- ============================================================

CREATE DATABASE IF NOT EXISTS `jn_fan_erp`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `jn_fan_erp`;

-- ----------------------------
-- 部门表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id`     bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `parent_id`   bigint(20)  DEFAULT 0  COMMENT '父部门ID',
  `ancestors`   varchar(500) DEFAULT '' COMMENT '祖级列表',
  `dept_name`   varchar(100) DEFAULT '' COMMENT '部门名称',
  `order_num`   int(11)     DEFAULT 0  COMMENT '显示顺序',
  `leader`      varchar(50)  DEFAULT '' COMMENT '负责人',
  `phone`       varchar(20)  DEFAULT '' COMMENT '联系电话',
  `email`       varchar(100) DEFAULT '' COMMENT '邮箱',
  `status`      char(1)     DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `del_flag`    char(1)     DEFAULT '0' COMMENT '删除标志(0正常 1删除)',
  `create_by`   varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    COMMENT '创建时间',
  `update_by`   varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

INSERT INTO `sys_dept` VALUES (100, 0, '0', '精恩风机', 0, '总经理', '0510-xxxxxxxx', '', '0', '0', 'admin', NOW(), '', NULL);
INSERT INTO `sys_dept` VALUES (101, 100, '0,100', '销售部', 1, '', '', '', '0', '0', 'admin', NOW(), '', NULL);
INSERT INTO `sys_dept` VALUES (102, 100, '0,100', '生产部', 2, '', '', '', '0', '0', 'admin', NOW(), '', NULL);
INSERT INTO `sys_dept` VALUES (103, 100, '0,100', '采购部', 3, '', '', '', '0', '0', 'admin', NOW(), '', NULL);
INSERT INTO `sys_dept` VALUES (104, 100, '0,100', '仓库', 4, '', '', '', '0', '0', 'admin', NOW(), '', NULL);
INSERT INTO `sys_dept` VALUES (105, 100, '0,100', '财务部', 5, '', '', '', '0', '0', 'admin', NOW(), '', NULL);
INSERT INTO `sys_dept` VALUES (106, 100, '0,100', '质检部', 6, '', '', '', '0', '0', 'admin', NOW(), '', NULL);
INSERT INTO `sys_dept` VALUES (107, 100, '0,100', '技术部', 7, '', '', '', '0', '0', 'admin', NOW(), '', NULL);

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id`     bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id`     bigint(20)  DEFAULT NULL COMMENT '部门ID',
  `user_name`   varchar(50) NOT NULL COMMENT '登录账号',
  `nick_name`   varchar(50) DEFAULT '' COMMENT '昵称',
  `user_type`   varchar(10) DEFAULT 'sys_user' COMMENT '用户类型',
  `email`       varchar(100) DEFAULT '' COMMENT '邮箱',
  `phone`       varchar(20)  DEFAULT '' COMMENT '手机号',
  `sex`         char(1)     DEFAULT '0' COMMENT '性别(0男 1女 2未知)',
  `avatar`      varchar(255) DEFAULT '' COMMENT '头像',
  `password`    varchar(255) DEFAULT '' COMMENT '密码(BCrypt加密)',
  `status`      char(1)     DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `del_flag`    char(1)     DEFAULT '0' COMMENT '删除标志',
  `login_ip`    varchar(50)  DEFAULT '' COMMENT '最后登录IP',
  `login_date`  datetime    COMMENT '最后登录日期',
  `create_by`   varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    COMMENT '创建时间',
  `update_by`   varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    COMMENT '更新时间',
  `remark`      varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_user_name` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 密码 admin123 -> $2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2
INSERT INTO `sys_user` VALUES (1, 100, 'admin', '系统管理员', 'sys_user', 'admin@jnfeng.com', '13800000000', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', NOW(), 'admin', NOW(), '', NULL, '超级管理员');

-- ----------------------------
-- 角色表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id`     bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name`   varchar(50) NOT NULL COMMENT '角色名称',
  `role_key`    varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort`   int(11)     DEFAULT 0 COMMENT '显示顺序',
  `data_scope`  char(1)     DEFAULT '1' COMMENT '数据范围(1全部 2自定义 3本部门 4本部门及以下 5仅本人)',
  `status`      char(1)     DEFAULT '0' COMMENT '状态',
  `del_flag`    char(1)     DEFAULT '0' COMMENT '删除标志',
  `create_by`   varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    COMMENT '创建时间',
  `update_by`   varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    COMMENT '更新时间',
  `remark`      varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', '0', '0', 'admin', NOW(), '', NULL, '超级管理员');
INSERT INTO `sys_role` VALUES (2, '销售经理', 'sales_mgr', 2, '3', '0', '0', 'admin', NOW(), '', NULL, '销售经理');
INSERT INTO `sys_role` VALUES (3, '生产主管', 'prod_mgr', 3, '3', '0', '0', 'admin', NOW(), '', NULL, '生产主管');
INSERT INTO `sys_role` VALUES (4, '采购主管', 'purch_mgr', 4, '3', '0', '0', 'admin', NOW(), '', NULL, '采购主管');
INSERT INTO `sys_role` VALUES (5, '仓库管理员', 'wh_mgr', 5, '3', '0', '0', 'admin', NOW(), '', NULL, '仓库管理员');
INSERT INTO `sys_role` VALUES (6, '财务人员', 'finance', 6, '1', '0', '0', 'admin', NOW(), '', NULL, '财务人员');
INSERT INTO `sys_role` VALUES (7, '质检员', 'qc', 7, '3', '0', '0', 'admin', NOW(), '', NULL, '质检员');

-- ----------------------------
-- 菜单表（精简版，主要菜单结构）
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id`     bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name`   varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id`   bigint(20)  DEFAULT 0  COMMENT '父菜单ID',
  `order_num`   int(11)     DEFAULT 0  COMMENT '显示顺序',
  `path`        varchar(200) DEFAULT '' COMMENT '路由地址',
  `component`   varchar(255) DEFAULT '' COMMENT '组件路径',
  `query`       varchar(255) DEFAULT '' COMMENT '路由参数',
  `is_frame`    char(1)     DEFAULT '1' COMMENT '是否外链',
  `is_cache`    char(1)     DEFAULT '0' COMMENT '是否缓存',
  `menu_type`   char(1)     DEFAULT '' COMMENT '菜单类型(M目录 C菜单 F按钮)',
  `visible`     char(1)     DEFAULT '0' COMMENT '可见性',
  `status`      char(1)     DEFAULT '0' COMMENT '状态',
  `perms`       varchar(100) DEFAULT '' COMMENT '权限标识',
  `icon`        varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by`   varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    COMMENT '创建时间',
  `update_by`   varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    COMMENT '更新时间',
  `remark`      varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- 系统管理
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 1, 'system', '', '', '1', '0', 'M', '0', '0', '', 'system', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', '1', '0', 'C', '0', '0', 'system:user:list', 'user', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', '1', '0', 'C', '0', '0', 'system:role:list', 'peoples', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', '1', '0', 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', '1', '0', 'C', '0', '0', 'system:dept:list', 'tree', 'admin', NOW(), '', NULL, '');

-- 基础数据
INSERT INTO `sys_menu` VALUES (2, '基础数据', 0, 2, 'base', '', '', '1', '0', 'M', '0', '0', '', 'documentation', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (200, '物料管理', 2, 1, 'material', 'base/material/index', '', '1', '0', 'C', '0', '0', 'base:material:list', 'component', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (201, '客户管理', 2, 2, 'customer', 'base/customer/index', '', '1', '0', 'C', '0', '0', 'base:customer:list', 'user', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (202, '供应商管理', 2, 3, 'supplier', 'base/supplier/index', '', '1', '0', 'C', '0', '0', 'base:supplier:list', 'nested', 'admin', NOW(), '', NULL, '');

-- 销售管理
INSERT INTO `sys_menu` VALUES (3, '销售管理', 0, 3, 'sales', '', '', '1', '0', 'M', '0', '0', '', 'shopping', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (300, '销售报价', 3, 1, 'quotation', 'sales/quotation/index', '', '1', '0', 'C', '0', '0', 'sales:quotation:list', 'form', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (301, '销售订单', 3, 2, 'order', 'sales/order/index', '', '1', '0', 'C', '0', '0', 'sales:order:list', 'list', 'admin', NOW(), '', NULL, '');

-- 采购管理
INSERT INTO `sys_menu` VALUES (4, '采购管理', 0, 4, 'purchase', '', '', '1', '0', 'M', '0', '0', '', 'shopping-cart', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (400, '采购需求', 4, 1, 'demand', 'purchase/demand/index', '', '1', '0', 'C', '0', '0', 'purchase:demand:list', 'guide', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (401, '采购订单', 4, 2, 'order', 'purchase/order/index', '', '1', '0', 'C', '0', '0', 'purchase:order:list', 'list', 'admin', NOW(), '', NULL, '');

-- 仓库管理
INSERT INTO `sys_menu` VALUES (5, '仓库管理', 0, 5, 'warehouse', '', '', '1', '0', 'M', '0', '0', '', 'education', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (500, '入库管理', 5, 1, 'instock', 'warehouse/instock/index', '', '1', '0', 'C', '0', '0', 'warehouse:instock:list', 'logininfor', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (501, '出库管理', 5, 2, 'outstock', 'warehouse/outstock/index', '', '1', '0', 'C', '0', '0', 'warehouse:outstock:list', 'logininfor', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (502, '库存查询', 5, 3, 'inventory', 'warehouse/inventory/index', '', '1', '0', 'C', '0', '0', 'warehouse:inventory:list', 'chart', 'admin', NOW(), '', NULL, '');

-- 生产管理
INSERT INTO `sys_menu` VALUES (6, '生产管理', 0, 6, 'production', '', '', '1', '0', 'M', '0', '0', '', 'monitor', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (600, 'BOM管理', 6, 1, 'bom', 'production/bom/index', '', '1', '0', 'C', '0', '0', 'production:bom:list', 'tree-table', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (601, '生产工单', 6, 2, 'workorder', 'production/workorder/index', '', '1', '0', 'C', '0', '0', 'production:workorder:list', 'documentation', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (602, '工序报工', 6, 3, 'report', 'production/report/index', '', '1', '0', 'C', '0', '0', 'production:report:list', 'edit', 'admin', NOW(), '', NULL, '');

-- 财务管理
INSERT INTO `sys_menu` VALUES (7, '财务管理', 0, 7, 'finance', '', '', '1', '0', 'M', '0', '0', '', 'money', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (700, '应收账款', 7, 1, 'receivable', 'finance/receivable/index', '', '1', '0', 'C', '0', '0', 'finance:receivable:list', 'date', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (701, '应付账款', 7, 2, 'payable', 'finance/payable/index', '', '1', '0', 'C', '0', '0', 'finance:payable:list', 'date-range', 'admin', NOW(), '', NULL, '');

-- 报表
INSERT INTO `sys_menu` VALUES (8, '报表中心', 0, 8, 'report', '', '', '1', '0', 'M', '0', '0', '', 'chart', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (800, '经营看板', 8, 1, 'dashboard', 'report/dashboard/index', '', '1', '0', 'C', '0', '0', 'report:dashboard:list', 'dashboard', 'admin', NOW(), '', NULL, '');

-- ----------------------------
-- 字典表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `dict_id`     bigint(20)  NOT NULL AUTO_INCREMENT,
  `dict_name`   varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type`   varchar(100) DEFAULT '' COMMENT '字典类型',
  `status`      char(1)     DEFAULT '0' COMMENT '状态',
  `create_by`   varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    COMMENT '创建时间',
  `update_by`   varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    COMMENT '更新时间',
  `remark`      varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `uk_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

INSERT INTO `sys_dict_type` VALUES (1, '风机类型', 'fan_type', '0', 'admin', NOW(), '', NULL, '离心风机/轴流风机/混流风机');
INSERT INTO `sys_dict_type` VALUES (2, '物料分类', 'material_category', '0', 'admin', NOW(), '', NULL, '原材料/半成品/成品/辅料');
INSERT INTO `sys_dict_type` VALUES (3, '仓库', 'warehouse', '0', 'admin', NOW(), '', NULL, '');

DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `dict_code`   bigint(20)  NOT NULL AUTO_INCREMENT,
  `dict_sort`   int(11)     DEFAULT 0 COMMENT '排序',
  `dict_label`  varchar(100) DEFAULT '' COMMENT '标签',
  `dict_value`  varchar(100) DEFAULT '' COMMENT '键值',
  `dict_type`   varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class`   varchar(100) DEFAULT '' COMMENT '样式',
  `list_class`  varchar(100) DEFAULT '' COMMENT '表格回显样式',
  `is_default`  char(1)     DEFAULT 'N' COMMENT '是否默认',
  `status`      char(1)     DEFAULT '0' COMMENT '状态',
  `create_by`   varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` datetime    COMMENT '创建时间',
  `update_by`   varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` datetime    COMMENT '更新时间',
  `remark`      varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

INSERT INTO `sys_dict_data` VALUES (1, 1, '离心风机', 'CENTRIFUGAL', 'fan_type', '', '', 'N', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_dict_data` VALUES (2, 2, '轴流风机', 'AXIAL', 'fan_type', '', '', 'N', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_dict_data` VALUES (3, 3, '混流风机', 'MIXED_FLOW', 'fan_type', '', '', 'N', '0', 'admin', NOW(), '', NULL, '');

-- ============================================================
-- 以下为Sprint 01环境验证完毕后的标记
-- ============================================================
-- Sprint 02将补充:
--   jn_material (风机物料主数据)
--   jn_customer, jn_supplier (客户/供应商)
--   销售域/采购域/仓库域全量表
--   jn_bom / jn_bom_line / jn_bom_version (BOM)
--   jn_work_order / jn_work_report (生产工单/报工)
--   财务域全量表
-- ============================================================
