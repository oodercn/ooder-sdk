package net.ooder.sdk.llm.activation.impl;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.llm.activation.*;
import net.ooder.sdk.llm.output.StructuredOutputApi;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 激活引导服务实现
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Slf4j
public class ActivationGuidanceServiceImpl implements ActivationGuidanceService {

    private final StructuredOutputApi structuredOutputApi;
    private final Map<String, StepDefinition> stepDefinitions = new ConcurrentHashMap<>();

    public ActivationGuidanceServiceImpl(StructuredOutputApi structuredOutputApi) {
        this.structuredOutputApi = structuredOutputApi;
    }

    @Override
    public GuidanceResult getNextStepGuidance(ActivationContext context) {
        if (context == null) {
            return GuidanceResult.completed("No context provided");
        }

        String currentStepId = context.getCurrentStepId();
        if (currentStepId == null) {
            return GuidanceResult.completed("Activation completed");
        }

        StepDefinition stepDef = stepDefinitions.get(currentStepId);
        if (stepDef == null) {
            return GuidanceResult.inputRequired("Please provide input for step: " + currentStepId, currentStepId);
        }

        List<Suggestion> suggestions = generateSuggestionsForStep(stepDef, context);
        
        if (stepDef.getType() == StepType.SELECTION) {
            List<GuidanceResult.Option> options = getRecommendedOptions(currentStepId, context);
            return GuidanceResult.selectionRequired(stepDef.getGuidanceText(), options, stepDef.getNextStepId());
        }

        if (stepDef.isRequiresConfirmation()) {
            return GuidanceResult.confirmationRequired(stepDef.getGuidanceText(), stepDef.getNextStepId());
        }

        Map<String, Object> suggestionMap = new HashMap<>();
        for (Suggestion suggestion : suggestions) {
            suggestionMap.put(suggestion.getFieldPath(), suggestion.getSuggestedValue());
        }

        return GuidanceResult.builder()
                .guidanceText(stepDef.getGuidanceText())
                .suggestions(suggestionMap)
                .nextStepId(stepDef.getNextStepId())
                .guidanceType(GuidanceResult.GuidanceType.INPUT_REQUIRED)
                .build();
    }

    @Override
    public ValidationResult validateUserInput(String stepId, Object userInput) {
        StepDefinition stepDef = stepDefinitions.get(stepId);
        if (stepDef == null) {
            return ValidationResult.valid();
        }

        List<String> errors = new ArrayList<>();

        if (stepDef.isRequired() && userInput == null) {
            errors.add("Input is required for step: " + stepId);
        }

        if (userInput != null && stepDef.getValidationRules() != null) {
            for (ValidationRule rule : stepDef.getValidationRules()) {
                if (!rule.validate(userInput)) {
                    errors.add(rule.getErrorMessage());
                }
            }
        }

        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }

    @Override
    public List<Suggestion> generateSuggestions(String fieldPath, ActivationContext context) {
        List<Suggestion> suggestions = new ArrayList<>();

        if (context.getUserProfile() != null) {
            Object historyValue = context.getUserProfile().get(fieldPath);
            if (historyValue != null) {
                suggestions.add(Suggestion.fromHistory(fieldPath, historyValue, 0.8));
            }
        }

        if (context.getTemplateConfig() != null) {
            Object defaultValue = context.getTemplateConfig().get(fieldPath);
            if (defaultValue != null) {
                suggestions.add(Suggestion.fromTemplate(fieldPath, defaultValue));
            }
        }

        return suggestions;
    }

    @Override
    public String getStepGuidanceText(String stepId, ActivationContext context) {
        StepDefinition stepDef = stepDefinitions.get(stepId);
        if (stepDef != null) {
            return stepDef.getGuidanceText();
        }
        return "Please complete this step";
    }

    @Override
    public boolean canSkipStep(String stepId, ActivationContext context) {
        StepDefinition stepDef = stepDefinitions.get(stepId);
        if (stepDef == null) {
            return true;
        }

        if (stepDef.isRequired()) {
            return false;
        }

        if (stepDef.getSkipCondition() != null) {
            return stepDef.getSkipCondition().test(context);
        }

        return true;
    }

    @Override
    public List<GuidanceResult.Option> getRecommendedOptions(String stepId, ActivationContext context) {
        StepDefinition stepDef = stepDefinitions.get(stepId);
        if (stepDef == null || stepDef.getOptions() == null) {
            return Collections.emptyList();
        }

        List<GuidanceResult.Option> recommendedOptions = new ArrayList<>();
        for (GuidanceResult.Option option : stepDef.getOptions()) {
            if (isOptionRecommended(option, context)) {
                recommendedOptions.add(option);
            }
        }

        return recommendedOptions;
    }

    public void registerStepDefinition(String stepId, StepDefinition stepDef) {
        stepDefinitions.put(stepId, stepDef);
    }

    private List<Suggestion> generateSuggestionsForStep(StepDefinition stepDef, ActivationContext context) {
        List<Suggestion> suggestions = new ArrayList<>();
        
        for (String fieldPath : stepDef.getFieldPaths()) {
            suggestions.addAll(generateSuggestions(fieldPath, context));
        }
        
        return suggestions;
    }

    private boolean isOptionRecommended(GuidanceResult.Option option, ActivationContext context) {
        if (option.isRecommended()) {
            return true;
        }

        if (context.getUserProfile() != null && option.getMetadata() != null) {
            String requiredRole = (String) option.getMetadata().get("requiredRole");
            if (requiredRole != null) {
                String userRole = (String) context.getUserProfile().get("role");
                return requiredRole.equals(userRole);
            }
        }

        return false;
    }

    public enum StepType {
        INPUT,
        SELECTION,
        CONFIRMATION,
        INFORMATION
    }

    @lombok.Data
    @lombok.Builder
    public static class StepDefinition {
        private String stepId;
        private String nextStepId;
        private String guidanceText;
        private StepType type;
        private boolean required;
        private boolean requiresConfirmation;
        private List<String> fieldPaths;
        private List<ValidationRule> validationRules;
        private List<GuidanceResult.Option> options;
        private java.util.function.Predicate<ActivationContext> skipCondition;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class ValidationRule {
        private java.util.function.Predicate<Object> validator;
        private String errorMessage;

        public boolean validate(Object input) {
            return validator.test(input);
        }
    }
}
