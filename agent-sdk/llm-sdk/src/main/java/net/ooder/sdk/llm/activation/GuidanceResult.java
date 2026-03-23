package net.ooder.sdk.llm.activation;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 引导结果
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuidanceResult {

    private String guidanceText;
    private List<Option> availableOptions;
    private Map<String, Object> suggestions;
    private boolean requiresConfirmation;
    private String nextStepId;
    private GuidanceType guidanceType;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Option {
        private String id;
        private String label;
        private String description;
        private Map<String, Object> metadata;
        private boolean recommended;
    }

    public enum GuidanceType {
        INPUT_REQUIRED,
        SELECTION_REQUIRED,
        CONFIRMATION_REQUIRED,
        AUTO_FILL_AVAILABLE,
        COMPLETED
    }

    public static GuidanceResult inputRequired(String guidanceText, String nextStepId) {
        return GuidanceResult.builder()
                .guidanceText(guidanceText)
                .nextStepId(nextStepId)
                .guidanceType(GuidanceType.INPUT_REQUIRED)
                .build();
    }

    public static GuidanceResult selectionRequired(String guidanceText, List<Option> options, String nextStepId) {
        return GuidanceResult.builder()
                .guidanceText(guidanceText)
                .availableOptions(options)
                .nextStepId(nextStepId)
                .guidanceType(GuidanceType.SELECTION_REQUIRED)
                .build();
    }

    public static GuidanceResult confirmationRequired(String guidanceText, String nextStepId) {
        return GuidanceResult.builder()
                .guidanceText(guidanceText)
                .requiresConfirmation(true)
                .nextStepId(nextStepId)
                .guidanceType(GuidanceType.CONFIRMATION_REQUIRED)
                .build();
    }

    public static GuidanceResult completed(String guidanceText) {
        return GuidanceResult.builder()
                .guidanceText(guidanceText)
                .guidanceType(GuidanceType.COMPLETED)
                .build();
    }
}
