#!/bin/bash

JMETER_HOME=/path/to/jmeter
TEST_PLAN=ErpPerformanceTest.jmx
RESULT_FILE=results/erp-perf-$(date +%Y%m%d-%H%M).jtl
REPORT_DIR=reports/erp-perf-$(date +%Y%m%d-%H%M)
BASE_URL=http://localhost:8080

generate_test_plan() {
    cat > $TEST_PLAN << 'JMXEOF'
<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2" properties="5.0">
  <hashTree>
    <TestPlan guiclass="TestPlanGui" testclass="TestPlan" testname="JN-FAN-ERP Performance Test">
      <stringProp name="TestPlan.comments">Target: 100 concurrent users, response < 3s</stringProp>
    </TestPlan>
    <hashTree>
      <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="ERP Concurrent Users">
        <intProp name="ThreadGroup.num_threads">100</intProp>
        <intProp name="ThreadGroup.ramp_time">10</intProp>
        <intProp name="ThreadGroup.loops">1</intProp>
        <stringProp name="ThreadGroup.scheduler">false</stringProp>
      </ThreadGroup>
      <hashTree>
        <ConfigTestElement guiclass="HttpDefaultsGui" testclass="ConfigTestElement" testname="HTTP Request Defaults">
          <stringProp name="HTTPSampler.domain">localhost</stringProp>
          <stringProp name="HTTPSampler.port">8080</stringProp>
          <stringProp name="HTTPSampler.protocol">http</stringProp>
          <stringProp name="HTTPSampler.contentEncoding">UTF-8</stringProp>
        </ConfigTestElement>
        <hashTree/>
        <HeaderManager guiclass="HeaderPanel" testclass="HeaderManager" testname="HTTP Header Manager">
          <collectionProp name="HeaderManager.headers">
            <elementProp name="Content-Type" elementType="Header">
              <stringProp name="Header.name">Content-Type</stringProp>
              <stringProp name="Header.value">application/json</stringProp>
            </elementProp>
          </collectionProp>
        </HeaderManager>
        <hashTree/>
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="1-Login">
          <stringProp name="HTTPSampler.path">/auth/login</stringProp>
          <stringProp name="HTTPSampler.method">POST</stringProp>
          <stringProp name="HTTPSampler.postBodyRaw">true</stringProp>
          <collectionProp name="HTTPSampler.arguments">
            <elementProp name="" elementType="HTTPArgument">
              <stringProp name="Argument.value">{"username":"admin","password":"admin123"}</stringProp>
            </elementProp>
          </collectionProp>
        </HTTPSamplerProxy>
        <hashTree>
          <ResponseAssertion guiclass="AssertionGui" testclass="ResponseAssertion" testname="Assert Login">
            <collectionProp name="Asserion.test_strings">
              <stringProp name="49586">200</stringProp>
            </collectionProp>
            <stringProp name="Assertion.test_field">Assertion.response_code</stringProp>
          </ResponseAssertion>
          <hashTree/>
          <JSONPostProcessor guiclass="JSONPostProcessorGui" testclass="JSONPostProcessor" testname="Extract Token">
            <stringProp name="JSONPostProcessor.referenceNames">token</stringProp>
            <stringProp name="JSONPostProcessor.jsonPathExpressions">$.data.token</stringProp>
          </JSONPostProcessor>
          <hashTree/>
        </hashTree>
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="2-MaterialList">
          <stringProp name="HTTPSampler.path">/material/list</stringProp>
          <stringProp name="HTTPSampler.method">GET</stringProp>
        </HTTPSamplerProxy>
        <hashTree>
          <ResponseAssertion guiclass="AssertionGui" testclass="ResponseAssertion" testname="Assert Material List">
            <collectionProp name="Asserion.test_strings">
              <stringProp name="49586">200</stringProp>
            </collectionProp>
            <stringProp name="Assertion.test_field">Assertion.response_code</stringProp>
          </ResponseAssertion>
          <hashTree/>
          <DurationAssertion guiclass="DurationAssertionGui" testclass="DurationAssertion" testname="Assert Response Time < 3s">
            <longProp name="DurationAssertion.duration">3000</longProp>
          </DurationAssertion>
          <hashTree/>
        </hashTree>
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="3-WorkOrderCreate">
          <stringProp name="HTTPSampler.path">/production/workorder/create</stringProp>
          <stringProp name="HTTPSampler.method">POST</stringProp>
          <stringProp name="HTTPSampler.postBodyRaw">true</stringProp>
          <collectionProp name="HTTPSampler.arguments">
            <elementProp name="" elementType="HTTPArgument">
              <stringProp name="Argument.value">{"productName":"LR406风机","quantity":10,"plannedStart":"${__time(yyyy-MM-dd)}","plannedEnd":"${__time(yyyy-MM-dd,plannedEnd)}"}</stringProp>
            </elementProp>
          </collectionProp>
        </HTTPSamplerProxy>
        <hashTree>
          <ResponseAssertion guiclass="AssertionGui" testclass="ResponseAssertion" testname="Assert Create WO">
            <collectionProp name="Asserion.test_strings">
              <stringProp name="49586">200</stringProp>
            </collectionProp>
            <stringProp name="Assertion.test_field">Assertion.response_code</stringProp>
          </ResponseAssertion>
          <hashTree/>
        </hashTree>
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="4-WorkOrderList">
          <stringProp name="HTTPSampler.path">/production/workorder/list</stringProp>
          <stringProp name="HTTPSampler.method">GET</stringProp>
        </HTTPSamplerProxy>
        <hashTree>
          <ResponseAssertion guiclass="AssertionGui" testclass="ResponseAssertion" testname="Assert WO List">
            <collectionProp name="Asserion.test_strings">
              <stringProp name="49586">200</stringProp>
            </collectionProp>
            <stringProp name="Assertion.test_field">Assertion.response_code</stringProp>
          </ResponseAssertion>
          <hashTree/>
        </hashTree>
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="5-BomTreeQuery">
          <stringProp name="HTTPSampler.path">/production/bom/tree/1</stringProp>
          <stringProp name="HTTPSampler.method">GET</stringProp>
        </HTTPSamplerProxy>
        <hashTree>
          <ResponseAssertion guiclass="AssertionGui" testclass="ResponseAssertion" testname="Assert BOM Tree">
            <collectionProp name="Asserion.test_strings">
              <stringProp name="49586">200</stringProp>
            </collectionProp>
            <stringProp name="Assertion.test_field">Assertion.response_code</stringProp>
          </ResponseAssertion>
          <hashTree/>
        </hashTree>
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="6-ReportSubmit">
          <stringProp name="HTTPSampler.path">/production/report/submit</stringProp>
          <stringProp name="HTTPSampler.method">POST</stringProp>
          <stringProp name="HTTPSampler.postBodyRaw">true</stringProp>
          <collectionProp name="HTTPSampler.arguments">
            <elementProp name="" elementType="HTTPArgument">
              <stringProp name="Argument.value">{"orderId":1,"outputQty":10,"goodQty":9,"defectQty":1,"reportDate":"${__time(yyyy-MM-dd)}"}</stringProp>
            </elementProp>
          </collectionProp>
        </HTTPSamplerProxy>
        <hashTree>
          <ResponseAssertion guiclass="AssertionGui" testclass="ResponseAssertion" testname="Assert Report">
            <collectionProp name="Asserion.test_strings">
              <stringProp name="49586">200</stringProp>
            </collectionProp>
            <stringProp name="Assertion.test_field">Assertion.response_code</stringProp>
          </ResponseAssertion>
          <hashTree/>
        </hashTree>
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="7-SalesOrderCreate">
          <stringProp name="HTTPSampler.path">/sales/order/create</stringProp>
          <stringProp name="HTTPSampler.method">POST</stringProp>
          <stringProp name="HTTPSampler.postBodyRaw">true</stringProp>
          <collectionProp name="HTTPSampler.arguments">
            <elementProp name="" elementType="HTTPArgument">
              <stringProp name="Argument.value">{"customerName":"测试客户","productName":"LR406风机","quantity":5,"orderDate":"${__time(yyyy-MM-dd)}"}</stringProp>
            </elementProp>
          </collectionProp>
        </HTTPSamplerProxy>
        <hashTree>
          <ResponseAssertion guiclass="AssertionGui" testclass="ResponseAssertion" testname="Assert Sales Order">
            <collectionProp name="Asserion.test_strings">
              <stringProp name="49586">200</stringProp>
            </collectionProp>
            <stringProp name="Assertion.test_field">Assertion.response_code</stringProp>
          </ResponseAssertion>
          <hashTree/>
        </hashTree>
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="8-ScheduleQuery">
          <stringProp name="HTTPSampler.path">/production/schedule/list</stringProp>
          <stringProp name="HTTPSampler.method">GET</stringProp>
        </HTTPSamplerProxy>
        <hashTree>
          <ResponseAssertion guiclass="AssertionGui" testclass="ResponseAssertion" testname="Assert Schedule">
            <collectionProp name="Asserion.test_strings">
              <stringProp name="49586">200</stringProp>
            </collectionProp>
            <stringProp name="Assertion.test_field">Assertion.response_code</stringProp>
          </ResponseAssertion>
          <hashTree/>
        </hashTree>
        <ResultCollector guiclass="SummaryReport" testclass="ResultCollector" testname="Summary Report">
          <stringProp name="filename">${RESULT_FILE}</stringProp>
        </ResultCollector>
        <hashTree/>
        <ResultCollector guiclass="AggregateReport" testclass="ResultCollector" testname="Aggregate Report"/>
        <hashTree/>
        <ResultCollector guiclass="GraphVisualizer" testclass="ResultCollector" testname="Graph Results"/>
        <hashTree/>
      </hashTree>
    </hashTree>
  </hashTree>
</jmeterTestPlan>
JMXEOF
}

run_test() {
    echo "[INFO] Generating JMeter test plan..."
    generate_test_plan

    mkdir -p results reports

    echo "[INFO] Starting JMeter performance test..."
    echo "[INFO] Threads: 100 | Ramp-up: 10s | Loops: 1"
    echo "[INFO] Result: $RESULT_FILE"
    echo "[INFO] Report: $REPORT_DIR"

    $JMETER_HOME/bin/jmeter -n -t $TEST_PLAN -l $RESULT_FILE -e -o $REPORT_DIR

    echo "[INFO] Test completed."
    echo "[INFO] Generating HTML report..."
    echo "[INFO] HTML report available at: $REPORT_DIR/index.html"
}

analyze_results() {
    if [ ! -f "$RESULT_FILE" ]; then
        echo "[ERROR] Result file not found: $RESULT_FILE"
        exit 1
    fi

    echo "=== Performance Summary ==="
    echo "File: $RESULT_FILE"

    $JMETER_HOME/bin/jmeter -g $RESULT_FILE -o $REPORT_DIR

    echo "=== Pass/Fail Count ==="
    awk -F, '{print $8}' $RESULT_FILE | sort | uniq -c

    echo "=== Response Time Stats ==="
    awk -F, 'NR>1 {sum+=$2; count++; if($2>max||max=="") max=$2; if(min==""||$2<min) min=$2} END {if(count>0) print "Min: "min"ms | Avg: "sum/count"ms | Max: "max"ms"}' $RESULT_FILE

    echo "=== Error Rate ==="
    total=$(awk -F, 'END{print NR-1}' $RESULT_FILE)
    errors=$(awk -F, 'NR>1 && $8!="OK" {count++} END{print count+0}' $RESULT_FILE)
    if [ "$total" -gt 0 ]; then
        rate=$(echo "scale=2; $errors*100/$total" | bc)
        echo "Error rate: $rate% ($errors/$total)"
    fi
}

case "${1:-run}" in
    run)
        run_test
        ;;
    analyze)
        analyze_results
        ;;
    clean)
        rm -rf results reports $TEST_PLAN
        echo "[INFO] Cleaned up test artifacts."
        ;;
    *)
        echo "Usage: $0 {run|analyze|clean}"
        echo "  run     - Generate test plan and execute performance test"
        echo "  analyze - Analyze existing result file"
        echo "  clean   - Remove test artifacts"
        exit 1
        ;;
esac
