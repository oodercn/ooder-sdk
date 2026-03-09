package net.ooder.scene.skill.tool;

import java.util.List;
import java.util.Map;

/**
 * 工具定义接口
 *
 * <p>定义可被 LLM Function Calling 调用的工具。</p>
 *
 * <p>架构层次：应用层 - 智能增强</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface Tool {
    
    /**
     * 获取工具名称
     */
    String getName();
    
    /**
     * 获取工具描述
     */
    String getDescription();
    
    /**
     * 获取工具参数定义（JSON Schema 格式）
     */
    Map<String, Object> getParametersSchema();
    
    /**
     * 执行工具
     *
     * @param arguments 参数（JSON 对象）
     * @param context 执行上下文
     * @return 执行结果
     */
    ToolResult execute(Map<String, Object> arguments, ToolExecutionContext context);
    
    /**
     * 验证参数
     *
     * @param arguments 参数
     * @return 验证结果
     */
    default ValidationResult validateArguments(Map<String, Object> arguments) {
        return ValidationResult.success();
    }
    
    /**
     * 是否需要用户确认
     */
    default boolean requiresConfirmation() {
        return false;
    }
    
    /**
     * 是否为只读操作
     */
    default boolean isReadOnly() {
        return true;
    }
    
    /**
     * 获取工具类别
     */
    default String getCategory() {
        return "general";
    }
    
    /**
     * 获取工具标签
     */
    default List<String> getTags() {
        return java.util.Collections.emptyList();
    }
}
