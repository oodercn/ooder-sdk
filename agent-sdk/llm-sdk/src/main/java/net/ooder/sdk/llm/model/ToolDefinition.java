package net.ooder.sdk.llm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 工具定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolDefinition {
    
    /**
     * 类型 (function)
     */
    private String type;
    
    /**
     * 函数定义
     */
    private FunctionDefinition function;
}
