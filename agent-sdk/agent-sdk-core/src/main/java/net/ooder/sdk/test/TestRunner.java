package net.ooder.sdk.test;

import net.ooder.sdk.validator.ScenePackage;

public interface TestRunner {
    
    TestReport runTests(ScenePackage scene, TestType type);
    
    TestReport runTest(ScenePackage scene, String testName);
    
    TestReport runTestsFromYaml(ScenePackage scene, String yamlPath);
}