package net.ooder.sdk.will;

/**
 * 步骤执行器接口
 * 用于执行意志转换后的具体执行步骤
 */
public interface StepExecutor {
    
    /**
     * 获取执行器支持的操作类型
     * @return 操作类型标识
     */
    String getActionType();
    
    /**
     * 执行步骤
     * @param will 意志表达式
     * @param step 执行步骤
     * @param context 执行上下文
     * @return 执行是否成功
     */
    boolean execute(WillExpression will, WillTransformer.ExecutionStep step, ExecutionContext context);
    
    /**
     * 验证步骤参数是否有效
     * @param step 执行步骤
     * @return 是否有效
     */
    default boolean validate(WillTransformer.ExecutionStep step) {
        return step != null && step.getAction() != null;
    }
    
    /**
     * 执行上下文
     */
    class ExecutionContext {
        private String executionId;
        private String willId;
        private String currentStepId;
        private java.util.Map<String, Object> parameters;
        private java.util.Map<String, Object> results;
        
        public ExecutionContext(String executionId, String willId) {
            this.executionId = executionId;
            this.willId = willId;
            this.parameters = new java.util.HashMap<>();
            this.results = new java.util.HashMap<>();
        }
        
        public String getExecutionId() { return executionId; }
        public String getWillId() { return willId; }
        public String getCurrentStepId() { return currentStepId; }
        public void setCurrentStepId(String stepId) { this.currentStepId = stepId; }
        public java.util.Map<String, Object> getParameters() { return parameters; }
        public java.util.Map<String, Object> getResults() { return results; }
        
        public void setParameter(String key, Object value) {
            parameters.put(key, value);
        }
        
        public Object getParameter(String key) {
            return parameters.get(key);
        }
        
        public void setResult(String key, Object value) {
            results.put(key, value);
        }
        
        public Object getResult(String key) {
            return results.get(key);
        }
    }
}
