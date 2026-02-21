package net.ooder.sdk.cli;

import net.ooder.sdk.test.TestRunner;
import net.ooder.sdk.test.impl.TestRunnerImpl;
import net.ooder.sdk.test.TestReport;
import net.ooder.sdk.test.TestType;
import net.ooder.sdk.validator.ScenePackage;

public class TestCommand implements CliCommand {
    
    private TestType type = null;
    private String testName = null;
    private String path = ".";
    
    public TestCommand(String[] args) {
        parseArgs(args);
    }
    
    private void parseArgs(String[] args) {
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--type")) {
                type = TestType.valueOf(args[i + 1].toUpperCase());
                i++;
            } else if (args[i].equals("--test")) {
                testName = args[i + 1];
                i++;
            } else if (args[i].equals("--path")) {
                path = args[i + 1];
                i++;
            }
        }
    }
    
    @Override
    public void execute() {
        try {
            ScenePackage scene = ScenePackage.load(path);
            TestRunner runner = new TestRunnerImpl();
            
            TestReport report;
            
            if (testName != null) {
                report = runner.runTest(scene, testName);
            } else if (type != null) {
                report = runner.runTests(scene, type);
            } else {
                report = runner.runTests(scene, TestType.UNIT);
                TestReport contract = runner.runTests(scene, TestType.CONTRACT);
                TestReport integration = runner.runTests(scene, TestType.INTEGRATION);
                // 这里应该实现报告合并逻辑
            }
            
            printReport(report);
            
            if (report.getFailed() > 0) {
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private void printReport(TestReport report) {
        System.out.println("Test Report:");
        System.out.println("============");
        System.out.println("Total: " + report.getTotal());
        System.out.println("Passed: " + report.getPassed());
        System.out.println("Failed: " + report.getFailed());
        System.out.println("Skipped: " + report.getSkipped());
        System.out.println("Duration: " + report.getDuration() + "ms");
        System.out.println("Coverage: " + report.getCoverage() + "%");
    }
}