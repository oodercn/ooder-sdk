package net.ooder.sdk.llm.activation;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 激活上下文
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationContext {

    private String sceneId;
    private String templateId;
    private String userId;
    private String roleId;
    private String currentStepId;
    private Map<String, Object> stepData;
    private List<String> completedSteps;
    private Map<String, Object> userProfile;
    private Map<String, Object> templateConfig;

    public boolean isStepCompleted(String stepId) {
        return completedSteps != null && completedSteps.contains(stepId);
    }

    public Object getStepData(String key) {
        return stepData != null ? stepData.get(key) : null;
    }

    public void addCompletedStep(String stepId) {
        if (completedSteps == null) {
            completedSteps = new java.util.ArrayList<>();
        }
        if (!completedSteps.contains(stepId)) {
            completedSteps.add(stepId);
        }
    }
}
