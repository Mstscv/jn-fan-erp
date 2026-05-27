SELECT '物料主数据' as check_point,
       COUNT(*) as total_count,
       SUM(CASE WHEN material_code IS NULL OR material_name IS NULL THEN 1 ELSE 0 END) as invalid_count,
       COUNT(DISTINCT material_code) as unique_codes
FROM jn_material
WHERE del_flag = '0';

SELECT '客户数据' as check_point,
       COUNT(*) as total_count,
       SUM(CASE WHEN customer_name IS NULL THEN 1 ELSE 0 END) as invalid_count
FROM jn_customer
WHERE del_flag = '0';

SELECT '供应商数据' as check_point,
       COUNT(*) as total_count,
       SUM(CASE WHEN supplier_name IS NULL THEN 1 ELSE 0 END) as invalid_count
FROM jn_supplier
WHERE del_flag = '0';

SELECT '库存数据' as check_point,
       COUNT(*) as total_inventory_items,
       SUM(quantity) as total_quantity,
       SUM(CASE WHEN quantity < 0 THEN 1 ELSE 0 END) as negative_stock_count
FROM jn_inventory;

SELECT '应收账款' as check_point,
       COUNT(*) as total_receivables,
       SUM(total_amount) as total_amount,
       SUM(balance_amount) as total_balance,
       SUM(CASE WHEN due_date < CURDATE() AND status IN ('PENDING', 'PARTIAL') THEN 1 ELSE 0 END) as overdue_count
FROM jn_receivable;

SELECT '应付账款' as check_point,
       COUNT(*) as total_payables,
       SUM(total_amount) as total_amount,
       SUM(balance_amount) as total_balance
FROM jn_payable;

SELECT '工单状态分布' as check_point,
       status,
       COUNT(*) as count
FROM jn_work_order
GROUP BY status
ORDER BY count DESC;

SELECT '未完成工单' as check_point,
       COUNT(*) as unfinished_count,
       SUM(quantity) as total_quantity
FROM jn_work_order
WHERE status NOT IN ('COMPLETED', 'CLOSED', 'CANCELLED');

SELECT '库存预警' as check_point,
       COUNT(*) as alert_count
FROM jn_inventory i
WHERE i.quantity <= 10;

SELECT '=== 校验报告 ===' as report_section;

SELECT CONCAT('物料数: ', (SELECT COUNT(*) FROM jn_material WHERE del_flag = '0')) as material_count,
       CONCAT('客户数: ', (SELECT COUNT(*) FROM jn_customer WHERE del_flag = '0')) as customer_count,
       CONCAT('供应商数: ', (SELECT COUNT(*) FROM jn_supplier WHERE del_flag = '0')) as supplier_count,
       CONCAT('库存项数: ', (SELECT COUNT(*) FROM jn_inventory)) as inventory_count,
       CONCAT('未结应收: ', (SELECT COALESCE(SUM(balance_amount), 0) FROM jn_receivable WHERE status IN ('PENDING', 'PARTIAL'))) as ar_balance,
       CONCAT('未结应付: ', (SELECT COALESCE(SUM(balance_amount), 0) FROM jn_payable WHERE status IN ('PENDING', 'PARTIAL'))) as ap_balance,
       CONCAT('在制工单: ', (SELECT COUNT(*) FROM jn_work_order WHERE status IN ('PENDING', 'SCHEDULED', 'IN_PROGRESS'))) as wip_orders;
