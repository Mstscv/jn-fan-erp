PROJECT_DIR=${1:-..}

echo "============================================"
echo " 精恩风机ERP系统 - 源码交付清单"
echo " 生成日期: $(date '+%Y-%m-%d %H:%M:%S')"
echo "============================================"
echo ""

echo "项目总览"
echo "--------"
echo "项目名称: 无锡精恩风机有限公司ERP系统"
echo "项目代号: JN-FAN-ERP"
echo "代码仓库: GitHub: Mstscv/jn-fan-erp"
echo ""

echo "Java源码统计"
echo "-----------"
find "$PROJECT_DIR" -name "*.java" -not -path "*/test/*" -not -path "*/target/*" | while read f; do
    echo "  $(echo $f | sed "s|$PROJECT_DIR/||")"
done | wc -l | xargs -I{} echo "Java文件总数: {}"
echo ""

echo "XML配置文件"
echo "-----------"
find "$PROJECT_DIR" -name "*.xml" -not -path "*/target/*" | while read f; do
    echo "  $(echo $f | sed "s|$PROJECT_DIR/||")"
done | wc -l | xargs -I{} echo "XML文件总数: {}"
echo ""

echo "SQL脚本"
echo "-------"
find "$PROJECT_DIR" -name "*.sql" -not -path "*/target/*" | while read f; do
    echo "  $(echo $f | sed "s|$PROJECT_DIR/||")"
done | wc -l | xargs -I{} echo "SQL文件总数: {}"
echo ""

echo "YAML配置"
echo "--------"
find "$PROJECT_DIR" -name "*.yml" -not -path "*/target/*" | while read f; do
    echo "  $(echo $f | sed "s|$PROJECT_DIR/||")"
done | wc -l | xargs -I{} echo "YAML文件总数: {}"
echo ""

echo "模块清单"
echo "--------"
echo "  ruoyi-auth           - 认证中心"
echo "  ruoyi-gateway        - 网关服务"
echo "  ruoyi-system         - 系统管理"
echo "  ruoyi-gen            - 代码生成"
echo "  ruoyi-job            - 定时任务"
echo "  ruoyi-file           - 文件服务"
echo "  jn-erp-common-core   - ERP公共核心"
echo "  jn-erp-material      - 物料管理"
echo "  jn-erp-sales         - 销售管理"
echo "  jn-erp-purchase      - 采购管理"
echo "  jn-erp-warehouse     - 仓库管理"
echo "  jn-erp-production    - 生产管理 (含BOM/工单/排程/报工/质检/委外)"
echo "  jn-erp-finance       - 财务管理 (含应收/应付/费用/成本/报表)"
echo ""

echo "测试用例统计"
echo "-----------"
find "$PROJECT_DIR" -path "*/test/*" -name "*.java" | while read f; do
    echo "  $(echo $f | sed "s|$PROJECT_DIR/||")"
done | wc -l | xargs -I{} echo "测试文件总数: {}"
echo ""

echo "构建要求"
echo "--------"
echo "  JDK:    17+"
echo "  Maven:  3.8+"
echo "  Node:   18+"
echo "  Docker: 24+"
echo "  MySQL:  8.0"
echo "  Redis:  7.x"
echo ""

echo "============================================"
echo " 交付物总计"
echo "============================================"
find "$PROJECT_DIR" -type f -not -path "*/target/*" -not -path "*/.git/*" -not -path "*/node_modules/*" | wc -l | xargs -I{} echo "总文件数: {}"
