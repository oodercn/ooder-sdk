package net.ooder.scene.skill.tool;

import java.util.List;
import java.util.Map;

/**
 * 工具接口
 *
 * @author Ooder Team
 * @version 2.3.1
 */
public interface Tool {

    /**
     * 获取工具ID
     * @return 工具ID
     */
    String getId();

    /**
     * 获取工具名称
     * @return 工具名称
     */
    String getName();

    /**
     * 获取工具描述
     * @return 工具描述
     */
    String getDescription();

    /**
     * 获取工具类别
     * @return 工具类别
     */
    default String getCategory() {
        return "general";
    }

    /**
     * 获取工具标签
     * @return 工具标签列表
     */
    default List<String> getTags() {
        return new java.util.ArrayList<>();
    }

    /**
     * 获取工具参数定义
     * @return 参数定义
     */
    Map<String, Object> getParameters();

    /**
     * 获取参数模式（JSON Schema）
     * @return 参数模式
     */
    default Map<String, Object> getParametersSchema() {
        return getParameters();
    }

    /**
     * 验证参数
     * @param arguments 参数
     * @return 验证结果
     */
    default ToolResult validateArguments(Map<String, Object> arguments) {
        return ToolResult.success(arguments);
    }

    /**
     * 执行工具
     * @param parameters 参数
     * @param context 上下文
     * @return 执行结果
     */
    ToolResult execute(Map<String, Object> parameters, ToolContext context);
}
