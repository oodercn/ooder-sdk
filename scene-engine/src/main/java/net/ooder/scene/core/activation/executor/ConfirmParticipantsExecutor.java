package net.ooder.scene.core.activation.executor;

import net.ooder.scene.core.spi.user.UserService;
import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor;
import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor.StepResult;
import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 确认参与者执行器
 *
 * <p>支持用户存在性验证，需要通过ServiceLocator注入UserService。</p>
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class ConfirmParticipantsExecutor implements EnhancedActivationStepExecutor {

    private static final Logger log = LoggerFactory.getLogger(ConfirmParticipantsExecutor.class);

    public static final String STEP_TYPE = "confirm-participants";

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public UserService getUserService() {
        return userService;
    }

    @Override
    public String getStepType() {
        return STEP_TYPE;
    }

    @Override
    public boolean canExecute(Map<String, Object> stepConfig) {
        return stepConfig != null && stepConfig.containsKey("roles");
    }

    @Override
    public StepResult execute(Map<String, Object> stepConfig, 
                               Map<String, Object> process, 
                               Map<String, Object> context) {
        try {
            List<String> participantIds = getParticipantIds(stepConfig);
            String sceneGroupId = (String) process.get("sceneGroupId");
            String userId = (String) context.get("userId");

            log.info("Confirming participants for scene: {} with participants: {}", sceneGroupId, participantIds);

            if (participantIds.isEmpty()) {
                return StepResult.failure(
                    ExecutorErrorCodes.PARTICIPANTS_REQUIRED + ": 未选择参与者"
                );
            }

            if (userService != null) {
                List<String> invalidUsers = userService.validateUsers(participantIds);
                if (!invalidUsers.isEmpty()) {
                    log.warn("Invalid participants found: {}", invalidUsers);
                    StepResult result = StepResult.failure(
                        ExecutorErrorCodes.PARTICIPANTS_NOT_FOUND + ": 以下用户不存在: " + String.join(", ", invalidUsers)
                    );
                    result.setData(Map.of("invalidUsers", invalidUsers));
                    return result;
                }
            }

            StepResult result = StepResult.success("Participants confirmed: " + participantIds.size() + " participants");
            Map<String, Object> data = new HashMap<>();
            data.put("sceneGroupId", sceneGroupId);
            data.put("confirmedBy", userId);
            data.put("confirmedAt", System.currentTimeMillis());
            data.put("participantIds", participantIds);
            result.setData(data);
            return result;
        } catch (Exception e) {
            log.error("Failed to confirm participants", e);
            return StepResult.failure(
                ExecutorErrorCodes.EXECUTION_ERROR + ": Failed to confirm participants: " + e.getMessage()
            );
        }
    }

    @Override
    public ValidationResult validateInput(Map<String, Object> stepConfig,
                                           Map<String, Object> input) {
        List<String> errors = new ArrayList<>();

        if (!stepConfig.containsKey("participantIds") && !stepConfig.containsKey("roles")) {
            errors.add("Missing required field: participantIds or roles");
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
        log.info("Rolling back participant confirmation for scene: {}", 
                process.get("sceneGroupId"));
    }

    @SuppressWarnings("unchecked")
    private List<String> getParticipantIds(Map<String, Object> stepConfig) {
        Object participantIds = stepConfig.get("participantIds");
        if (participantIds instanceof List) {
            return (List<String>) participantIds;
        }
        Object roles = stepConfig.get("roles");
        if (roles instanceof List) {
            return (List<String>) roles;
        }
        return Collections.emptyList();
    }
}
