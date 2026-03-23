package net.ooder.sdk.llm.activation;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 填充建议
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Suggestion {

    private String fieldPath;
    private Object suggestedValue;
    private String suggestionText;
    private double confidence;
    private SuggestionSource source;
    private Map<String, Object> metadata;

    public enum SuggestionSource {
        USER_HISTORY,
        TEMPLATE_DEFAULT,
        LLM_INFERENCE,
        SYSTEM_RULE,
        EXTERNAL_API
    }

    public static Suggestion fromHistory(String fieldPath, Object value, double confidence) {
        return Suggestion.builder()
                .fieldPath(fieldPath)
                .suggestedValue(value)
                .source(SuggestionSource.USER_HISTORY)
                .confidence(confidence)
                .build();
    }

    public static Suggestion fromTemplate(String fieldPath, Object value) {
        return Suggestion.builder()
                .fieldPath(fieldPath)
                .suggestedValue(value)
                .source(SuggestionSource.TEMPLATE_DEFAULT)
                .confidence(1.0)
                .build();
    }

    public static Suggestion fromLlm(String fieldPath, Object value, String text, double confidence) {
        return Suggestion.builder()
                .fieldPath(fieldPath)
                .suggestedValue(value)
                .suggestionText(text)
                .source(SuggestionSource.LLM_INFERENCE)
                .confidence(confidence)
                .build();
    }
}
