package net.ooder.scene.core.activation.executor;

import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor;
import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor.StepResult;
import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 配置工作流执行器
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class ConfigWorkflowExecutor implements EnhancedActivationStepExecutor {

    private static final Logger log = LoggerFactory.getLogger(ConfigWorkflowExecutor.class);

    public static final String STEP_TYPE = "config-workflow";

    @Override
    public String getStepType() {
        return STEP_TYPE;
    }

    @Override
    public boolean canExecute(Map<String, Object> stepConfig) {
        return stepConfig != null && stepConfig.containsKey("workflowId");
    }

    @Override
    public StepResult execute(Map<String, Object> stepConfig, 
                               Map<String, Object> process, 
                               Map<String, Object> context) {
        try {
            String workflowId = getConfigString(stepConfig, "workflowId");
            String workflowName = getConfigString(stepConfig, "workflowName");
            Map<String, Object> workflowConfig = getWorkflowConfig(stepConfig);
            String sceneGroupId = (String) process.get("sceneGroupId");

            log.info("Configuring workflow for scene: {} workflow: {}", sceneGroupId, workflowId);

            StepResult result = StepResult.success("Workflow configured: " + workflowId);
            Map<String, Object> data = new HashMap<>();
            data.put("sceneGroupId", sceneGroupId);
            data.put("workflowId", workflowId);
            data.put("workflowName", workflowName);
            data.put("workflowConfig", workflowConfig);
            data.put("configuredAt", System.currentTimeMillis());
            result.setData(data);
            return result;
        } catch (Exception e) {
            log.error("Failed to configure workflow", e);
            return StepResult.failure("Failed to configure workflow: " + e.getMessage());
        }
    }

    @Override
    public ValidationResult validateInput(Map<String, Object> stepConfig,
                                           Map<String, Object> input) {
        List<String> errors = new ArrayList<>();

        if (!stepConfig.containsKey("workflowId")) {
            errors.add("Missing required field: workflowId");
        }

        if (!errors.isEmpty()) {
            return ValidationResult.failure(errors);
        }

        return ValidationResult.success();
    }

    @Override
    public boolean supportsRollback() {
        return true;
    }

    @Override
    public void rollback(Map<String, Object> stepConfig, 
                          Map<String, Object> process, 
                          Map<String, Object> context) {
        log.info("Rolling back workflow configuration for scene: {}", process.get("sceneGroupId"));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getWorkflowConfig(Map<String, Object> stepConfig) {
        Object config = stepConfig.get("config");
        if (config instanceof Map) {
            return (Map<String, Object>) config;
        }
        return Collections.emptyMap();
    }

    protected String getConfigString(Map<String, Object> stepConfig, String key) {
        Object value = stepConfig != null ? stepConfig.get(key) : null;
        return value != null ? value.toString() : null;
    }
}
