package net.ooder.sdk.test.impl;

import net.ooder.sdk.test.*;
import net.ooder.sdk.validator.ScenePackage;
import net.ooder.sdk.validator.Status;

import java.util.ArrayList;
import java.util.List;

public class TestRunnerImpl implements TestRunner {
    
    @Override
    public TestReport runTests(ScenePackage scene, TestType type) {
        TestReport report = new TestReport();
        report.setTimestamp(System.currentTimeMillis());
        
        List<TestResult> results = new ArrayList<>();
        int passed = 0, failed = 0, skipped = 0;
        
        // 这里应该实现实际的测试运行逻辑
        // 暂时返回一个空的测试报告
        
        report.setTotal(results.size());
        report.setPassed(passed);
        report.setFailed(failed);
        report.setSkipped(skipped);
        report.setResults(results);
        report.setDuration(System.currentTimeMillis() - report.getTimestamp());
        
        return report;
    }
    
    @Override
    public TestReport runTest(ScenePackage scene, String testName) {
        TestReport report = new TestReport();
        report.setTimestamp(System.currentTimeMillis());
        
        List<TestResult> results = new ArrayList<>();
        
        // 这里应该实现运行单个测试的逻辑
        // 暂时返回一个空的测试报告
        
        report.setTotal(results.size());
        report.setPassed((int) results.stream().filter(r -> r.getStatus() == Status.PASS).count());
        report.setFailed((int) results.stream().filter(r -> r.getStatus() == Status.FAIL).count());
        report.setSkipped((int) results.stream().filter(r -> r.getStatus() == Status.SKIP).count());
        report.setResults(results);
        report.setDuration(System.currentTimeMillis() - report.getTimestamp());
        
        return report;
    }
    
    @Override
    public TestReport runTestsFromYaml(ScenePackage scene, String yamlPath) {
        TestReport report = new TestReport();
        report.setTimestamp(System.currentTimeMillis());
        
        List<TestResult> results = new ArrayList<>();
        
        // 这里应该实现从YAML文件加载测试用例并运行的逻辑
        // 暂时返回一个空的测试报告
        
        report.setTotal(results.size());
        report.setPassed((int) results.stream().filter(r -> r.getStatus() == Status.PASS).count());
        report.setFailed((int) results.stream().filter(r -> r.getStatus() == Status.FAIL).count());
        report.setSkipped((int) results.stream().filter(r -> r.getStatus() == Status.SKIP).count());
        report.setResults(results);
        report.setDuration(System.currentTimeMillis() - report.getTimestamp());
        
        return report;
    }
}