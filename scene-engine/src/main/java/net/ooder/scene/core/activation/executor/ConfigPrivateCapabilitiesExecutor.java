package net.ooder.scene.core.activation.executor;

import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor;
import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor.StepResult;
import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 配置私有能力执行器
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class ConfigPrivateCapabilitiesExecutor implements EnhancedActivationStepExecutor {

    private static final Logger log = LoggerFactory.getLogger(ConfigPrivateCapabilitiesExecutor.class);

    public static final String STEP_TYPE = "config-private-capabilities";

    @Override
    public String getStepType() {
        return STEP_TYPE;
    }

    @Override
    public boolean canExecute(Map<String, Object> stepConfig) {
        return stepConfig != null && stepConfig.containsKey("capabilities");
    }

    @Override
    public StepResult execute(Map<String, Object> stepConfig, 
                               Map<String, Object> process, 
                               Map<String, Object> context) {
        try {
            List<String> capabilities = getCapabilities(stepConfig);
            String sceneGroupId = (String) process.get("sceneGroupId");
            String roleId = getConfigString(stepConfig, "roleId");

            log.info("Configuring private capabilities for scene: {} role: {} count: {}", 
                    sceneGroupId, roleId, capabilities.size());

            StepResult result = StepResult.success("Private capabilities configured: " + capabilities.size());
            Map<String, Object> data = new HashMap<>();
            data.put("sceneGroupId", sceneGroupId);
            data.put("roleId", roleId);
            data.put("capabilities", capabilities);
            data.put("configuredAt", System.currentTimeMillis());
            result.setData(data);
            return result;
        } catch (Exception e) {
            log.error("Failed to configure private capabilities", e);
            return StepResult.failure("Failed to configure private capabilities: " + e.getMessage());
        }
    }

    @Override
    public ValidationResult validateInput(Map<String, Object> stepConfig,
                                           Map<String, Object> input) {
        List<String> errors = new ArrayList<>();

        if (!stepConfig.containsKey("capabilities")) {
            errors.add("Missing required field: capabilities");
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
        log.info("Rolling back private capabilities for scene: {}", process.get("sceneGroupId"));
    }

    @SuppressWarnings("unchecked")
    private List<String> getCapabilities(Map<String, Object> stepConfig) {
        Object caps = stepConfig.get("capabilities");
        if (caps instanceof List) {
            return (List<String>) caps;
        }
        return Collections.emptyList();
    }

    protected String getConfigString(Map<String, Object> stepConfig, String key) {
        Object value = stepConfig != null ? stepConfig.get(key) : null;
        return value != null ? value.toString() : null;
    }
}
