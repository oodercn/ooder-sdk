package net.ooder.scene.skill.rule;

import java.util.List;
import java.util.Map;

/**
 * LLM 规则生成器接口
 * 
 * <p>利用 LLM 动态生成决策规则并持久化到规则引擎</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public interface LlmRuleGenerator {

    /**
     * 根据对话生成规则
     *
     * @param sceneId 场景ID
     * @param conversation 对话内容
     * @param context 上下文信息
     * @return 生成的规则
     */
    RuleScript generateRule(String sceneId, String conversation, Map<String, Object> context);

    /**
     * 根据意图生成规则
     *
     * @param sceneId 场景ID
     * @param intent 用户意图
     * @param examples 示例对话
     * @return 生成的规则
     */
    RuleScript generateRuleFromIntent(String sceneId, String intent, List<String> examples);

    /**
     * 优化现有规则
     *
     * @param rule 现有规则
     * @param feedback 反馈信息
     * @return 优化后的规则
     */
    RuleScript optimizeRule(RuleScript rule, String feedback);

    /**
     * 验证规则有效性
     *
     * @param rule 规则
     * @return 验证结果
     */
    RuleValidationResult validateRule(RuleScript rule);

    /**
     * 测试规则
     *
     * @param rule 规则
     * @param testCases 测试用例
     * @return 测试结果
     */
    RuleTestResult testRule(RuleScript rule, List<Map<String, Object>> testCases);

    /**
     * 获取生成器名称
     *
     * @return 名称
     */
    String getName();

    /**
     * 获取生成器版本
     *
     * @return 版本
     */
    String getVersion();

    /**
     * 规则验证结果
     */
    class RuleValidationResult {
        private boolean valid;
        private String errorMessage;
        private List<String> warnings;
        private Map<String, Object> suggestions;

        public RuleValidationResult() {
            this.warnings = new java.util.ArrayList<>();
            this.suggestions = new java.util.HashMap<>();
        }

        public static RuleValidationResult valid() {
            RuleValidationResult result = new RuleValidationResult();
            result.setValid(true);
            return result;
        }

        public static RuleValidationResult invalid(String errorMessage) {
            RuleValidationResult result = new RuleValidationResult();
            result.setValid(false);
            result.setErrorMessage(errorMessage);
            return result;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public List<String> getWarnings() {
            return warnings;
        }

        public void setWarnings(List<String> warnings) {
            this.warnings = warnings;
        }

        public void addWarning(String warning) {
            this.warnings.add(warning);
        }

        public Map<String, Object> getSuggestions() {
            return suggestions;
        }

        public void setSuggestions(Map<String, Object> suggestions) {
            this.suggestions = suggestions;
        }
    }

    /**
     * 规则测试结果
     */
    class RuleTestResult {
        private boolean passed;
        private int totalCases;
        private int passedCases;
        private int failedCases;
        private List<TestCaseResult> caseResults;
        private String summary;

        public RuleTestResult() {
            this.caseResults = new java.util.ArrayList<>();
        }

        public boolean isPassed() {
            return passed;
        }

        public void setPassed(boolean passed) {
            this.passed = passed;
        }

        public int getTotalCases() {
            return totalCases;
        }

        public void setTotalCases(int totalCases) {
            this.totalCases = totalCases;
        }

        public int getPassedCases() {
            return passedCases;
        }

        public void setPassedCases(int passedCases) {
            this.passedCases = passedCases;
        }

        public int getFailedCases() {
            return failedCases;
        }

        public void setFailedCases(int failedCases) {
            this.failedCases = failedCases;
        }

        public List<TestCaseResult> getCaseResults() {
            return caseResults;
        }

        public void setCaseResults(List<TestCaseResult> caseResults) {
            this.caseResults = caseResults;
        }

        public void addCaseResult(TestCaseResult result) {
            this.caseResults.add(result);
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }
    }

    /**
     * 测试用例结果
     */
    class TestCaseResult {
        private Map<String, Object> input;
        private Object expectedOutput;
        private Object actualOutput;
        private boolean passed;
        private String message;

        public TestCaseResult() {
        }

        public Map<String, Object> getInput() {
            return input;
        }

        public void setInput(Map<String, Object> input) {
            this.input = input;
        }

        public Object getExpectedOutput() {
            return expectedOutput;
        }

        public void setExpectedOutput(Object expectedOutput) {
            this.expectedOutput = expectedOutput;
        }

        public Object getActualOutput() {
            return actualOutput;
        }

        public void setActualOutput(Object actualOutput) {
            this.actualOutput = actualOutput;
        }

        public boolean isPassed() {
            return passed;
        }

        public void setPassed(boolean passed) {
            this.passed = passed;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
