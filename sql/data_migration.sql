-- ============================================================
-- 精恩风机ERP - 数据清洗与迁移脚本 (Sprint 13)
-- 版本: v13.0.0
-- 日期: 2026-05-27
-- ============================================================

USE `jn_fan_erp`;

-- ============================================================
-- 步骤1: 创建迁移日志表
-- ============================================================
CREATE TABLE IF NOT EXISTS `jn_data_migration_log` (
    `log_id`        BIGINT AUTO_INCREMENT PRIMARY KEY,
    `batch_no`      VARCHAR(50) NOT NULL,
    `table_name`    VARCHAR(100) NOT NULL,
    `record_count`  INT DEFAULT 0,
    `success_count` INT DEFAULT 0,
    `error_count`   INT DEFAULT 0,
    `error_message` TEXT,
    `status`        VARCHAR(20) DEFAULT 'PENDING',
    `started_at`    DATETIME,
    `completed_at`  DATETIME,
    `created_by`    VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据迁移日志';

-- ============================================================
-- 步骤2: 存储过程 - 物料数据清洗
-- ============================================================
DELIMITER $$

DROP PROCEDURE IF EXISTS `clean_material_data`$$
CREATE PROCEDURE `clean_material_data`()
BEGIN
    DECLARE v_batch_no VARCHAR(50);
    DECLARE v_count INT DEFAULT 0;
    DECLARE v_success INT DEFAULT 0;
    DECLARE v_error INT DEFAULT 0;
    DECLARE v_error_msg TEXT DEFAULT '';

    SET v_batch_no = CONCAT('CLN-MAT-', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'));

    INSERT INTO `jn_data_migration_log` (batch_no, table_name, record_count, status, started_at, created_by)
    VALUES (v_batch_no, 'jn_material', 0, 'IN_PROGRESS', NOW(), 'SYSTEM');

    SET v_count = (SELECT COUNT(*) FROM `jn_material` WHERE `del_flag` = '0');

    -- 1. 删除重复物料编码，保留最新记录
    DELETE FROM `jn_material`
    WHERE `material_id` IN (
        SELECT `material_id` FROM (
            SELECT m1.`material_id`
            FROM `jn_material` m1
            INNER JOIN (
                SELECT `material_code`, MAX(`create_time`) AS max_time
                FROM `jn_material`
                WHERE `del_flag` = '0'
                GROUP BY `material_code`
                HAVING COUNT(*) > 1
            ) dup ON m1.`material_code` = dup.`material_code`
            WHERE m1.`create_time` < dup.max_time AND m1.`del_flag` = '0'
        ) tmp
    );

    -- 2. 设置NULL字段默认值
    UPDATE `jn_material`
    SET `is_configurable` = COALESCE(NULLIF(`is_configurable`, ''), 'N'),
        `version` = COALESCE(`version`, 1),
        `unit` = COALESCE(NULLIF(`unit`, ''), '个')
    WHERE `del_flag` = '0';

    -- 3. 验证单位不为空
    UPDATE `jn_material`
    SET `unit` = '个'
    WHERE `del_flag` = '0' AND (`unit` IS NULL OR `unit` = '');

    SET v_success = v_success + (SELECT ROW_COUNT());

    -- 4. 记录无效数据
    SELECT COUNT(*) INTO v_error FROM `jn_material`
    WHERE `del_flag` = '0' AND (`material_code` IS NULL OR `material_code` = '' OR `material_name` IS NULL OR `material_name` = '');

    IF v_error > 0 THEN
        SET v_error_msg = CONCAT('存在 ', v_error, ' 条物料数据缺少编码或名称');
    END IF;

    UPDATE `jn_data_migration_log`
    SET `record_count` = v_count,
        `success_count` = v_success,
        `error_count` = v_error,
        `error_message` = v_error_msg,
        `status` = IF(v_error = 0, 'COMPLETED', 'COMPLETED_WITH_ERRORS'),
        `completed_at` = NOW()
    WHERE `batch_no` = v_batch_no;

    SELECT v_batch_no AS batch_no, v_count AS total, v_success AS success, v_error AS error, v_error_msg AS error_message;
END$$

-- ============================================================
-- 存储过程 - 客户数据清洗
-- ============================================================
DROP PROCEDURE IF EXISTS `clean_customer_data`$$
CREATE PROCEDURE `clean_customer_data`()
BEGIN
    DECLARE v_batch_no VARCHAR(50);
    DECLARE v_count INT DEFAULT 0;
    DECLARE v_success INT DEFAULT 0;
    DECLARE v_error INT DEFAULT 0;
    DECLARE v_error_msg TEXT DEFAULT '';

    SET v_batch_no = CONCAT('CLN-CUST-', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'));

    INSERT INTO `jn_data_migration_log` (batch_no, table_name, record_count, status, started_at, created_by)
    VALUES (v_batch_no, 'jn_customer', 0, 'IN_PROGRESS', NOW(), 'SYSTEM');

    SET v_count = (SELECT COUNT(*) FROM `jn_customer` WHERE `del_flag` = '0');

    -- 1. 删除重复客户名称，保留最新记录
    DELETE FROM `jn_customer`
    WHERE `customer_id` IN (
        SELECT `customer_id` FROM (
            SELECT c1.`customer_id`
            FROM `jn_customer` c1
            INNER JOIN (
                SELECT `customer_name`, MAX(`create_time`) AS max_time
                FROM `jn_customer`
                WHERE `del_flag` = '0'
                GROUP BY `customer_name`
                HAVING COUNT(*) > 1
            ) dup ON c1.`customer_name` = dup.`customer_name`
            WHERE c1.`create_time` < dup.max_time AND c1.`del_flag` = '0'
        ) tmp
    );

    -- 2. 设置默认信用额度
    UPDATE `jn_customer`
    SET `credit_limit` = COALESCE(`credit_limit`, 0.00)
    WHERE `del_flag` = '0' AND (`credit_limit` IS NULL OR `credit_limit` = 0);

    -- 3. 标准化电话号码格式（去除空格、中划线统一）
    UPDATE `jn_customer`
    SET `phone` = TRIM(REPLACE(REPLACE(REPLACE(`phone`, '-', ''), ' ', ''), '　', ''))
    WHERE `del_flag` = '0' AND `phone` IS NOT NULL AND `phone` != '';

    SET v_success = v_success + (SELECT ROW_COUNT());

    SELECT COUNT(*) INTO v_error FROM `jn_customer`
    WHERE `del_flag` = '0' AND (`customer_code` IS NULL OR `customer_code` = '' OR `customer_name` IS NULL OR `customer_name` = '');

    IF v_error > 0 THEN
        SET v_error_msg = CONCAT('存在 ', v_error, ' 条客户数据缺少编号或名称');
    END IF;

    UPDATE `jn_data_migration_log`
    SET `record_count` = v_count,
        `success_count` = v_success,
        `error_count` = v_error,
        `error_message` = v_error_msg,
        `status` = IF(v_error = 0, 'COMPLETED', 'COMPLETED_WITH_ERRORS'),
        `completed_at` = NOW()
    WHERE `batch_no` = v_batch_no;

    SELECT v_batch_no AS batch_no, v_count AS total, v_success AS success, v_error AS error, v_error_msg AS error_message;
END$$

-- ============================================================
-- 存储过程 - 供应商数据清洗
-- ============================================================
DROP PROCEDURE IF EXISTS `clean_supplier_data`$$
CREATE PROCEDURE `clean_supplier_data`()
BEGIN
    DECLARE v_batch_no VARCHAR(50);
    DECLARE v_count INT DEFAULT 0;
    DECLARE v_success INT DEFAULT 0;
    DECLARE v_error INT DEFAULT 0;
    DECLARE v_error_msg TEXT DEFAULT '';

    SET v_batch_no = CONCAT('CLN-SUPP-', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'));

    INSERT INTO `jn_data_migration_log` (batch_no, table_name, record_count, status, started_at, created_by)
    VALUES (v_batch_no, 'jn_supplier', 0, 'IN_PROGRESS', NOW(), 'SYSTEM');

    SET v_count = (SELECT COUNT(*) FROM `jn_supplier` WHERE `del_flag` = '0');

    -- 1. 删除重复供应商编码，保留最新记录
    DELETE FROM `jn_supplier`
    WHERE `supplier_id` IN (
        SELECT `supplier_id` FROM (
            SELECT s1.`supplier_id`
            FROM `jn_supplier` s1
            INNER JOIN (
                SELECT `supplier_code`, MAX(`create_time`) AS max_time
                FROM `jn_supplier`
                WHERE `del_flag` = '0'
                GROUP BY `supplier_code`
                HAVING COUNT(*) > 1
            ) dup ON s1.`supplier_code` = dup.`supplier_code`
            WHERE s1.`create_time` < dup.max_time AND s1.`del_flag` = '0'
        ) tmp
    );

    -- 2. 设置默认评级
    UPDATE `jn_supplier`
    SET `rating` = COALESCE(`rating`, 3)
    WHERE `del_flag` = '0' AND (`rating` IS NULL OR `rating` = 0);

    SET v_success = v_success + (SELECT ROW_COUNT());

    SELECT COUNT(*) INTO v_error FROM `jn_supplier`
    WHERE `del_flag` = '0' AND (`supplier_code` IS NULL OR `supplier_code` = '' OR `supplier_name` IS NULL OR `supplier_name` = '');

    IF v_error > 0 THEN
        SET v_error_msg = CONCAT('存在 ', v_error, ' 条供应商数据缺少编号或名称');
    END IF;

    UPDATE `jn_data_migration_log`
    SET `record_count` = v_count,
        `success_count` = v_success,
        `error_count` = v_error,
        `error_message` = v_error_msg,
        `status` = IF(v_error = 0, 'COMPLETED', 'COMPLETED_WITH_ERRORS'),
        `completed_at` = NOW()
    WHERE `batch_no` = v_batch_no;

    SELECT v_batch_no AS batch_no, v_count AS total, v_success AS success, v_error AS error, v_error_msg AS error_message;
END$$

DELIMITER ;

-- ============================================================
-- 步骤3: 数据验证查询
-- ============================================================

-- 3.1 检查各表必填字段NULL值
SELECT 'jn_material' AS table_name, COUNT(*) AS null_required_count
FROM `jn_material`
WHERE `del_flag` = '0' AND (`material_code` IS NULL OR `material_code` = '' OR `material_name` IS NULL OR `material_name` = '')
UNION ALL
SELECT 'jn_customer', COUNT(*)
FROM `jn_customer`
WHERE `del_flag` = '0' AND (`customer_code` IS NULL OR `customer_code` = '' OR `customer_name` IS NULL OR `customer_name` = '')
UNION ALL
SELECT 'jn_supplier', COUNT(*)
FROM `jn_supplier`
WHERE `del_flag` = '0' AND (`supplier_code` IS NULL OR `supplier_code` = '' OR `supplier_name` IS NULL OR `supplier_name` = '')
UNION ALL
SELECT 'jn_warehouse', COUNT(*)
FROM `jn_warehouse`
WHERE (`wh_code` IS NULL OR `wh_code` = '' OR `wh_name` IS NULL OR `wh_name` = '')
UNION ALL
SELECT 'jn_bom', COUNT(*)
FROM `jn_bom`
WHERE `del_flag` = '0' AND (`bom_code` IS NULL OR `bom_code` = '' OR `bom_name` IS NULL OR `bom_name` = '');

-- 3.2 检查孤立外键
SELECT 'jn_sales_order' AS table_name, COUNT(*) AS orphaned_count
FROM `jn_sales_order` so
WHERE so.`del_flag` = '0' AND so.`customer_id` > 0
  AND NOT EXISTS (SELECT 1 FROM `jn_customer` c WHERE c.`customer_id` = so.`customer_id`)
UNION ALL
SELECT 'jn_purchase_order', COUNT(*)
FROM `jn_purchase_order` po
WHERE po.`del_flag` = '0' AND po.`supplier_id` > 0
  AND NOT EXISTS (SELECT 1 FROM `jn_supplier` s WHERE s.`supplier_id` = po.`supplier_id`)
UNION ALL
SELECT 'jn_bom', COUNT(*)
FROM `jn_bom` b
WHERE b.`del_flag` = '0' AND b.`product_id` > 0
  AND NOT EXISTS (SELECT 1 FROM `jn_material` m WHERE m.`material_id` = b.`product_id`)
UNION ALL
SELECT 'jn_inventory', COUNT(*)
FROM `jn_inventory` i
WHERE i.`material_id` > 0
  AND NOT EXISTS (SELECT 1 FROM `jn_material` m WHERE m.`material_id` = i.`material_id`)
UNION ALL
SELECT 'jn_inventory', COUNT(*)
FROM `jn_inventory` i
WHERE i.`wh_id` > 0
  AND NOT EXISTS (SELECT 1 FROM `jn_warehouse` w WHERE w.`wh_id` = i.`wh_id`);

-- 3.3 检查无效状态值
SELECT 'jn_sales_order' AS table_name, `status`, COUNT(*) AS count
FROM `jn_sales_order`
WHERE `del_flag` = '0' AND `status` NOT IN ('PENDING','CONFIRMED','PRODUCTION','SHIPPED','COMPLETED','CANCELLED')
GROUP BY `status`
UNION ALL
SELECT 'jn_purchase_order', `status`, COUNT(*)
FROM `jn_purchase_order`
WHERE `del_flag` = '0' AND `status` NOT IN ('DRAFT','ORDERED','PARTIAL','RECEIVED','COMPLETED','CANCELLED')
GROUP BY `status`
UNION ALL
SELECT 'jn_work_order', `status`, COUNT(*)
FROM `jn_work_order`
WHERE `del_flag` = '0' AND `status` NOT IN ('PENDING','SCHEDULED','IN_PROGRESS','COMPLETED','CLOSED','ON_HOLD')
GROUP BY `status`
UNION ALL
SELECT 'jn_bom', `status`, COUNT(*)
FROM `jn_bom`
WHERE `del_flag` = '0' AND `status` NOT IN ('DRAFT','APPROVED','EFFECTIVE','DEPRECATED')
GROUP BY `status`;

-- 3.4 检查负数数量
SELECT 'jn_inventory' AS table_name, COUNT(*) AS negative_qty_count
FROM `jn_inventory`
WHERE `quantity` < 0 OR `available_qty` < 0
UNION ALL
SELECT 'jn_order_line', COUNT(*)
FROM `jn_order_line`
WHERE `quantity` < 0
UNION ALL
SELECT 'jn_po_line', COUNT(*)
FROM `jn_po_line`
WHERE `quantity` < 0
UNION ALL
SELECT 'jn_work_order', COUNT(*)
FROM `jn_work_order`
WHERE `quantity` < 0;

-- 3.5 检查不应存在的未来日期
SELECT 'jn_sales_order' AS table_name, COUNT(*) AS future_date_count
FROM `jn_sales_order`
WHERE `create_time` > NOW()
UNION ALL
SELECT 'jn_purchase_order', COUNT(*)
FROM `jn_purchase_order`
WHERE `create_time` > NOW()
UNION ALL
SELECT 'jn_work_order', COUNT(*)
FROM `jn_work_order`
WHERE `create_time` > NOW();

-- ============================================================
-- 步骤4: 测试数据插入
-- ============================================================

-- 4.1 插入测试物料（风机专用属性）
INSERT INTO `jn_material` (`material_code`, `material_name`, `spec`, `category_id`, `category_name`, `unit`, `unit_price`, `safety_stock`, `status`, `del_flag`, `fan_type`, `fan_model`, `airflow_cfm`, `pressure_pa`, `power_kw`, `rpm`, `noise_dba`, `impeller_diameter`, `weight_kg`, `is_configurable`, `version`, `remark`, `create_by`, `create_time`)
SELECT * FROM (
    SELECT 'FN-20260527-001' AS `material_code`, 'LR406离心风机' AS `material_name`, 'LR406' AS `spec`, 11 AS `category_id`, '离心风机' AS `category_name`, '台' AS `unit`, 12500.00 AS `unit_price`, 5 AS `safety_stock`, '0' AS `status`, '0' AS `del_flag`, 'CENTRIFUGAL' AS `fan_type`, 'LR406' AS `fan_model`, 12000.0 AS `airflow_cfm`, 2800.0 AS `pressure_pa`, 7.50 AS `power_kw`, 1450 AS `rpm`, 78.5 AS `noise_dba`, 406 AS `impeller_diameter`, 180.00 AS `weight_kg`, 'Y' AS `is_configurable`, 1 AS `version`, '高效离心通风机' AS `remark`, 'admin' AS `create_by`, NOW() AS `create_time`
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `jn_material` WHERE `material_code` = 'FN-20260527-001');

INSERT INTO `jn_material` (`material_code`, `material_name`, `spec`, `category_id`, `category_name`, `unit`, `unit_price`, `safety_stock`, `status`, `del_flag`, `fan_type`, `fan_model`, `airflow_cfm`, `pressure_pa`, `power_kw`, `rpm`, `noise_dba`, `impeller_diameter`, `weight_kg`, `is_configurable`, `version`, `remark`, `create_by`, `create_time`)
SELECT * FROM (
    SELECT 'FN-20260527-002' AS `material_code`, 'LR355离心风机' AS `material_name`, 'LR355' AS `spec`, 11 AS `category_id`, '离心风机' AS `category_name`, '台' AS `unit`, 9800.00 AS `unit_price`, 5 AS `safety_stock`, '0' AS `status`, '0' AS `del_flag`, 'CENTRIFUGAL' AS `fan_type`, 'LR355' AS `fan_model`, 8000.0 AS `airflow_cfm`, 2200.0 AS `pressure_pa`, 5.50 AS `power_kw`, 1450 AS `rpm`, 75.0 AS `noise_dba`, 355 AS `impeller_diameter`, 135.00 AS `weight_kg`, 'Y' AS `is_configurable`, 1 AS `version`, '中压离心通风机' AS `remark`, 'admin' AS `create_by`, NOW() AS `create_time`
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `jn_material` WHERE `material_code` = 'FN-20260527-002');

INSERT INTO `jn_material` (`material_code`, `material_name`, `spec`, `category_id`, `category_name`, `unit`, `unit_price`, `safety_stock`, `status`, `del_flag`, `fan_type`, `fan_model`, `airflow_cfm`, `pressure_pa`, `power_kw`, `rpm`, `noise_dba`, `impeller_diameter`, `weight_kg`, `is_configurable`, `version`, `remark`, `create_by`, `create_time`)
SELECT * FROM (
    SELECT 'FN-20260527-003' AS `material_code`, '轴流风机SF-6G' AS `material_name`, 'SF-6G' AS `spec`, 12 AS `category_id`, '轴流风机' AS `category_name`, '台' AS `unit`, 3200.00 AS `unit_price`, 10 AS `safety_stock`, '0' AS `status`, '0' AS `del_flag`, 'AXIAL' AS `fan_type`, 'SF-6G' AS `fan_model`, 18000.0 AS `airflow_cfm`, 400.0 AS `pressure_pa`, 3.00 AS `power_kw`, 960 AS `rpm`, 72.0 AS `noise_dba`, 600 AS `impeller_diameter`, 45.00 AS `weight_kg`, 'N' AS `is_configurable`, 1 AS `version`, '管道式轴流风机' AS `remark`, 'admin' AS `create_by`, NOW() AS `create_time`
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `jn_material` WHERE `material_code` = 'FN-20260527-003');

-- 4.2 插入BOM组件物料（叶轮、蜗壳、电机）
INSERT INTO `jn_material` (`material_code`, `material_name`, `spec`, `category_id`, `category_name`, `unit`, `unit_price`, `safety_stock`, `status`, `del_flag`, `remark`, `create_by`, `create_time`)
SELECT * FROM (
    SELECT 'FN-20260527-004' AS `material_code`, 'LR406叶轮总成' AS `material_name`, 'LR406-YL' AS `spec`, 21 AS `category_id`, '叶轮组' AS `category_name`, '套' AS `unit`, 3200.00 AS `unit_price`, 10 AS `safety_stock`, '0' AS `status`, '0' AS `del_flag`, 'LR406风机叶轮组件' AS `remark`, 'admin' AS `create_by`, NOW() AS `create_time`
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `jn_material` WHERE `material_code` = 'FN-20260527-004');

INSERT INTO `jn_material` (`material_code`, `material_name`, `spec`, `category_id`, `category_name`, `unit`, `unit_price`, `safety_stock`, `status`, `del_flag`, `remark`, `create_by`, `create_time`)
SELECT * FROM (
    SELECT 'FN-20260527-005' AS `material_code`, 'LR406蜗壳总成' AS `material_name`, 'LR406-WK' AS `spec`, 22 AS `category_id`, '蜗壳组' AS `category_name`, '套' AS `unit`, 2800.00 AS `unit_price`, 10 AS `safety_stock`, '0' AS `status`, '0' AS `del_flag`, 'LR406风机蜗壳组件' AS `remark`, 'admin' AS `create_by`, NOW() AS `create_time`
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `jn_material` WHERE `material_code` = 'FN-20260527-005');

INSERT INTO `jn_material` (`material_code`, `material_name`, `spec`, `category_id`, `category_name`, `unit`, `unit_price`, `safety_stock`, `status`, `del_flag`, `remark`, `create_by`, `create_time`)
SELECT * FROM (
    SELECT 'FN-20260527-006' AS `material_code`, '7.5kW风机电机' AS `material_name`, 'Y132S-4-7.5kW' AS `spec`, 31 AS `category_id`, '电机' AS `category_name`, '台' AS `unit`, 4500.00 AS `unit_price`, 15 AS `safety_stock`, '0' AS `status`, '0' AS `del_flag`, '7.5kW/380V/1450rpm风机专用电机' AS `remark`, 'admin' AS `create_by`, NOW() AS `create_time`
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `jn_material` WHERE `material_code` = 'FN-20260527-006');

-- 4.3 插入测试客户
INSERT INTO `jn_customer` (`customer_code`, `customer_name`, `contact`, `phone`, `email`, `region`, `address`, `credit_limit`, `tax_no`, `level`, `status`, `del_flag`, `remark`, `create_by`, `create_time`)
SELECT * FROM (
    SELECT 'C-2026-0001' AS `customer_code`, '上海工业通风设备有限公司' AS `customer_name`, '张经理' AS `contact`, '13800138001' AS `phone`, 'shanghai@indus.com' AS `email`, '华东' AS `region`, '上海市浦东新区工业园1号' AS `address`, 500000.00 AS `credit_limit`, '91310000MA1XXXXX01' AS `tax_no`, '2' AS `level`, '0' AS `status`, '0' AS `del_flag`, '长期合作客户' AS `remark`, 'admin' AS `create_by`, NOW() AS `create_time`
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `jn_customer` WHERE `customer_name` = '上海工业通风设备有限公司');

INSERT INTO `jn_customer` (`customer_code`, `customer_name`, `contact`, `phone`, `email`, `region`, `address`, `credit_limit`, `tax_no`, `level`, `status`, `del_flag`, `remark`, `create_by`, `create_time`)
SELECT * FROM (
    SELECT 'C-2026-0002' AS `customer_code`, '江苏环保科技工程有限公司' AS `customer_name`, '李经理' AS `contact`, '13900139002' AS `phone`, 'jiangsu@huanbao.com' AS `email`, '华东' AS `region`, '江苏省南京市环保产业园2号' AS `address`, 300000.00 AS `credit_limit`, '91320000MA2XXXXX02' AS `tax_no`, '2' AS `level`, '0' AS `status`, '0' AS `del_flag`, '环保工程配套' AS `remark`, 'admin' AS `create_by`, NOW() AS `create_time`
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `jn_customer` WHERE `customer_name` = '江苏环保科技工程有限公司');

-- 4.4 插入测试供应商
INSERT INTO `jn_supplier` (`supplier_code`, `supplier_name`, `contact`, `phone`, `email`, `biz_scope`, `address`, `rating`, `cooperation`, `del_flag`, `remark`, `create_by`, `create_time`)
SELECT * FROM (
    SELECT 'S-2026-0001' AS `supplier_code`, '无锡钢材供应有限公司' AS `supplier_name`, '王经理' AS `contact`, '13700137001' AS `phone`, 'wuxi@steel.com' AS `email`, '钢板/型材/管材' AS `biz_scope`, '江苏省无锡市新区钢材城8号' AS `address`, 4 AS `rating`, '0' AS `cooperation`, '0' AS `del_flag`, '主要钢板供应商' AS `remark`, 'admin' AS `create_by`, NOW() AS `create_time`
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `jn_supplier` WHERE `supplier_name` = '无锡钢材供应有限公司');

INSERT INTO `jn_supplier` (`supplier_code`, `supplier_name`, `contact`, `phone`, `email`, `biz_scope`, `address`, `rating`, `cooperation`, `del_flag`, `remark`, `create_by`, `create_time`)
SELECT * FROM (
    SELECT 'S-2026-0002' AS `supplier_code`, '苏州电机科技有限公司' AS `supplier_name`, '赵经理' AS `contact`, '13600136002' AS `phone`, 'suzhou@motor.com' AS `email`, '风机专用电机/防爆电机' AS `biz_scope`, '江苏省苏州市吴江区电机产业园3号' AS `address`, 5 AS `rating`, '0' AS `cooperation`, '0' AS `del_flag`, '电机战略合作供应商' AS `remark`, 'admin' AS `create_by`, NOW() AS `create_time`
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `jn_supplier` WHERE `supplier_name` = '苏州电机科技有限公司');

-- 4.5 插入测试仓库
INSERT INTO `jn_warehouse` (`wh_code`, `wh_name`, `wh_type`, `status`, `remark`, `create_time`)
SELECT * FROM (
    SELECT 'WH-RAW-02' AS `wh_code`, '原材料仓库' AS `wh_name`, '0' AS `wh_type`, '0' AS `status`, '钢板/电机/轴承等原材料' AS `remark`, NOW() AS `create_time`
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `jn_warehouse` WHERE `wh_code` = 'WH-RAW-02');

INSERT INTO `jn_warehouse` (`wh_code`, `wh_name`, `wh_type`, `status`, `remark`, `create_time`)
SELECT * FROM (
    SELECT 'WH-FIN-02' AS `wh_code`, '成品仓库' AS `wh_name`, '2' AS `wh_type`, '0' AS `status`, '风机成品' AS `remark`, NOW() AS `create_time`
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `jn_warehouse` WHERE `wh_code` = 'WH-FIN-02');

-- 4.6 插入测试BOM（LR406风机BOM）
INSERT INTO `jn_bom` (`bom_code`, `bom_name`, `product_id`, `product_code`, `product_name`, `version`, `status`, `total_cost`, `del_flag`, `remark`, `create_by`, `create_time`)
SELECT * FROM (
    SELECT 'BOM-LR406-V1' AS `bom_code`, 'LR406离心风机BOM' AS `bom_name`,
        (SELECT `material_id` FROM `jn_material` WHERE `material_code` = 'FN-20260527-001' LIMIT 1) AS `product_id`,
        'FN-20260527-001' AS `product_code`, 'LR406离心风机' AS `product_name`,
        'v1.0' AS `version`, 'EFFECTIVE' AS `status`,
        10500.00 AS `total_cost`, '0' AS `del_flag`, 'LR406离心风机标准BOM配置' AS `remark`, 'admin' AS `create_by`, NOW() AS `create_time`
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `jn_bom` WHERE `bom_code` = 'BOM-LR406-V1');

INSERT INTO `jn_bom_line` (`bom_id`, `material_id`, `material_code`, `material_name`, `spec`, `quantity`, `unit`, `scrap_rate`, `sort_order`)
SELECT b.`bom_id`,
    (SELECT `material_id` FROM `jn_material` WHERE `material_code` = 'FN-20260527-004' LIMIT 1),
    'FN-20260527-004', 'LR406叶轮总成', 'LR406-YL', 1.0000, '套', 1.00, 1
FROM `jn_bom` b
WHERE b.`bom_code` = 'BOM-LR406-V1'
  AND NOT EXISTS (SELECT 1 FROM `jn_bom_line` bl WHERE bl.`bom_id` = b.`bom_id` AND bl.`material_code` = 'FN-20260527-004');

INSERT INTO `jn_bom_line` (`bom_id`, `material_id`, `material_code`, `material_name`, `spec`, `quantity`, `unit`, `scrap_rate`, `sort_order`)
SELECT b.`bom_id`,
    (SELECT `material_id` FROM `jn_material` WHERE `material_code` = 'FN-20260527-005' LIMIT 1),
    'FN-20260527-005', 'LR406蜗壳总成', 'LR406-WK', 1.0000, '套', 2.00, 2
FROM `jn_bom` b
WHERE b.`bom_code` = 'BOM-LR406-V1'
  AND NOT EXISTS (SELECT 1 FROM `jn_bom_line` bl WHERE bl.`bom_id` = b.`bom_id` AND bl.`material_code` = 'FN-20260527-005');

INSERT INTO `jn_bom_line` (`bom_id`, `material_id`, `material_code`, `material_name`, `spec`, `quantity`, `unit`, `scrap_rate`, `sort_order`)
SELECT b.`bom_id`,
    (SELECT `material_id` FROM `jn_material` WHERE `material_code` = 'FN-20260527-006' LIMIT 1),
    'FN-20260527-006', '7.5kW风机电机', 'Y132S-4-7.5kW', 1.0000, '台', 0.50, 3
FROM `jn_bom` b
WHERE b.`bom_code` = 'BOM-LR406-V1'
  AND NOT EXISTS (SELECT 1 FROM `jn_bom_line` bl WHERE bl.`bom_id` = b.`bom_id` AND bl.`material_code` = 'FN-20260527-006');

-- 4.7 插入测试工艺路线（LR406风机）
INSERT INTO `jn_process_route` (`route_name`, `product_id`, `status`, `remark`, `create_by`, `create_time`)
SELECT 'LR406离心风机工艺路线',
    (SELECT `material_id` FROM `jn_material` WHERE `material_code` = 'FN-20260527-001' LIMIT 1),
    '0', 'LR406标准工艺路线5道工序', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `jn_process_route` WHERE `route_name` = 'LR406离心风机工艺路线');

SET @v_route_id = (SELECT `route_id` FROM `jn_process_route` WHERE `route_name` = 'LR406离心风机工艺路线' LIMIT 1);

INSERT INTO `jn_process_operation` (`route_id`, `seq_no`, `operation_name`, `work_center`, `standard_hours`, `setup_hours`, `is_qc_point`, `description`, `sort_order`)
SELECT @v_route_id, 1, '下料', '下料区', 4.5, 0.5, 'N', '钢板切割/激光下料', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `jn_process_operation` WHERE `route_id` = @v_route_id AND `seq_no` = 1);

INSERT INTO `jn_process_operation` (`route_id`, `seq_no`, `operation_name`, `work_center`, `standard_hours`, `setup_hours`, `is_qc_point`, `description`, `sort_order`)
SELECT @v_route_id, 2, '焊接', '焊接区', 8.0, 1.0, 'N', '蜗壳/底座焊接', 2
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `jn_process_operation` WHERE `route_id` = @v_route_id AND `seq_no` = 2);

INSERT INTO `jn_process_operation` (`route_id`, `seq_no`, `operation_name`, `work_center`, `standard_hours`, `setup_hours`, `is_qc_point`, `description`, `sort_order`)
SELECT @v_route_id, 3, '动平衡', '平衡机房', 3.0, 0.5, 'Y', '叶轮G2.5级动平衡校正', 3
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `jn_process_operation` WHERE `route_id` = @v_route_id AND `seq_no` = 3);

INSERT INTO `jn_process_operation` (`route_id`, `seq_no`, `operation_name`, `work_center`, `standard_hours`, `setup_hours`, `is_qc_point`, `description`, `sort_order`)
SELECT @v_route_id, 4, '装配', '装配线', 6.0, 1.0, 'N', '叶轮/蜗壳/电机/底座总装', 4
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `jn_process_operation` WHERE `route_id` = @v_route_id AND `seq_no` = 4);

INSERT INTO `jn_process_operation` (`route_id`, `seq_no`, `operation_name`, `work_center`, `standard_hours`, `setup_hours`, `is_qc_point`, `description`, `sort_order`)
SELECT @v_route_id, 5, '试机', '测试台', 2.0, 0.5, 'Y', '整机试运转/风量风压测试', 5
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `jn_process_operation` WHERE `route_id` = @v_route_id AND `seq_no` = 5);

-- 4.8 插入测试生产工单（LR406风机，10台）
INSERT INTO `jn_work_order` (`order_no`, `product_id`, `product_code`, `product_name`, `bom_id`, `bom_version`, `quantity`, `priority`, `status`, `planned_start`, `planned_end`, `remark`, `create_by`, `create_time`)
SELECT 'WO-20260527-001',
    (SELECT `material_id` FROM `jn_material` WHERE `material_code` = 'FN-20260527-001' LIMIT 1),
    'FN-20260527-001', 'LR406离心风机',
    (SELECT `bom_id` FROM `jn_bom` WHERE `bom_code` = 'BOM-LR406-V1' LIMIT 1),
    'v1.0', 10, '2', 'PENDING',
    '2026-06-01 08:00:00', '2026-06-15 17:00:00',
    'Sprint13测试工单-LR406离心风机10台', 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `jn_work_order` WHERE `order_no` = 'WO-20260527-001');

-- ============================================================
-- 步骤5: 执行数据清洗
-- ============================================================
CALL clean_material_data();
CALL clean_customer_data();
CALL clean_supplier_data();

-- ============================================================
-- 完成标记
-- ============================================================
SELECT 'Sprint 13 数据迁移完成' AS status;
SELECT * FROM `jn_data_migration_log` ORDER BY `log_id` DESC;
