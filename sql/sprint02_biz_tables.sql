-- ============================================================
-- 精恩风机ERP - 业务表结构设计 (Sprint 02)
-- 包含: 物料/客户/供应商/销售/采购/仓库/BOM/生产/质检/财务
-- 版本: v2.0.0
-- 日期: 2026-05-26
-- ============================================================

USE `jn_fan_erp`;

-- ============================================================
-- 一、基础数据域
-- ============================================================

-- 1.1 物料主数据 ★风机专用属性★
DROP TABLE IF EXISTS `jn_material`;
CREATE TABLE `jn_material` (
  `material_id`       bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '物料ID',
  `material_code`     varchar(100) NOT NULL COMMENT '物料编码',
  `material_name`     varchar(200) NOT NULL COMMENT '物料名称',
  `spec`              varchar(500) DEFAULT '' COMMENT '规格型号',
  `category_id`       bigint(20)   DEFAULT NULL COMMENT '分类ID(sys_dict)',
  `category_name`     varchar(50)  DEFAULT '' COMMENT '分类名称(原材料/半成品/成品/辅料)',
  `unit`              varchar(20)  DEFAULT '台' COMMENT '单位',
  `unit_price`        decimal(10,2) DEFAULT 0.00 COMMENT '参考单价',
  `safety_stock`      int(11)      DEFAULT 0 COMMENT '安全库存量',
  `status`            char(1)      DEFAULT '0' COMMENT '状态(0启用 1停用)',
  `del_flag`          char(1)      DEFAULT '0' COMMENT '删除标志',

  -- 风机专用属性
  `fan_type`          varchar(30)  DEFAULT '' COMMENT '风机类型(离心/轴流/混流)',
  `fan_model`         varchar(100) DEFAULT '' COMMENT '型号(如LR406)',
  `airflow_cfm`       decimal(10,1) DEFAULT NULL COMMENT '风量(CFM)',
  `pressure_pa`       decimal(10,1) DEFAULT NULL COMMENT '风压(Pa)',
  `power_kw`          decimal(8,2) DEFAULT NULL COMMENT '功率(kW)',
  `rpm`               int(11)      DEFAULT NULL COMMENT '转速(RPM)',
  `noise_dba`         decimal(5,1) DEFAULT NULL COMMENT '噪音(dBA)',
  `impeller_diameter` int(11)      DEFAULT NULL COMMENT '叶轮直径(mm)',
  `weight_kg`         decimal(8,2) DEFAULT NULL COMMENT '重量(kg)',
  `is_configurable`   char(1)      DEFAULT 'N' COMMENT '是否可选配(Y/N)',
  `config_rules`      varchar(2000) DEFAULT '' COMMENT '选配规则JSON',
  `drawing_no`        varchar(100) DEFAULT '' COMMENT '图纸编号',
  `version`           int(11)      DEFAULT 1 COMMENT '版本号',

  `remark`            varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`         varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`       datetime     COMMENT '创建时间',
  `update_by`         varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`       datetime     COMMENT '更新时间',
  PRIMARY KEY (`material_id`),
  UNIQUE KEY `uk_material_code` (`material_code`),
  KEY `idx_fan_type` (`fan_type`),
  KEY `idx_fan_model` (`fan_model`),
  KEY `idx_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料主数据(含风机专用属性)';

-- 1.2 物料分类(若依字典| 自定义扩展)
DROP TABLE IF EXISTS `jn_material_category`;
CREATE TABLE `jn_material_category` (
  `category_id`   bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `parent_id`     bigint(20)   DEFAULT 0 COMMENT '上级分类',
  `category_name` varchar(100) NOT NULL COMMENT '分类名称',
  `order_num`     int(11)      DEFAULT 0 COMMENT '排序',
  `status`        char(1)      DEFAULT '0' COMMENT '状态',
  `create_time`   datetime     COMMENT '创建时间',
  `update_time`   datetime     COMMENT '更新时间',
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料分类(树形)';

INSERT INTO `jn_material_category` VALUES (1,0,'风机成品',1,'0',NOW(),NULL);
INSERT INTO `jn_material_category` VALUES (2,0,'半成品',2,'0',NOW(),NULL);
INSERT INTO `jn_material_category` VALUES (3,0,'原材料',3,'0',NOW(),NULL);
INSERT INTO `jn_material_category` VALUES (4,0,'辅料包材',4,'0',NOW(),NULL);
INSERT INTO `jn_material_category` VALUES (11,1,'离心风机',1,'0',NOW(),NULL);
INSERT INTO `jn_material_category` VALUES (12,1,'轴流风机',2,'0',NOW(),NULL);
INSERT INTO `jn_material_category` VALUES (13,1,'混流风机',3,'0',NOW(),NULL);
INSERT INTO `jn_material_category` VALUES (21,2,'叶轮组',1,'0',NOW(),NULL);
INSERT INTO `jn_material_category` VALUES (22,2,'蜗壳组',2,'0',NOW(),NULL);
INSERT INTO `jn_material_category` VALUES (23,2,'底座组',3,'0',NOW(),NULL);
INSERT INTO `jn_material_category` VALUES (31,3,'电机',1,'0',NOW(),NULL);
INSERT INTO `jn_material_category` VALUES (32,3,'钢材',2,'0',NOW(),NULL);
INSERT INTO `jn_material_category` VALUES (33,3,'轴承',3,'0',NOW(),NULL);

-- 1.3 客户管理
DROP TABLE IF EXISTS `jn_customer`;
CREATE TABLE `jn_customer` (
  `customer_id`   bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '客户ID',
  `customer_code` varchar(50)  NOT NULL COMMENT '客户编号',
  `customer_name` varchar(200) NOT NULL COMMENT '客户名称',
  `contact`       varchar(50)  DEFAULT '' COMMENT '联系人',
  `phone`         varchar(50)  DEFAULT '' COMMENT '电话',
  `email`         varchar(100) DEFAULT '' COMMENT '邮箱',
  `region`        varchar(100) DEFAULT '' COMMENT '地区',
  `address`       varchar(500) DEFAULT '' COMMENT '地址',
  `credit_limit`  decimal(12,2) DEFAULT 0.00 COMMENT '信用额度',
  `tax_no`        varchar(50)  DEFAULT '' COMMENT '税号',
  `bank_info`     varchar(200) DEFAULT '' COMMENT '开户行信息',
  `level`         char(1)      DEFAULT '3' COMMENT '客户等级(1-5)',
  `status`        char(1)      DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `del_flag`      char(1)      DEFAULT '0' COMMENT '删除标志',
  `remark`        varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`     varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`   datetime     COMMENT '创建时间',
  `update_by`     varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`   datetime     COMMENT '更新时间',
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `uk_customer_code` (`customer_code`),
  KEY `idx_customer_name` (`customer_name`),
  KEY `idx_region` (`region`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户管理';

-- 1.4 供应商管理
DROP TABLE IF EXISTS `jn_supplier`;
CREATE TABLE `jn_supplier` (
  `supplier_id`   bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '供应商ID',
  `supplier_code` varchar(50)  NOT NULL COMMENT '供应商编号',
  `supplier_name` varchar(200) NOT NULL COMMENT '供应商名称',
  `contact`       varchar(50)  DEFAULT '' COMMENT '联系人',
  `phone`         varchar(50)  DEFAULT '' COMMENT '电话',
  `email`         varchar(100) DEFAULT '' COMMENT '邮箱',
  `biz_scope`     varchar(500) DEFAULT '' COMMENT '主营业务',
  `address`       varchar(500) DEFAULT '' COMMENT '地址',
  `rating`        int(11)      DEFAULT 3 COMMENT '评级(1-5)',
  `cooperation`   char(1)      DEFAULT '0' COMMENT '合作状态(0合作中 1暂停 2终止)',
  `del_flag`      char(1)      DEFAULT '0' COMMENT '删除标志',
  `remark`        varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`     varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`   datetime     COMMENT '创建时间',
  `update_by`     varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`   datetime     COMMENT '更新时间',
  PRIMARY KEY (`supplier_id`),
  UNIQUE KEY `uk_supplier_code` (`supplier_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商管理';

-- ============================================================
-- 二、销售域
-- ============================================================

-- 2.1 销售报价单
DROP TABLE IF EXISTS `jn_sales_quotation`;
CREATE TABLE `jn_sales_quotation` (
  `quotation_id`     bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '报价ID',
  `quotation_no`     varchar(50)  NOT NULL COMMENT '报价单号',
  `customer_id`      bigint(20)   NOT NULL COMMENT '客户ID',
  `customer_name`    varchar(200) DEFAULT '' COMMENT '客户名称',
  `salesman`         varchar(64)  DEFAULT '' COMMENT '业务员',
  `total_amount`     decimal(12,2) DEFAULT 0.00 COMMENT '总金额',
  `valid_days`       int(11)      DEFAULT 30 COMMENT '有效期(天)',
  `valid_until`      date         COMMENT '有效期至',
  `status`           char(1)      DEFAULT '0' COMMENT '状态(0待审批 1已通过 2已拒绝 3已过期)',
  `del_flag`         char(1)      DEFAULT '0' COMMENT '删除标志',
  `remark`           varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`        varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`      datetime     COMMENT '创建时间',
  `approve_by`       varchar(64)  DEFAULT '' COMMENT '审批人',
  `approve_time`     datetime     COMMENT '审批时间',
  `update_by`        varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`      datetime     COMMENT '更新时间',
  PRIMARY KEY (`quotation_id`),
  UNIQUE KEY `uk_quotation_no` (`quotation_no`),
  KEY `idx_customer` (`customer_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售报价单';

-- 2.2 报价明细
DROP TABLE IF EXISTS `jn_quotation_line`;
CREATE TABLE `jn_quotation_line` (
  `line_id`      bigint(20) NOT NULL AUTO_INCREMENT COMMENT '行ID',
  `quotation_id` bigint(20) NOT NULL COMMENT '报价单ID',
  `material_id`  bigint(20) DEFAULT NULL COMMENT '物料ID',
  `material_code` varchar(100) DEFAULT '' COMMENT '物料编码',
  `material_name` varchar(200) DEFAULT '' COMMENT '物料名称',
  `spec`         varchar(500) DEFAULT '' COMMENT '规格',
  `quantity`     int(11)    DEFAULT 1 COMMENT '数量',
  `unit_price`   decimal(10,2) DEFAULT 0.00 COMMENT '单价',
  `amount`       decimal(12,2) DEFAULT 0.00 COMMENT '金额',
  `remark`       varchar(500) DEFAULT '' COMMENT '备注',
  `sort_order`   int(11)    DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`line_id`),
  KEY `idx_quotation` (`quotation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报价明细行';

-- 2.3 销售订单
DROP TABLE IF EXISTS `jn_sales_order`;
CREATE TABLE `jn_sales_order` (
  `order_id`       bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no`       varchar(50)  NOT NULL COMMENT '订单号',
  `quotation_id`   bigint(20)   DEFAULT NULL COMMENT '关联报价单',
  `customer_id`    bigint(20)   NOT NULL COMMENT '客户ID',
  `customer_name`  varchar(200) DEFAULT '' COMMENT '客户名称',
  `salesman`       varchar(64)  DEFAULT '' COMMENT '业务员',
  `order_date`     date         COMMENT '下单日期',
  `delivery_date`  date         COMMENT '要求交期',
  `total_amount`   decimal(12,2) DEFAULT 0.00 COMMENT '订单总额',
  `paid_amount`    decimal(12,2) DEFAULT 0.00 COMMENT '已收金额',
  `payment_terms`  varchar(100) DEFAULT '' COMMENT '付款方式',
  `delivery_addr`  varchar(500) DEFAULT '' COMMENT '发货地址',
  `status`         varchar(20)  DEFAULT 'PENDING' COMMENT '状态(PENDING待审批/CONFIRMED已确认/PRODUCTION生产中/SHIPPED已发货/COMPLETED已完成/CANCELLED已取消)',
  `del_flag`       char(1)      DEFAULT '0' COMMENT '删除标志',
  `remark`         varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`      varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`    datetime     COMMENT '创建时间',
  `update_by`      varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`    datetime     COMMENT '更新时间',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_customer` (`customer_id`),
  KEY `idx_status` (`status`),
  KEY `idx_delivery_date` (`delivery_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单';

-- 2.4 销售订单明细
DROP TABLE IF EXISTS `jn_order_line`;
CREATE TABLE `jn_order_line` (
  `line_id`      bigint(20) NOT NULL AUTO_INCREMENT COMMENT '行ID',
  `order_id`     bigint(20) NOT NULL COMMENT '订单ID',
  `material_id`  bigint(20) DEFAULT NULL COMMENT '物料ID',
  `material_code` varchar(100) DEFAULT '' COMMENT '物料编码',
  `material_name` varchar(200) DEFAULT '' COMMENT '物料名称',
  `spec`         varchar(500) DEFAULT '' COMMENT '规格',
  `quantity`     int(11)    DEFAULT 1 COMMENT '数量',
  `shipped_qty`  int(11)    DEFAULT 0 COMMENT '已发货数量',
  `unit_price`   decimal(10,2) DEFAULT 0.00 COMMENT '单价',
  `amount`       decimal(12,2) DEFAULT 0.00 COMMENT '金额',
  `remark`       varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`line_id`),
  KEY `idx_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细行';

-- 2.5 发货通知单
DROP TABLE IF EXISTS `jn_delivery_note`;
CREATE TABLE `jn_delivery_note` (
  `delivery_id`    bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '发货ID',
  `delivery_no`    varchar(50)  NOT NULL COMMENT '发货单号',
  `order_id`       bigint(20)   NOT NULL COMMENT '销售订单ID',
  `customer_id`    bigint(20)   NOT NULL COMMENT '客户ID',
  `customer_name`  varchar(200) DEFAULT '' COMMENT '客户名称',
  `delivery_date`  date         COMMENT '发货日期',
  `logistics_company` varchar(100) DEFAULT '' COMMENT '物流公司',
  `logistics_no`   varchar(100) DEFAULT '' COMMENT '物流单号',
  `status`         char(1)      DEFAULT '0' COMMENT '状态(0待发货 1已发货 2已签收)',
  `del_flag`       char(1)      DEFAULT '0' COMMENT '删除标志',
  `remark`         varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`      varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`    datetime     COMMENT '创建时间',
  `update_by`      varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`    datetime     COMMENT '更新时间',
  PRIMARY KEY (`delivery_id`),
  UNIQUE KEY `uk_delivery_no` (`delivery_no`),
  KEY `idx_order` (`order_id`),
  KEY `idx_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发货通知单';

-- 2.6 销售退货
DROP TABLE IF EXISTS `jn_sales_return`;
CREATE TABLE `jn_sales_return` (
  `return_id`    bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '退货ID',
  `return_no`    varchar(50)  NOT NULL COMMENT '退货单号',
  `order_id`     bigint(20)   NOT NULL COMMENT '原订单ID',
  `customer_id`  bigint(20)   NOT NULL COMMENT '客户ID',
  `return_date`  date         COMMENT '退货日期',
  `total_amount` decimal(12,2) DEFAULT 0.00 COMMENT '退款金额',
  `reason`       varchar(500) DEFAULT '' COMMENT '退货原因',
  `status`       char(1)      DEFAULT '0' COMMENT '状态(0待处理 1已入库 2已退款 3已关闭)',
  `del_flag`     char(1)      DEFAULT '0' COMMENT '删除标志',
  `create_by`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`  datetime     COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`  datetime     COMMENT '更新时间',
  PRIMARY KEY (`return_id`),
  UNIQUE KEY `uk_return_no` (`return_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售退货单';

-- ============================================================
-- 三、采购域
-- ============================================================

-- 3.1 采购需求单
DROP TABLE IF EXISTS `jn_purchase_demand`;
CREATE TABLE `jn_purchase_demand` (
  `demand_id`     bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '需求ID',
  `demand_no`     varchar(50)  NOT NULL COMMENT '需求编号',
  `material_id`   bigint(20)   DEFAULT NULL COMMENT '物料ID',
  `material_name` varchar(200) DEFAULT '' COMMENT '物料名称',
  `quantity`      int(11)      DEFAULT 0 COMMENT '需求量',
  `demand_date`   date         COMMENT '需求日期',
  `source_type`   varchar(50)  DEFAULT 'MANUAL' COMMENT '来源类型(MANUAL手工/MRP运算/SAFETY安全库存/ORDER订单)',
  `source_ref`    varchar(100) DEFAULT '' COMMENT '来源单号(如WO-xxx/SO-xxx)',
  `status`        char(1)      DEFAULT '0' COMMENT '状态(0待处理 1已转采购 2已关闭)',
  `remark`        varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`     varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`   datetime     COMMENT '创建时间',
  `update_by`     varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`   datetime     COMMENT '更新时间',
  PRIMARY KEY (`demand_id`),
  UNIQUE KEY `uk_demand_no` (`demand_no`),
  KEY `idx_status` (`status`),
  KEY `idx_material` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购需求单';

-- 3.2 采购订单
DROP TABLE IF EXISTS `jn_purchase_order`;
CREATE TABLE `jn_purchase_order` (
  `po_id`         bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '采购单ID',
  `po_no`         varchar(50)  NOT NULL COMMENT '采购单号',
  `supplier_id`   bigint(20)   NOT NULL COMMENT '供应商ID',
  `supplier_name` varchar(200) DEFAULT '' COMMENT '供应商名称',
  `buyer`         varchar(64)  DEFAULT '' COMMENT '采购员',
  `order_date`    date         COMMENT '下单日期',
  `delivery_date` date         COMMENT '要求交期',
  `total_amount`  decimal(12,2) DEFAULT 0.00 COMMENT '订单总额',
  `paid_amount`   decimal(12,2) DEFAULT 0.00 COMMENT '已付金额',
  `payment_terms` varchar(100) DEFAULT '' COMMENT '付款方式',
  `status`        varchar(20)  DEFAULT 'DRAFT' COMMENT '状态(DRAFT草稿/ORDERED已下单/PARTIAL部分收货/RECEIVED已收货/COMPLETED已完成/CANCELLED已取消)',
  `del_flag`      char(1)      DEFAULT '0' COMMENT '删除标志',
  `remark`        varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`     varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`   datetime     COMMENT '创建时间',
  `approve_by`    varchar(64)  DEFAULT '' COMMENT '审批人',
  `approve_time`  datetime     COMMENT '审批时间',
  `update_by`     varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`   datetime     COMMENT '更新时间',
  PRIMARY KEY (`po_id`),
  UNIQUE KEY `uk_po_no` (`po_no`),
  KEY `idx_supplier` (`supplier_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单';

-- 3.3 采购订单明细
DROP TABLE IF EXISTS `jn_po_line`;
CREATE TABLE `jn_po_line` (
  `line_id`      bigint(20) NOT NULL AUTO_INCREMENT COMMENT '行ID',
  `po_id`        bigint(20) NOT NULL COMMENT '采购单ID',
  `material_id`  bigint(20) DEFAULT NULL COMMENT '物料ID',
  `material_code` varchar(100) DEFAULT '' COMMENT '物料编码',
  `material_name` varchar(200) DEFAULT '' COMMENT '物料名称',
  `spec`         varchar(500) DEFAULT '' COMMENT '规格',
  `quantity`     int(11)    DEFAULT 1 COMMENT '数量',
  `received_qty` int(11)    DEFAULT 0 COMMENT '已收货数量',
  `unit_price`   decimal(10,2) DEFAULT 0.00 COMMENT '单价',
  `amount`       decimal(12,2) DEFAULT 0.00 COMMENT '金额',
  `remark`       varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`line_id`),
  KEY `idx_po` (`po_id`),
  KEY `idx_material` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单明细';

-- 3.4 采购收货单(含质检)
DROP TABLE IF EXISTS `jn_purchase_receive`;
CREATE TABLE `jn_purchase_receive` (
  `receive_id`   bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '收货ID',
  `receive_no`   varchar(50)  NOT NULL COMMENT '收货单号',
  `po_id`        bigint(20)   NOT NULL COMMENT '采购订单ID',
  `supplier_id`  bigint(20)   NOT NULL COMMENT '供应商ID',
  `receive_date` date         COMMENT '收货日期',
  `total_qty`    int(11)      DEFAULT 0 COMMENT '收货总数',
  `ok_qty`       int(11)      DEFAULT 0 COMMENT '合格数',
  `bad_qty`      int(11)      DEFAULT 0 COMMENT '不良数',
  `qc_result`    char(1)      DEFAULT '0' COMMENT '质检结果(0待检 1合格 2让步接收 3退货)',
  `status`       char(1)      DEFAULT '0' COMMENT '状态(0待入库 1已入库)',
  `remark`       varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`  datetime     COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`  datetime     COMMENT '更新时间',
  PRIMARY KEY (`receive_id`),
  UNIQUE KEY `uk_receive_no` (`receive_no`),
  KEY `idx_po` (`po_id`),
  KEY `idx_qc_result` (`qc_result`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购收货单(含质检)';

-- 3.5 采购退货
DROP TABLE IF EXISTS `jn_purchase_return`;
CREATE TABLE `jn_purchase_return` (
  `return_id`    bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '退货ID',
  `return_no`    varchar(50)  NOT NULL COMMENT '退货单号',
  `po_id`        bigint(20)   NOT NULL COMMENT '原采购单ID',
  `supplier_id`  bigint(20)   NOT NULL COMMENT '供应商ID',
  `material_id`  bigint(20)   DEFAULT NULL COMMENT '物料ID',
  `quantity`     int(11)      DEFAULT 0 COMMENT '退货数量',
  `return_date`  date         COMMENT '退货日期',
  `reason`       varchar(500) DEFAULT '' COMMENT '退货原因',
  `status`       char(1)      DEFAULT '0' COMMENT '状态(0待处理 1已出库 2已退款)',
  `remark`       varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`  datetime     COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`  datetime     COMMENT '更新时间',
  PRIMARY KEY (`return_id`),
  UNIQUE KEY `uk_return_no` (`return_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购退货单';

-- ============================================================
-- 四、仓库域
-- ============================================================

-- 4.1 仓库
DROP TABLE IF EXISTS `jn_warehouse`;
CREATE TABLE `jn_warehouse` (
  `wh_id`       bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '仓库ID',
  `wh_code`     varchar(50)  NOT NULL COMMENT '仓库编码',
  `wh_name`     varchar(100) NOT NULL COMMENT '仓库名称',
  `wh_type`     char(1)      DEFAULT '0' COMMENT '类型(0原料仓 1半成品仓 2成品仓 3报废仓)',
  `status`      char(1)      DEFAULT '0' COMMENT '状态(0启用 1停用)',
  `remark`      varchar(500) DEFAULT '' COMMENT '备注',
  `create_time` datetime     COMMENT '创建时间',
  `update_time` datetime     COMMENT '更新时间',
  PRIMARY KEY (`wh_id`),
  UNIQUE KEY `uk_wh_code` (`wh_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库';

INSERT INTO `jn_warehouse` VALUES (1,'WH-RAW','原料仓','0','0','钢板/电机等原材料',NOW(),NULL);
INSERT INTO `jn_warehouse` VALUES (2,'WH-SEMI','半成品仓','1','0','叶轮/蜗壳等半成品',NOW(),NULL);
INSERT INTO `jn_warehouse` VALUES (3,'WH-FIN','成品仓','2','0','风机成品',NOW(),NULL);
INSERT INTO `jn_warehouse` VALUES (4,'WH-SCRAP','报废仓','3','0','不良品',NOW(),NULL);

-- 4.2 库存实时表
DROP TABLE IF EXISTS `jn_inventory`;
CREATE TABLE `jn_inventory` (
  `inventory_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '库存ID',
  `material_id`  bigint(20) NOT NULL COMMENT '物料ID',
  `material_code` varchar(100) DEFAULT '' COMMENT '物料编码',
  `material_name` varchar(200) DEFAULT '' COMMENT '物料名称',
  `spec`         varchar(500) DEFAULT '' COMMENT '规格',
  `wh_id`        bigint(20) NOT NULL COMMENT '仓库ID',
  `batch_no`     varchar(100) DEFAULT '' COMMENT '批次号',
  `quantity`     int(11)    DEFAULT 0 COMMENT '当前库存量',
  `locked_qty`   int(11)    DEFAULT 0 COMMENT '锁定数量(已分配未出库)',
  `available_qty` int(11)   DEFAULT 0 COMMENT '可用数量',
  `safety_stock` int(11)    DEFAULT 0 COMMENT '安全库存',
  `status`       char(1)    DEFAULT '0' COMMENT '状态(0正常 1低于安全库存 2库存不足)',
  `update_time`  datetime   COMMENT '最后更新时间',
  PRIMARY KEY (`inventory_id`),
  UNIQUE KEY `uk_mat_wh_batch` (`material_id`,`wh_id`,`batch_no`),
  KEY `idx_material` (`material_id`),
  KEY `idx_wh` (`wh_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存实时表';

-- 4.3 库存流水日志
DROP TABLE IF EXISTS `jn_inventory_log`;
CREATE TABLE `jn_inventory_log` (
  `log_id`       bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `material_id`  bigint(20)   NOT NULL COMMENT '物料ID',
  `material_code` varchar(100) DEFAULT '' COMMENT '物料编码',
  `wh_id`        bigint(20)   NOT NULL COMMENT '仓库ID',
  `change_type`  varchar(50)  NOT NULL COMMENT '变动类型(PURCHASE_IN采购入库/SALE_OUT销售出库/PRODUCTION_IN生产入库/ISSUE_OUT领料出库/TRANSFER_IN调拨入库/TRANSFER_OUT调拨出库/CHECK_IN盘点入库/CHECK_OUT盘点出库/RETURN_IN退货入库/RETURN_OUT退货出库/INIT_INITIAL初始入库)',
  `change_qty`   int(11)      NOT NULL COMMENT '变动数量(正数增加/负数减少)',
  `before_qty`   int(11)      DEFAULT 0 COMMENT '变动前数量',
  `after_qty`    int(11)      DEFAULT 0 COMMENT '变动后数量',
  `ref_no`       varchar(100) DEFAULT '' COMMENT '关联单号',
  `ref_type`     varchar(50)  DEFAULT '' COMMENT '关联单据类型',
  `remark`       varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`    varchar(64)  DEFAULT '' COMMENT '操作人',
  `create_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_material` (`material_id`),
  KEY `idx_wh` (`wh_id`),
  KEY `idx_change_type` (`change_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水日志(不可删除/修改)';

-- 4.4 库存盘点
DROP TABLE IF EXISTS `jn_inventory_check`;
CREATE TABLE `jn_inventory_check` (
  `check_id`     bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '盘点ID',
  `check_no`     varchar(50)  NOT NULL COMMENT '盘点单号',
  `wh_id`        bigint(20)   NOT NULL COMMENT '仓库ID',
  `check_date`   date         COMMENT '盘点日期',
  `status`       char(1)      DEFAULT '0' COMMENT '状态(0盘点中 1已完成 2已审核)',
  `diff_count`   int(11)      DEFAULT 0 COMMENT '差异项数',
  `remark`       varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`    varchar(64)  DEFAULT '' COMMENT '盘点人',
  `create_time`  datetime     COMMENT '创建时间',
  `approve_by`   varchar(64)  DEFAULT '' COMMENT '审核人',
  `approve_time` datetime     COMMENT '审核时间',
  PRIMARY KEY (`check_id`),
  UNIQUE KEY `uk_check_no` (`check_no`),
  KEY `idx_wh_date` (`wh_id`,`check_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存盘点';

-- 4.5 盘点明细
DROP TABLE IF EXISTS `jn_check_line`;
CREATE TABLE `jn_check_line` (
  `line_id`       bigint(20) NOT NULL AUTO_INCREMENT COMMENT '行ID',
  `check_id`      bigint(20) NOT NULL COMMENT '盘点单ID',
  `material_id`   bigint(20) NOT NULL COMMENT '物料ID',
  `book_qty`      int(11)    DEFAULT 0 COMMENT '账面数量',
  `actual_qty`    int(11)    DEFAULT 0 COMMENT '实盘数量',
  `diff_qty`      int(11)    DEFAULT 0 COMMENT '差异数量',
  `unit_price`    decimal(10,2) DEFAULT 0.00 COMMENT '单价',
  `diff_amount`   decimal(12,2) DEFAULT 0.00 COMMENT '差异金额',
  `remark`        varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`line_id`),
  KEY `idx_check` (`check_id`),
  KEY `idx_material` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点明细';

-- ============================================================
-- 五、生产域（核心）
-- ============================================================

-- 5.1 BOM主表 ★风机行业核心★
DROP TABLE IF EXISTS `jn_bom`;
CREATE TABLE `jn_bom` (
  `bom_id`        bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'BOM ID',
  `bom_code`      varchar(50)  NOT NULL COMMENT 'BOM编号',
  `bom_name`      varchar(200) NOT NULL COMMENT 'BOM名称',
  `product_id`    bigint(20)   NOT NULL COMMENT '成品物料ID',
  `product_code`  varchar(100) DEFAULT '' COMMENT '成品编码',
  `product_name`  varchar(200) DEFAULT '' COMMENT '成品名称',
  `parent_bom_id` bigint(20)   DEFAULT NULL COMMENT '父BOM(子装配件)',
  `version`       varchar(20)  DEFAULT 'v1.0' COMMENT '版本号',
  `status`        varchar(20)  DEFAULT 'DRAFT' COMMENT '状态(DRAFT草稿/APPROVED已审核/EFFECTIVE已生效/DEPRECATED已作废)',
  `effective_date` date        COMMENT '生效日期',
  `expire_date`   date         COMMENT '失效日期',
  `total_cost`    decimal(12,2) DEFAULT 0.00 COMMENT '总成本(卷积计算)',
  `del_flag`      char(1)      DEFAULT '0' COMMENT '删除标志',
  `remark`        varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`     varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`   datetime     COMMENT '创建时间',
  `approve_by`    varchar(64)  DEFAULT '' COMMENT '审核人',
  `approve_time`  datetime     COMMENT '审核时间',
  `update_by`     varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`   datetime     COMMENT '更新时间',
  PRIMARY KEY (`bom_id`),
  UNIQUE KEY `uk_bom_code` (`bom_code`),
  KEY `idx_product` (`product_id`),
  KEY `idx_version` (`version`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='BOM主表(多层级/版本)';

-- 5.2 BOM明细
DROP TABLE IF EXISTS `jn_bom_line`;
CREATE TABLE `jn_bom_line` (
  `line_id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '行ID',
  `bom_id`           bigint(20) NOT NULL COMMENT 'BOM ID',
  `parent_line_id`   bigint(20) DEFAULT NULL COMMENT '父行ID(子BOM关联)',
  `material_id`      bigint(20) NOT NULL COMMENT '物料ID',
  `material_code`    varchar(100) DEFAULT '' COMMENT '物料编码',
  `material_name`    varchar(200) DEFAULT '' COMMENT '物料名称',
  `spec`             varchar(500) DEFAULT '' COMMENT '规格型号',
  `quantity`         decimal(10,4) DEFAULT 1.0000 COMMENT '用量',
  `unit`             varchar(20)  DEFAULT '' COMMENT '单位',
  `position_no`      varchar(50)  DEFAULT '' COMMENT '位号(装配位置)',
  `scrap_rate`       decimal(5,2) DEFAULT 0.00 COMMENT '损耗率(%)',
  `is_optional`      char(1)      DEFAULT 'N' COMMENT '是否可选配(Y/N)',
  `optional_group`   varchar(100) DEFAULT '' COMMENT '选配组名(如电机组/叶轮材质组)',
  `optional_rule`    varchar(500) DEFAULT '' COMMENT '选配条件规则JSON',
  `default_selected` char(1)      DEFAULT 'N' COMMENT '是否为默认选配(Y/N)',
  `alternative_id`   bigint(20)   DEFAULT NULL COMMENT '替代物料ID',
  `effective_date`   date         COMMENT '生效日期',
  `expire_date`      date         COMMENT '失效日期',
  `remark`           varchar(500) DEFAULT '' COMMENT '备注',
  `sort_order`       int(11)      DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`line_id`),
  KEY `idx_bom` (`bom_id`),
  KEY `idx_material` (`material_id`),
  KEY `idx_optional_group` (`optional_group`),
  KEY `idx_optional` (`is_optional`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='BOM明细(含选配/替代/损耗)';

-- 5.3 BOM版本历史
DROP TABLE IF EXISTS `jn_bom_version`;
CREATE TABLE `jn_bom_version` (
  `version_id`   bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '版本ID',
  `bom_id`       bigint(20)   NOT NULL COMMENT 'BOM ID',
  `version`      varchar(20)  NOT NULL COMMENT '版本号',
  `change_log`   varchar(2000) DEFAULT '' COMMENT '变更说明',
  `bom_snapshot` longtext     COMMENT 'BOM快照(JSON)',
  `created_by`   varchar(64)  DEFAULT '' COMMENT '创建人',
  `created_time` datetime     COMMENT '创建时间',
  PRIMARY KEY (`version_id`),
  KEY `idx_bom` (`bom_id`),
  KEY `idx_version` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='BOM版本历史';

-- 5.4 生产工单
DROP TABLE IF EXISTS `jn_work_order`;
CREATE TABLE `jn_work_order` (
  `order_id`     bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '工单ID',
  `order_no`     varchar(50)  NOT NULL COMMENT '工单号',
  `product_id`   bigint(20)   NOT NULL COMMENT '产品物料ID',
  `product_code` varchar(100) DEFAULT '' COMMENT '产品编码',
  `product_name` varchar(200) DEFAULT '' COMMENT '产品名称',
  `bom_id`       bigint(20)   DEFAULT NULL COMMENT '关联BOM ID',
  `bom_version`  varchar(20)  DEFAULT '' COMMENT 'BOM版本',
  `quantity`     int(11)      DEFAULT 1 COMMENT '计划数量',
  `finished_qty` int(11)      DEFAULT 0 COMMENT '完成数量',
  `defect_qty`   int(11)      DEFAULT 0 COMMENT '不良数量',
  `sales_order_id` bigint(20) DEFAULT NULL COMMENT '关联销售订单',
  `customer_name`  varchar(200) DEFAULT '' COMMENT '客户名称',
  `priority`     char(1)      DEFAULT '3' COMMENT '优先级(1紧急 2高 3中 4低)',
  `planned_start` datetime    COMMENT '计划开始时间',
  `planned_end`  datetime     COMMENT '计划完成时间',
  `actual_start` datetime     COMMENT '实际开始时间',
  `actual_end`   datetime     COMMENT '实际完成时间',
  `progress`     int(11)      DEFAULT 0 COMMENT '进度百分比(0-100)',
  `status`       varchar(20)  DEFAULT 'PENDING' COMMENT '状态(PENDING待排程/SCHEDULED已排程/IN_PROGRESS生产中/COMPLETED已完成/CLOSED已关闭/ON_HOLD暂停)',
  `del_flag`     char(1)      DEFAULT '0' COMMENT '删除标志',
  `remark`       varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`  datetime     COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`  datetime     COMMENT '更新时间',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_product` (`product_id`),
  KEY `idx_sales_order` (`sales_order_id`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`),
  KEY `idx_planned_start` (`planned_start`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产工单';

-- 5.5 工艺路线
DROP TABLE IF EXISTS `jn_process_route`;
CREATE TABLE `jn_process_route` (
  `route_id`     bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '工艺路线ID',
  `route_name`   varchar(200) NOT NULL COMMENT '路线名称',
  `product_id`   bigint(20)   DEFAULT NULL COMMENT '适用产品',
  `status`       char(1)      DEFAULT '0' COMMENT '状态(0启用 1停用)',
  `remark`       varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`  datetime     COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`  datetime     COMMENT '更新时间',
  PRIMARY KEY (`route_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工艺路线';

-- 5.6 工艺路线工序 ★风机工序★
DROP TABLE IF EXISTS `jn_process_operation`;
CREATE TABLE `jn_process_operation` (
  `operation_id`  bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '工序ID',
  `route_id`      bigint(20)   NOT NULL COMMENT '工艺路线ID',
  `seq_no`        int(11)      NOT NULL COMMENT '工序序号',
  `operation_name` varchar(100) NOT NULL COMMENT '工序名称',
  `work_center`   varchar(100) DEFAULT '' COMMENT '工作中心',
  `standard_hours` decimal(8,2) DEFAULT 0.00 COMMENT '标准工时(h)',
  `setup_hours`   decimal(8,2) DEFAULT 0.00 COMMENT '准备工时(h)',
  `is_qc_point`   char(1)      DEFAULT 'N' COMMENT '是否必检工位(Y/N)',
  `description`   varchar(500) DEFAULT '' COMMENT '工序说明',
  `sort_order`    int(11)      DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`operation_id`),
  KEY `idx_route` (`route_id`),
  KEY `idx_seq` (`route_id`,`seq_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工艺路线工序';

-- 风机典型工序数据
INSERT INTO `jn_process_operation` VALUES (1,1,1,'下料','下料区',4.5,0.5,'N','钢板切割/激光下料',1);
INSERT INTO `jn_process_operation` VALUES (2,1,2,'焊接','焊接区',8.0,1.0,'N','蜗壳/底座焊接',2);
INSERT INTO `jn_process_operation` VALUES (3,1,3,'动平衡','平衡机房',3.0,0.5,'Y','叶轮G2.5级动平衡校正',3);
INSERT INTO `jn_process_operation` VALUES (4,1,4,'装配','装配线',6.0,1.0,'N','叶轮/蜗壳/电机/底座总装',4);
INSERT INTO `jn_process_operation` VALUES (5,1,5,'试机','测试台',2.0,0.5,'Y','整机试运转/风量风压测试',5);
INSERT INTO `jn_process_operation` VALUES (6,1,6,'喷涂','喷涂房',3.0,0.5,'N','表面喷涂/干燥',6);
INSERT INTO `jn_process_operation` VALUES (7,1,7,'包装','包装区',1.5,0.5,'N','装箱/打包/贴标',7);

-- 5.7 工序报工
DROP TABLE IF EXISTS `jn_work_report`;
CREATE TABLE `jn_work_report` (
  `report_id`    bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '报工ID',
  `work_order_id` bigint(20)  NOT NULL COMMENT '工单ID',
  `operation_id` bigint(20)   DEFAULT NULL COMMENT '工序ID',
  `operation_name` varchar(100) DEFAULT '' COMMENT '工序名称',
  `worker`       varchar(64)  DEFAULT '' COMMENT '操作工',
  `work_date`    date         COMMENT '工作日期',
  `start_time`   datetime     COMMENT '开始时间',
  `end_time`     datetime     COMMENT '结束时间',
  `output_qty`   int(11)      DEFAULT 0 COMMENT '产出数量',
  `defect_qty`   int(11)      DEFAULT 0 COMMENT '不良数量',
  `hours`        decimal(6,2) DEFAULT 0.00 COMMENT '实际工时(h)',
  `status`       varchar(20)  DEFAULT 'PENDING' COMMENT '状态(PENDING待开始/IN_PROGRESS进行中/COMPLETED已完成)',
  `remark`       varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`    varchar(64)  DEFAULT '' COMMENT '报工录入人',
  `create_time`  datetime     COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`  datetime     COMMENT '更新时间',
  PRIMARY KEY (`report_id`),
  KEY `idx_work_order` (`work_order_id`),
  KEY `idx_operation` (`operation_id`),
  KEY `idx_worker` (`worker`),
  KEY `idx_work_date` (`work_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工序报工记录';

-- 5.8 工单物料需求(从BOM展开)
DROP TABLE IF EXISTS `jn_work_order_material`;
CREATE TABLE `jn_work_order_material` (
  `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `work_order_id` bigint(20) NOT NULL COMMENT '工单ID',
  `material_id` bigint(20) NOT NULL COMMENT '物料ID',
  `material_code` varchar(100) DEFAULT '' COMMENT '物料编码',
  `material_name` varchar(200) DEFAULT '' COMMENT '物料名称',
  `spec`        varchar(500) DEFAULT '' COMMENT '规格',
  `required_qty` decimal(10,2) DEFAULT 0.00 COMMENT '需求数量',
  `issued_qty`  decimal(10,2) DEFAULT 0.00 COMMENT '已领用数量',
  `unit`        varchar(20)  DEFAULT '' COMMENT '单位',
  `stock_qty`   decimal(10,2) DEFAULT 0.00 COMMENT '当前库存',
  `shortage_qty` decimal(10,2) DEFAULT 0.00 COMMENT '缺料数量',
  `remark`      varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_work_order` (`work_order_id`),
  KEY `idx_material` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单物料需求(从BOM展开)';

-- 5.9 委外加工
DROP TABLE IF EXISTS `jn_outsource_order`;
CREATE TABLE `jn_outsource_order` (
  `os_id`         bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '委外ID',
  `os_no`         varchar(50)  NOT NULL COMMENT '委外单号',
  `supplier_id`   bigint(20)   NOT NULL COMMENT '供应商ID',
  `material_id`   bigint(20)   NOT NULL COMMENT '委外物料',
  `quantity`      int(11)      DEFAULT 0 COMMENT '数量',
  `process_fee`   decimal(10,2) DEFAULT 0.00 COMMENT '加工单价',
  `total_fee`     decimal(12,2) DEFAULT 0.00 COMMENT '加工总价',
  `delivery_date` date         COMMENT '要求交期',
  `status`        char(1)      DEFAULT '0' COMMENT '状态(0待发料 1已发料 2已收回 3已结算)',
  `remark`        varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`     varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`   datetime     COMMENT '创建时间',
  `update_by`     varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`   datetime     COMMENT '更新时间',
  PRIMARY KEY (`os_id`),
  UNIQUE KEY `uk_os_no` (`os_no`),
  KEY `idx_supplier` (`supplier_id`),
  KEY `idx_material` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='委外加工订单';

-- ============================================================
-- 六、质量域
-- ============================================================

-- 6.1 质量检验
DROP TABLE IF EXISTS `jn_quality_check`;
CREATE TABLE `jn_quality_check` (
  `qc_id`        bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '检验ID',
  `qc_no`        varchar(50)  NOT NULL COMMENT '检验单号',
  `qc_type`      varchar(20)  NOT NULL COMMENT '检验类型(IQC来料检/IPQC过程检/OQC成品检)',
  `source_type`  varchar(50)  DEFAULT '' COMMENT '来源类型(PURCHASE采购/PRODUCTION生产)',
  `source_ref`   varchar(100) DEFAULT '' COMMENT '来源单号',
  `material_id`  bigint(20)   DEFAULT NULL COMMENT '物料ID',
  `material_code` varchar(100) DEFAULT '' COMMENT '物料编码',
  `material_name` varchar(200) DEFAULT '' COMMENT '物料名称',
  `spec`         varchar(500) DEFAULT '' COMMENT '规格',
  `batch_no`     varchar(100) DEFAULT '' COMMENT '批次号',
  `total_qty`    int(11)      DEFAULT 0 COMMENT '检验数量',
  `ok_qty`       int(11)      DEFAULT 0 COMMENT '合格数量',
  `bad_qty`      int(11)      DEFAULT 0 COMMENT '不良数量',
  `result`       varchar(20)  DEFAULT 'PENDING' COMMENT '结果(PENDING待检/PASS合格/CONDITIONAL让步接收/FAIL不合格/RETURN退货)',
  `inspector`    varchar(64)  DEFAULT '' COMMENT '检验员',
  `check_date`   date         COMMENT '检验日期',
  `remark`       varchar(2000) DEFAULT '' COMMENT '缺陷描述',
  `create_by`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`  datetime     COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`  datetime     COMMENT '更新时间',
  PRIMARY KEY (`qc_id`),
  UNIQUE KEY `uk_qc_no` (`qc_no`),
  KEY `idx_type` (`qc_type`),
  KEY `idx_material` (`material_id`),
  KEY `idx_result` (`result`),
  KEY `idx_check_date` (`check_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量检验';

-- 6.2 检验项目标准
DROP TABLE IF EXISTS `jn_qc_standard`;
CREATE TABLE `jn_qc_standard` (
  `std_id`       bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '标准ID',
  `material_id`  bigint(20)   DEFAULT NULL COMMENT '物料ID(为空则通用)',
  `qc_type`      varchar(20)  NOT NULL COMMENT '检验类型',
  `item_name`    varchar(100) NOT NULL COMMENT '检验项目',
  `spec_value`   varchar(200) DEFAULT '' COMMENT '规格值',
  `method`       varchar(500) DEFAULT '' COMMENT '检验方法',
  `tool`         varchar(100) DEFAULT '' COMMENT '检测工具',
  `is_required`  char(1)      DEFAULT 'Y' COMMENT '是否必检',
  `sort_order`   int(11)      DEFAULT 0 COMMENT '排序',
  `status`       char(1)      DEFAULT '0' COMMENT '状态',
  PRIMARY KEY (`std_id`),
  KEY `idx_material` (`material_id`),
  KEY `idx_type` (`qc_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检验项目标准';

-- ============================================================
-- 七、财务域
-- ============================================================

-- 7.1 应收账款
DROP TABLE IF EXISTS `jn_receivable`;
CREATE TABLE `jn_receivable` (
  `recv_id`      bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '应收ID',
  `customer_id`  bigint(20)   NOT NULL COMMENT '客户ID',
  `customer_name` varchar(200) DEFAULT '' COMMENT '客户名称',
  `source_type`  varchar(50)  DEFAULT 'SALES_ORDER' COMMENT '来源类型',
  `source_ref`   varchar(100) DEFAULT '' COMMENT '来源单号(销售订单号)',
  `total_amount` decimal(12,2) DEFAULT 0.00 COMMENT '应收总额',
  `received_amount` decimal(12,2) DEFAULT 0.00 COMMENT '已收金额',
  `balance`      decimal(12,2) DEFAULT 0.00 COMMENT '未收余额',
  `due_date`     date         COMMENT '到期日期',
  `aging_days`   int(11)      DEFAULT 0 COMMENT '账龄(天)',
  `status`       varchar(20)  DEFAULT 'UNPAID' COMMENT '状态(UNPAID未回款/PARTIAL部分回款/SETTLED已结清)',
  `remark`       varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`  datetime     COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`  datetime     COMMENT '更新时间',
  PRIMARY KEY (`recv_id`),
  KEY `idx_customer` (`customer_id`),
  KEY `idx_status` (`status`),
  KEY `idx_due_date` (`due_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应收账款';

-- 7.2 收款记录
DROP TABLE IF EXISTS `jn_receipt`;
CREATE TABLE `jn_receipt` (
  `receipt_id`   bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '收款ID',
  `receipt_no`   varchar(50)  NOT NULL COMMENT '收款单号',
  `customer_id`  bigint(20)   NOT NULL COMMENT '客户ID',
  `recv_id`      bigint(20)   DEFAULT NULL COMMENT '关联应收ID',
  `amount`       decimal(12,2) DEFAULT 0.00 COMMENT '收款金额',
  `receipt_date` date         COMMENT '收款日期',
  `payment_method` varchar(50) DEFAULT '' COMMENT '收款方式(转账/现金/支票/承兑)',
  `remark`       varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`  datetime     COMMENT '创建时间',
  PRIMARY KEY (`receipt_id`),
  UNIQUE KEY `uk_receipt_no` (`receipt_no`),
  KEY `idx_recv` (`recv_id`),
  KEY `idx_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收款记录';

-- 7.3 应付账款
DROP TABLE IF EXISTS `jn_payable`;
CREATE TABLE `jn_payable` (
  `payable_id`   bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '应付ID',
  `supplier_id`  bigint(20)   NOT NULL COMMENT '供应商ID',
  `supplier_name` varchar(200) DEFAULT '' COMMENT '供应商名称',
  `source_type`  varchar(50)  DEFAULT 'PURCHASE_ORDER' COMMENT '来源类型',
  `source_ref`   varchar(100) DEFAULT '' COMMENT '来源单号(采购订单号)',
  `total_amount` decimal(12,2) DEFAULT 0.00 COMMENT '应付总额',
  `paid_amount`  decimal(12,2) DEFAULT 0.00 COMMENT '已付金额',
  `balance`      decimal(12,2) DEFAULT 0.00 COMMENT '未付余额',
  `due_date`     date         COMMENT '到期日期',
  `status`       varchar(20)  DEFAULT 'UNPAID' COMMENT '状态(UNPAID待付款/PARTIAL部分付款/SETTLED已结清)',
  `remark`       varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`  datetime     COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`  datetime     COMMENT '更新时间',
  PRIMARY KEY (`payable_id`),
  KEY `idx_supplier` (`supplier_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应付账款';

-- 7.4 付款记录
DROP TABLE IF EXISTS `jn_payment`;
CREATE TABLE `jn_payment` (
  `payment_id`   bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '付款ID',
  `payment_no`   varchar(50)  NOT NULL COMMENT '付款单号',
  `supplier_id`  bigint(20)   NOT NULL COMMENT '供应商ID',
  `payable_id`   bigint(20)   DEFAULT NULL COMMENT '关联应付ID',
  `amount`       decimal(12,2) DEFAULT 0.00 COMMENT '付款金额',
  `payment_date` date         COMMENT '付款日期',
  `payment_method` varchar(50) DEFAULT '' COMMENT '付款方式',
  `remark`       varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`  datetime     COMMENT '创建时间',
  PRIMARY KEY (`payment_id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_payable` (`payable_id`),
  KEY `idx_supplier` (`supplier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='付款记录';

-- 7.5 费用报销
DROP TABLE IF EXISTS `jn_expense`;
CREATE TABLE `jn_expense` (
  `expense_id`   bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '费用ID',
  `expense_no`   varchar(50)  NOT NULL COMMENT '费用单号',
  `category`     varchar(50)  DEFAULT '' COMMENT '费用类别(差旅/办公/运输/其他)',
  `amount`       decimal(10,2) DEFAULT 0.00 COMMENT '金额',
  `expense_date` date         COMMENT '发生日期',
  `applicant`    varchar(64)  DEFAULT '' COMMENT '申请人',
  `status`       char(1)      DEFAULT '0' COMMENT '状态(0待审批 1已通过 2已驳回 3已付款)',
  `remark`       varchar(500) DEFAULT '' COMMENT '备注',
  `create_by`    varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`  datetime     COMMENT '创建时间',
  `approve_by`   varchar(64)  DEFAULT '' COMMENT '审批人',
  `approve_time` datetime     COMMENT '审批时间',
  PRIMARY KEY (`expense_id`),
  UNIQUE KEY `uk_expense_no` (`expense_no`),
  KEY `idx_status` (`status`),
  KEY `idx_applicant` (`applicant`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用报销';

-- ============================================================
-- 八、系统配置扩展
-- ============================================================

-- 8.1 编号规则
DROP TABLE IF EXISTS `jn_sequence`;
CREATE TABLE `jn_sequence` (
  `seq_code`     varchar(50)  NOT NULL COMMENT '编码规则代码',
  `seq_name`     varchar(100) DEFAULT '' COMMENT '规则名称',
  `prefix`       varchar(20)  DEFAULT '' COMMENT '前缀',
  `date_format`  varchar(20)  DEFAULT 'yyyyMMdd' COMMENT '日期格式',
  `seq_length`   int(11)      DEFAULT 4 COMMENT '流水号长度',
  `current_no`   int(11)      DEFAULT 0 COMMENT '当前流水号',
  `reset_mode`   char(1)      DEFAULT 'D' COMMENT '重置方式(D每日/M每月/Y每年/N不重置)',
  PRIMARY KEY (`seq_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='编码规则';

INSERT INTO `jn_sequence` VALUES
('MAT','物料编码','FN-','yyyyMMdd',4,0,'N'),
('CUST','客户编码','C-','yyyy',4,24004,'Y'),
('SUPP','供应商编码','S-','yyyy',4,3,'Y'),
('SO','销售订单','SO-','yyyyMMdd',4,0,'D'),
('PO','采购订单','PO-','yyyyMMdd',4,0,'D'),
('WO','工单','WO-','yyyyMMdd',4,0,'D'),
('QC','质检单','QC-','yyyyMMdd',4,0,'D'),
('DN','发货单','DN-','yyyyMMdd',4,0,'D');

-- ============================================================
-- 索引创建 (若依系统表补充索引)
-- ============================================================
ALTER TABLE `sys_login_log` ADD INDEX `idx_user_name` (`user_name`);
ALTER TABLE `sys_login_log` ADD INDEX `idx_login_time` (`login_time`);
ALTER TABLE `sys_oper_log` ADD INDEX `idx_oper_time` (`oper_time`);
ALTER TABLE `sys_oper_log` ADD INDEX `idx_status` (`status`);
ALTER TABLE `sys_job` ADD INDEX `idx_status` (`status`);

-- ============================================================
-- 字典补充 (风机行业专用)
-- ============================================================
INSERT INTO `sys_dict_type` VALUES (4,'生产状态','production_status','0','admin',NOW(),'',NULL,'工单状态');
INSERT INTO `sys_dict_type` VALUES (5,'质检结果','qc_result','0','admin',NOW(),'',NULL,'检验结果');
INSERT INTO `sys_dict_type` VALUES (6,'入库类型','instock_type','0','admin',NOW(),'',NULL,'入库单据类型');
INSERT INTO `sys_dict_type` VALUES (7,'出库类型','outstock_type','0','admin',NOW(),'',NULL,'出库单据类型');
INSERT INTO `sys_dict_type` VALUES (8,'付款方式','payment_method','0','admin',NOW(),'',NULL,'');
INSERT INTO `sys_dict_type` VALUES (9,'费用类别','expense_category','0','admin',NOW(),'',NULL,'');
INSERT INTO `sys_dict_type` VALUES (10,'质检类型','qc_type','0','admin',NOW(),'',NULL,'IQC/IPQC/OQC');

INSERT INTO `sys_dict_data` VALUES (10,1,'待排程','PENDING','production_status','','','N','0','admin',NOW(),'',NULL,'');
INSERT INTO `sys_dict_data` VALUES (11,2,'生产中','IN_PROGRESS','production_status','','primary','N','0','admin',NOW(),'',NULL,'');
INSERT INTO `sys_dict_data` VALUES (12,3,'已完成','COMPLETED','production_status','','success','N','0','admin',NOW(),'',NULL,'');
INSERT INTO `sys_dict_data` VALUES (13,1,'合格','PASS','qc_result','','success','N','0','admin',NOW(),'',NULL,'');
INSERT INTO `sys_dict_data` VALUES (14,2,'让步接收','CONDITIONAL','qc_result','','warning','N','0','admin',NOW(),'',NULL,'');
INSERT INTO `sys_dict_data` VALUES (15,3,'不合格','FAIL','qc_result','','danger','N','0','admin',NOW(),'',NULL,'');
INSERT INTO `sys_dict_data` VALUES (16,1,'IQC来料检','IQC','qc_type','','','N','0','admin',NOW(),'',NULL,'');
INSERT INTO `sys_dict_data` VALUES (17,2,'IPQC过程检','IPQC','qc_type','','primary','N','0','admin',NOW(),'',NULL,'');
INSERT INTO `sys_dict_data` VALUES (18,3,'OQC成品检','OQC','qc_type','','success','N','0','admin',NOW(),'',NULL,'');

-- ============================================================
-- Sprint 02 完成标记
-- 共30张业务表 + 18条字典数据
-- ============================================================
