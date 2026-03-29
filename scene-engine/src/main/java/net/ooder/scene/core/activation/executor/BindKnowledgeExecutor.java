package net.ooder.scene.core.activation.executor;

import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor;
import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor.StepResult;
import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 绑定知识库执行器
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class BindKnowledgeExecutor implements EnhancedActivationStepExecutor {

    private static final Logger log = LoggerFactory.getLogger(BindKnowledgeExecutor.class);

    public static final String STEP_TYPE = "bind-knowledge";

    @Override
    public String getStepType() {
        return STEP_TYPE;
    }

    @Override
    public boolean canExecute(Map<String, Object> stepConfig) {
        return stepConfig != null && stepConfig.containsKey("knowledgeBaseIds");
    }

    @Override
    public StepResult execute(Map<String, Object> stepConfig, 
                               Map<String, Object> process, 
                               Map<String, Object> context) {
        try {
            List<String> knowledgeBaseIds = getKnowledgeBaseIds(stepConfig);
            String sceneGroupId = (String) process.get("sceneGroupId");
            String roleId = getConfigString(stepConfig, "roleId");

            log.info("Binding knowledge bases for scene: {} role: {} kb: {}", 
                    sceneGroupId, roleId, knowledgeBaseIds);

            StepResult result = StepResult.success("Knowledge bases bound: " + knowledgeBaseIds.size());
            Map<String, Object> data = new HashMap<>();
            data.put("sceneGroupId", sceneGroupId);
            data.put("roleId", roleId);
            data.put("knowledgeBaseIds", knowledgeBaseIds);
            data.put("boundAt", System.currentTimeMillis());
            result.setData(data);
            return result;
        } catch (Exception e) {
            log.error("Failed to bind knowledge bases", e);
            return StepResult.failure("Failed to bind knowledge bases: " + e.getMessage());
        }
    }

    @Override
    public ValidationResult validateInput(Map<String, Object> stepConfig,
                                           Map<String, Object> input) {
        List<String> errors = new ArrayList<>();

        if (!stepConfig.containsKey("knowledgeBaseIds")) {
            errors.add("Missing required field: knowledgeBaseIds");
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
        log.info("Rolling back knowledge binding for scene: {}", process.get("sceneGroupId"));
    }

    @SuppressWarnings("unchecked")
    private List<String> getKnowledgeBaseIds(Map<String, Object> stepConfig) {
        Object kbIds = stepConfig.get("knowledgeBaseIds");
        if (kbIds instanceof List) {
            return (List<String>) kbIds;
        }
        return Collections.emptyList();
    }

    protected String getConfigString(Map<String, Object> stepConfig, String key) {
        Object value = stepConfig != null ? stepConfig.get(key) : null;
        return value != null ? value.toString() : null;
    }
}
