package net.ooder.scene.core.activation.executor;

import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor;
import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor.StepResult;
import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 配置条件执行器
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class ConfigConditionsExecutor implements EnhancedActivationStepExecutor {

    private static final Logger log = LoggerFactory.getLogger(ConfigConditionsExecutor.class);

    public static final String STEP_TYPE = "config-conditions";

    @Override
    public String getStepType() {
        return STEP_TYPE;
    }

    @Override
    public boolean canExecute(Map<String, Object> stepConfig) {
        return stepConfig != null && stepConfig.containsKey("conditions");
    }

    @Override
    public StepResult execute(Map<String, Object> stepConfig, 
                               Map<String, Object> process, 
                               Map<String, Object> context) {
        try {
            List<Map<String, Object>> conditions = getConditions(stepConfig);
            String sceneGroupId = (String) process.get("sceneGroupId");

            log.info("Configuring conditions for scene: {} count: {}", sceneGroupId, conditions.size());

            StepResult result = StepResult.success("Conditions configured: " + conditions.size());
            Map<String, Object> data = new HashMap<>();
            data.put("sceneGroupId", sceneGroupId);
            data.put("conditions", conditions);
            data.put("configuredAt", System.currentTimeMillis());
            result.setData(data);
            return result;
        } catch (Exception e) {
            log.error("Failed to configure conditions", e);
            return StepResult.failure("Failed to configure conditions: " + e.getMessage());
        }
    }

    @Override
    public ValidationResult validateInput(Map<String, Object> stepConfig,
                                           Map<String, Object> input) {
        List<String> errors = new ArrayList<>();

        if (!stepConfig.containsKey("conditions")) {
            errors.add("Missing required field: conditions");
        }

        if (!errors.isEmpty()) {
            return ValidationResult.failure(errors);
        }

        return ValidationResult.success();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getConditions(Map<String, Object> stepConfig) {
        Object conditions = stepConfig.get("conditions");
        if (conditions instanceof List) {
            return (List<Map<String, Object>>) conditions;
        }
        return Collections.emptyList();
    }
}
