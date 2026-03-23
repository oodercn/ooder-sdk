package net.ooder.sdk.llm.activation;

import java.util.List;

/**
 * 激活引导服务接口
 *
 * @version 2.3.1
 * @since 2.3.1
 */
public interface ActivationGuidanceService {

    /**
     * 获取下一步操作建议
     *
     * @param context 激活上下文
     * @return 操作建议
     */
    GuidanceResult getNextStepGuidance(ActivationContext context);

    /**
     * 验证用户输入
     *
     * @param stepId 步骤ID
     * @param userInput 用户输入
     * @return 验证结果
     */
    ValidationResult validateUserInput(String stepId, Object userInput);

    /**
     * 生成智能填充建议
     *
     * @param fieldPath 字段路径
     * @param context 上下文
     * @return 填充建议
     */
    List<Suggestion> generateSuggestions(String fieldPath, ActivationContext context);

    /**
     * 获取步骤引导文本
     *
     * @param stepId 步骤ID
     * @param context 上下文
     * @return 引导文本
     */
    String getStepGuidanceText(String stepId, ActivationContext context);

    /**
     * 检查是否可以跳过步骤
     *
     * @param stepId 步骤ID
     * @param context 上下文
     * @return 是否可跳过
     */
    boolean canSkipStep(String stepId, ActivationContext context);

    /**
     * 获取推荐选项
     *
     * @param stepId 步骤ID
     * @param context 上下文
     * @return 推荐选项列表
     */
    List<GuidanceResult.Option> getRecommendedOptions(String stepId, ActivationContext context);
}
