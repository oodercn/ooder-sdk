package net.ooder.sdk.llm.tool;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 工具定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolDefinition {

    /**
     * 工具ID
     */
    private String toolId;

    /**
     * 工具名称
     */
    private String name;

    /**
     * 工具描述
     */
    private String description;

    /**
     * 参数Schema (JSON Schema格式)
     */
    private Map<String, Object> parametersSchema;

    /**
     * 处理器类名
     */
    private String handlerClass;

    /**
     * 工具分类
     */
    private String category;

    /**
     * 是否启用
     */
    @Builder.Default
    private boolean enabled = true;

    /**
     * 超时时间(毫秒)
     */
    @Builder.Default
    private long timeout = 30000;
}
