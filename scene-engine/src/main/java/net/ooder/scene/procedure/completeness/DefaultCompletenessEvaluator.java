package net.ooder.scene.procedure.completeness;

import net.ooder.sdk.api.completeness.*;
import net.ooder.sdk.api.procedure.EnterpriseProcedure;
import net.ooder.sdk.api.procedure.CompletenessSuggestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 默认完善度评估器实现
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class DefaultCompletenessEvaluator implements CompletenessEvaluator {

    private List<CompletenessDimensionConfig> dimensionConfigs;

    public DefaultCompletenessEvaluator() {
        this.dimensionConfigs = createDefaultDimensionConfigs();
    }

    @Override
    public CompletenessDetail evaluate(EnterpriseProcedure procedure) {
        List<CompletenessDimension> dimensions = new ArrayList<>();
        List<CompletenessIssue> issues = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        int totalScore = 0;
        int totalWeight = 0;

        for (CompletenessDimensionConfig config : dimensionConfigs) {
            CompletenessDimension dimension = evaluateDimension(procedure, config);
            dimensions.add(dimension);
            totalScore += dimension.getScore() * config.getWeight();
            totalWeight += config.getWeight();

            for (String missingItem : dimension.getMissingItems()) {
                CompletenessIssue issue = new CompletenessIssueEntity();
                issue.setDimension(dimension.getName());
                issue.setDescription(dimension.getName() + " 缺失: " + missingItem);
                issue.setSeverity(dimension.getScore() < 50 ? IssueSeverity.CRITICAL : IssueSeverity.WARNING);
                issue.setSuggestion("请补充 " + missingItem);
                issues.add(issue);
            }
        }

        int overallScore = totalWeight > 0 ? totalScore / totalWeight : 0;

        if (overallScore < 60) {
            suggestions.add("建议完善基础信息，提高流程完整性");
        }
        if (overallScore < 80) {
            suggestions.add("建议添加更多约束规则，增强流程规范性");
        }
        if (overallScore < 100) {
            suggestions.add("建议关联知识库，提升流程可追溯性");
        }

        CompletenessDetailEntity detail = new CompletenessDetailEntity();
        detail.setOverallScore(overallScore);
        detail.setDimensions(dimensions);
        detail.setIssues(issues);
        detail.setSuggestions(suggestions);

        return detail;
    }

    private CompletenessDimension evaluateDimension(EnterpriseProcedure procedure, CompletenessDimensionConfig config) {
        List<String> checkedItems = new ArrayList<>();
        List<String> missingItems = new ArrayList<>();
        int score = 0;
        int totalItems = config.getCheckItems().size();

        for (CompletenessCheckItem item : config.getCheckItems()) {
            boolean passed = checkItem(procedure, item);
            if (passed) {
                checkedItems.add(item.getName());
                score += item.getScore();
            } else {
                missingItems.add(item.getName());
            }
        }

        int dimensionScore = totalItems > 0 ? (score * 100) / (totalItems * 100 / totalItems) : 0;
        dimensionScore = Math.min(100, Math.max(0, dimensionScore));

        String status = "MISSING";
        if (dimensionScore >= 80) {
            status = "COMPLETE";
        } else if (dimensionScore >= 40) {
            status = "PARTIAL";
        }

        CompletenessDimensionEntity dimension = new CompletenessDimensionEntity();
        dimension.setName(config.getName());
        dimension.setWeight(config.getWeight());
        dimension.setScore(dimensionScore);
        dimension.setStatus(status);
        dimension.setCheckedItems(checkedItems);
        dimension.setMissingItems(missingItems);

        return dimension;
    }

    private boolean checkItem(EnterpriseProcedure procedure, CompletenessCheckItem item) {
        String field = item.getCheckExpression();
        
        if (field == null) {
            return true;
        }

        return switch (field) {
            case "name" -> procedure.getName() != null && !procedure.getName().isEmpty();
            case "description" -> procedure.getDescription() != null && !procedure.getDescription().isEmpty();
            case "category" -> procedure.getCategory() != null && !procedure.getCategory().isEmpty();
            case "tags" -> procedure.getTags() != null && !procedure.getTags().isEmpty();
            case "roles" -> procedure.getRoles() != null && !procedure.getRoles().isEmpty();
            case "steps" -> procedure.getSteps() != null && !procedure.getSteps().isEmpty();
            case "rules" -> procedure.getRules() != null && !procedure.getRules().isEmpty();
            case "requiredCapabilities" -> procedure.getRequiredCapabilities() != null && 
                    !procedure.getRequiredCapabilities().isEmpty();
            case "knowledgeBaseIds" -> procedure.getKnowledgeBaseIds() != null && 
                    !procedure.getKnowledgeBaseIds().isEmpty();
            case "organizationId" -> procedure.getOrganizationId() != null && 
                    !procedure.getOrganizationId().isEmpty();
            default -> true;
        };
    }

    public List<CompletenessSuggestion> getSuggestions(EnterpriseProcedure procedure) {
        List<CompletenessSuggestion> suggestions = new ArrayList<>();
        CompletenessDetail detail = evaluate(procedure);

        for (CompletenessIssue issue : detail.getIssues()) {
            CompletenessSuggestionEntity suggestion = new CompletenessSuggestionEntity();
            suggestion.setDimension(issue.getDimension());
            suggestion.setDescription(issue.getDescription());
            suggestion.setSuggestion(issue.getSuggestion());
            suggestions.add(suggestion);
        }

        return suggestions;
    }

    @Override
    public List<CompletenessDimensionConfig> getDimensionConfigs() {
        return dimensionConfigs;
    }

    @Override
    public void setDimensionConfigs(List<CompletenessDimensionConfig> configs) {
        this.dimensionConfigs = configs != null ? configs : createDefaultDimensionConfigs();
    }

    private List<CompletenessDimensionConfig> createDefaultDimensionConfigs() {
        List<CompletenessDimensionConfig> configs = new ArrayList<>();

        configs.add(createDimensionConfig("基础信息", 15,
                Arrays.asList(
                        createCheckItem("名称", "name", 25),
                        createCheckItem("描述", "description", 25),
                        createCheckItem("分类", "category", 25),
                        createCheckItem("标签", "tags", 25)
                )));

        configs.add(createDimensionConfig("角色定义", 25,
                Arrays.asList(
                        createCheckItem("角色列表", "roles", 100)
                )));

        configs.add(createDimensionConfig("流程步骤", 25,
                Arrays.asList(
                        createCheckItem("步骤列表", "steps", 100)
                )));

        configs.add(createDimensionConfig("约束规则", 15,
                Arrays.asList(
                        createCheckItem("规则列表", "rules", 100)
                )));

        configs.add(createDimensionConfig("能力要求", 10,
                Arrays.asList(
                        createCheckItem("必需能力", "requiredCapabilities", 100)
                )));

        configs.add(createDimensionConfig("知识库", 10,
                Arrays.asList(
                        createCheckItem("知识库关联", "knowledgeBaseIds", 100)
                )));

        return configs;
    }

    private CompletenessDimensionConfig createDimensionConfig(String name, int weight, 
            List<CompletenessCheckItem> checkItems) {
        CompletenessDimensionConfigEntity config = new CompletenessDimensionConfigEntity();
        config.setName(name);
        config.setWeight(weight);
        config.setCheckItems(checkItems);
        return config;
    }

    private CompletenessCheckItem createCheckItem(String name, String checkExpression, int score) {
        CompletenessCheckItemEntity item = new CompletenessCheckItemEntity();
        item.setName(name);
        item.setCheckExpression(checkExpression);
        item.setScore(score);
        return item;
    }
}
