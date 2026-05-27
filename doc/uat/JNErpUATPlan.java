package com.jn.erp.uat;

import java.util.List;
import java.util.ArrayList;

public class JNErpUATPlan {

    public static final UATTestCase[] TEST_CASES = new UATTestCase[] {
        new UATTestCase("TC-UAT-001", "销售管理", "创建客户→创建报价单→审核报价→生成订单→确认订单",
                "客户代表", "销售主管", "P0"),
        new UATTestCase("TC-UAT-002", "销售管理", "订单变更→取消订单→查询订单历史",
                "销售主管", "销售经理", "P1"),
        new UATTestCase("TC-UAT-003", "采购管理", "创建物料需求→MRP运算→生成采购建议→创建采购订单→审批→发送供应商",
                "采购员", "采购主管", "P0"),
        new UATTestCase("TC-UAT-004", "采购管理", "采购收货→来料检验→合格入库/退货处理→部分收货",
                "仓管员", "采购主管", "P0"),
        new UATTestCase("TC-UAT-005", "仓库管理", "采购入库→销售出库→生产领料→库存调拨→库存查询",
                "仓管员", "仓库主管", "P0"),
        new UATTestCase("TC-UAT-006", "仓库管理", "创建盘点计划→录入盘点数据→盘盈盘亏处理→库存调整",
                "仓管员", "财务主管", "P1"),
        new UATTestCase("TC-UAT-007", "BOM管理", "创建BOM→添加多层级物料→设置选配规则→BOM审核→BOM版本管理",
                "技术员", "技术主管", "P0"),
        new UATTestCase("TC-UAT-008", "BOM管理", "BOM成本卷积计算→BOM复制→BOM物料替换",
                "技术员", "技术主管", "P1"),
        new UATTestCase("TC-UAT-009", "生产管理", "创建工单→关联BOM和工艺路线→BOM展开→有限产能排程→领料→开工",
                "生产计划", "生产主管", "P0"),
        new UATTestCase("TC-UAT-010", "生产管理", "工序报工→工序交接→不良品处理→返工单→完工质检→入库",
                "车间主任", "生产主管", "P0"),
        new UATTestCase("TC-UAT-011", "质量管理", "来料检验(IQC)→过程检验(IPQC)→成品检验(OQC)→检验标准维护",
                "质检员", "质量主管", "P1"),
        new UATTestCase("TC-UAT-012", "委外管理", "创建委外订单→委外发料→委外收货→加工费结算",
                "采购员", "采购主管", "P1"),
        new UATTestCase("TC-UAT-013", "财务管理", "销售发货→自动生成应收→收款核销→应收余额查询→账龄分析",
                "财务", "财务主管", "P0"),
        new UATTestCase("TC-UAT-014", "财务管理", "采购收货→生成应付→付款核销→费用报销→报销审批流程",
                "财务", "财务主管", "P0"),
        new UATTestCase("TC-UAT-015", "报表中心", "查看经营仪表盘→销售分析报表→库存周转分析→产量日报→工时汇总",
                "总经理", "项目经理", "P1")
    };

    public static class UATTestCase {
        private String id;
        private String module;
        private String scenario;
        private String tester;
        private String reviewer;
        private String priority;

        public UATTestCase(String id, String module, String scenario, String tester, String reviewer, String priority) {
            this.id = id;
            this.module = module;
            this.scenario = scenario;
            this.tester = tester;
            this.reviewer = reviewer;
            this.priority = priority;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getModule() {
            return module;
        }

        public void setModule(String module) {
            this.module = module;
        }

        public String getScenario() {
            return scenario;
        }

        public void setScenario(String scenario) {
            this.scenario = scenario;
        }

        public String getTester() {
            return tester;
        }

        public void setTester(String tester) {
            this.tester = tester;
        }

        public String getReviewer() {
            return reviewer;
        }

        public void setReviewer(String reviewer) {
            this.reviewer = reviewer;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }
    }

    public static class UATAcceptanceReport {
        private String projectName;
        private String customerName;
        private String testDate;
        private int totalCases;
        private int passed;
        private int failed;
        private int blocked;
        private String overallResult;
        private String customerSignOff;
        private String customerSignDate;
        private List<String> issues;

        public UATAcceptanceReport(String projectName, String customerName, String testDate,
                                    int totalCases, int passed, int failed, int blocked,
                                    String overallResult, String customerSignOff, String customerSignDate,
                                    List<String> issues) {
            this.projectName = projectName;
            this.customerName = customerName;
            this.testDate = testDate;
            this.totalCases = totalCases;
            this.passed = passed;
            this.failed = failed;
            this.blocked = blocked;
            this.overallResult = overallResult;
            this.customerSignOff = customerSignOff;
            this.customerSignDate = customerSignDate;
            this.issues = issues;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getTestDate() {
            return testDate;
        }

        public void setTestDate(String testDate) {
            this.testDate = testDate;
        }

        public int getTotalCases() {
            return totalCases;
        }

        public void setTotalCases(int totalCases) {
            this.totalCases = totalCases;
        }

        public int getPassed() {
            return passed;
        }

        public void setPassed(int passed) {
            this.passed = passed;
        }

        public int getFailed() {
            return failed;
        }

        public void setFailed(int failed) {
            this.failed = failed;
        }

        public int getBlocked() {
            return blocked;
        }

        public void setBlocked(int blocked) {
            this.blocked = blocked;
        }

        public String getOverallResult() {
            return overallResult;
        }

        public void setOverallResult(String overallResult) {
            this.overallResult = overallResult;
        }

        public String getCustomerSignOff() {
            return customerSignOff;
        }

        public void setCustomerSignOff(String customerSignOff) {
            this.customerSignOff = customerSignOff;
        }

        public String getCustomerSignDate() {
            return customerSignDate;
        }

        public void setCustomerSignDate(String customerSignDate) {
            this.customerSignDate = customerSignDate;
        }

        public List<String> getIssues() {
            return issues;
        }

        public void setIssues(List<String> issues) {
            this.issues = issues;
        }
    }

    public static UATAcceptanceReport createAcceptanceReport() {
        List<String> issues = new ArrayList<String>();
        issues.add("待确认问题列表");
        return new UATAcceptanceReport(
            "JN-FAN-ERP 风机行业ERP系统",
            "_______________（客户名称）",
            "_______________（测试日期）",
            15,
            0,
            0,
            0,
            "通过 / 有条件通过 / 不通过",
            "_______________（客户签字）",
            "_______________（签字日期）",
            issues
        );
    }
}
